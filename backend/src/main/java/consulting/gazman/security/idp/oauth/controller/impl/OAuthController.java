package consulting.gazman.security.idp.oauth.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.common.dto.ApiError;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.MfaService;
import consulting.gazman.security.idp.model.OAuthFlowData;
import consulting.gazman.security.idp.model.OAuthSession;
import consulting.gazman.security.idp.oauth.controller.IOAuthController;
import consulting.gazman.security.idp.oauth.dto.*;
import consulting.gazman.security.idp.oauth.service.JwtService;
import consulting.gazman.security.idp.oauth.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController

public class OAuthController  extends ApiController implements IOAuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    MfaService mfaService;
    @Autowired private JwtService jwtService;
    private final RedisTemplate<String, OAuthSession> sessionRedisTemplate;
    private final RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate;
    private ObjectMapper objectMapper; // Inject ObjectMapper

    public OAuthController(RedisTemplate<String, OAuthSession> sessionRedisTemplate, RedisTemplate<String, OAuthSession> flowDataRedisTemplate, RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate1) {
        this.sessionRedisTemplate = sessionRedisTemplate;
        this.flowDataRedisTemplate = flowDataRedisTemplate1;
    }

    public ResponseEntity<?> authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String scope,
            @RequestParam String state,
            @RequestParam(required = false) String code_challenge,
            @RequestParam(required = false) String code_challenge_method,
            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken
    ) {
        OAuthSession session;
        if (sessionToken == null || sessionToken.isEmpty()) {
            // Create new session with a dedicated oauthSessionId
            String sessionId = UUID.randomUUID().toString();
            session = OAuthSession.builder()
                    .clientId(client_id)
                    .oauthSessionId(sessionId)

                    .build();
            sessionRedisTemplate.opsForValue().set("oauth:session:" + sessionId, session);

            // Store OAuth flow data using the state as key
            OAuthFlowData flowData = OAuthFlowData.builder()
                    .clientId(client_id)
                    .redirectUri(redirect_uri)
                    .responseType(response_type)
                    .scope(scope)
                    .codeChallenge(code_challenge)
                    .codeChallengeMethod(code_challenge_method)
                    .createdAt(Instant.now())
                    .build();
            flowDataRedisTemplate.opsForValue().set("oauth:flow:" + state, flowData);


//            OAuthFlowData x = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + state);
//            OAuthSession y = sessionRedisTemplate.opsForValue().get("oauth:session:" + sessionId);
            String newToken = jwtService.generateSessionToken(session);
            ResponseCookie cookie = ResponseCookie.from("OAUTH_SESSION", newToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(true) // Set to true if using HTTPS
                    .maxAge(Duration.ofHours(1))
                    .build();
            URI location = UriComponentsBuilder.fromUriString("/login")
                    .queryParam("state", state)
                    .build()
                    .toUri();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(location)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();
        }

        // Parse session and retrieve flow data using the state key
        session = jwtService.parseSessionToken(sessionToken);
        OAuthFlowData flowData = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + state);

        // Validate state and other flow parameters
        if (state == null || flowDataRedisTemplate.opsForValue().get("oauth:flow:" + state) == null) {
            throw AppException.sessionException("Invalid or expired state");
        }

        if (flowData == null ||
                !flowData.getRedirectUri().equals(redirect_uri) ||
                !flowData.getScope().equals(scope) ||
                !flowData.getResponseType().equals(response_type)) {
            throw AppException.oauthException("Invalid OAuth parameters");
        }

        AuthorizeResponse authResponse = oAuthService.generateAuthCode(
                AuthorizeRequest.builder()
                        .email(session.getEmail())
                        .responseType(response_type)
                        .clientId(client_id)
                        .redirectUri(redirect_uri)
                        .scope(scope)
                        .codeChallenge(code_challenge)
                        .codeChallengeMethod(code_challenge_method)
                        .build()
        );


        String redirectUrl = UriComponentsBuilder.fromUriString(redirect_uri)
                .queryParam("code", authResponse.getCode())
                .queryParam("state", state)
                .build()
                .toUriString();

        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

@Override
public ResponseEntity<?> token(@RequestBody TokenRequest request,
                               @CookieValue(name = "OAUTH_SESSION") String sessionToken) {
    try {
        TokenResponse tokenResponse = handleGrantType(request);

        // Clean up Redis data after successful token exchange
        OAuthSession session = jwtService.parseSessionToken(sessionToken);
        if (session != null) {
            sessionRedisTemplate.delete("oauth:session:" + session.getOauthSessionId());
        }
        flowDataRedisTemplate.delete("oauth:flow:" + request.getState());

        return ResponseEntity.ok(tokenResponse);
    } catch (AppException e) {
        return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        return wrapErrorResponse("INTERNAL_SERVER_ERROR",
                "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    private TokenResponse handleGrantType(TokenRequest request) {
        return switch (request.getGrantType()) {
            case "authorization_code" -> handleAuthorizationCode(request);
            case "refresh_token" -> handleRefreshToken(request);
            default -> throw new AppException("UNSUPPORTED_GRANT_TYPE",
                    "Grant type '" + request.getGrantType() + "' not supported");
        };
    }

    private TokenResponse handleAuthorizationCode(TokenRequest request) {
        validateAuthCodeRequest(request);

        OAuthFlowData flowData = flowDataRedisTemplate.opsForValue()
                .get("oauth:flow:" + request.getState());

        if (flowData == null) {
            throw new AppException("INVALID_STATE", "State not found or expired");
        }

        verifyCodeChallenge(request.getCodeVerifier(), flowData.getCodeChallenge());

        return oAuthService.exchangeToken(request);
    }

    private void verifyCodeChallenge(String verifier, String storedChallenge) {
        String computedChallenge = generateCodeChallenge(verifier);
        if (!computedChallenge.equals(storedChallenge)) {
            throw new AppException("INVALID_GRANT", "Code verifier mismatch");
        }
    }
    private String generateCodeChallenge(String verifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new AppException("INTERNAL_ERROR", "Failed to generate code challenge");
        }
    }
    private TokenResponse handleRefreshToken(TokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            throw new AppException("INVALID_REQUEST", "Refresh token is required");
        }
        return oAuthService.refreshToken(request);
    }

    private void validateAuthCodeRequest(TokenRequest request) {
        if (request.getState() == null || request.getState().isEmpty()) {
            throw new AppException("INVALID_REQUEST", "State parameter is required");
        }
        if (request.getCode() == null || request.getCode().isEmpty()) {
            throw new AppException("INVALID_REQUEST", "Authorization code is required");
        }
        if (request.getCodeVerifier() == null || request.getCodeVerifier().isEmpty()) {
            throw new AppException("INVALID_REQUEST", "Code verifier is required");
        }
    }
    @Override
    public ResponseEntity<?> introspect(@RequestBody String bearerToken) {
        try {
            // Validate and parse the token using your service
            IntrospectResponse response = oAuthService.introspectToken(bearerToken);

            // Return the introspection response
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @Override
    public ResponseEntity<?> revokeToken(@RequestBody String refreshToken) {
        try {
            // Attempt to revoke the token using your service
            oAuthService.revokeToken(refreshToken);

            // Return a success response
            return ResponseEntity.ok("Token successfully revoked.");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @Override
    public ResponseEntity<?> userinfo(@RequestHeader("Authorization") String bearerToken) {
        try {
            UserInfoResponse response = oAuthService.getUserInfo(bearerToken);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    private String generateAuthorizationCode(String responseType, String clientId, String redirectUri, String scope, String state) {
        return oAuthService.generateAuthCode(AuthorizeRequest.builder()
                .responseType(responseType)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .scope(scope)
                .state(state)
                .build()).getCode();
    }

}
