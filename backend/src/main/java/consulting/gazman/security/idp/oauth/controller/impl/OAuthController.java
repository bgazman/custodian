package consulting.gazman.security.idp.oauth.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController

public class OAuthController implements IOAuthController {

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
                    .build();
            sessionRedisTemplate.opsForValue().set("oauth:session:" + sessionId, session);

            // Store OAuth flow data using the state as key
            OAuthFlowData flowData = OAuthFlowData.builder()
                    .state(state)
                    .clientId(client_id)
                    .redirectUri(redirect_uri)
                    .responseType(response_type)
                    .scope(scope)
                    .codeChallenge(code_challenge)
                    .codeChallengeMethod(code_challenge_method)
                    .createdAt(Instant.now())
                    .build();
            flowDataRedisTemplate.opsForValue().set("oauth:flow:" + state, flowData);

            // Redirect to login page
            String newToken = jwtService.generateSessionToken(session);
            ResponseCookie cookie = ResponseCookie.from("OAUTH_SESSION", newToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(true) // Set to true if using HTTPS
                    .maxAge(Duration.ofHours(1))
                    .build();
            URI location = UriComponentsBuilder.fromUriString("/login").build().toUri();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(location)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();
        }

        // Parse session and retrieve flow data using the state key
        session = jwtService.parseSessionToken(sessionToken);
        OAuthFlowData flowData = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + state);

        // Validate state and other flow parameters
        if (flowData == null ||
                !flowData.getState().equals(state) ||
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

        // Clean up Redis data after successful authorization
        sessionRedisTemplate.delete("oauth:session:" + session.getOauthSessionId());
        flowDataRedisTemplate.delete("oauth:flow:" + state);

        String redirectUrl = UriComponentsBuilder.fromUriString(redirect_uri)
                .queryParam("code", authResponse.getCode())
                .queryParam("state", state)
                .build()
                .toUriString();

        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    @Override
    public ResponseEntity<?> token(@RequestBody TokenRequest request,
                                   @CookieValue(name = "OAUTH_SESSION") String sessionToken
    ) {
        try {
            return ResponseEntity.ok(switch (request.getGrantType()) {
                case "authorization_code" -> oAuthService.exchangeToken(request);
                case "refresh_token" -> {
                    if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
                        throw new AppException("INVALID_REQUEST", "Refresh token is required");
                    }
                    yield oAuthService.refreshToken(request);
                }
                default -> throw new AppException("UNSUPPORTED_GRANT_TYPE",
                        "Grant type '" + request.getGrantType() + "' not supported");
            });
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiError.builder()
                            .code(e.getErrorCode())
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiError.builder()
                            .code("INTERNAL_SERVER_ERROR")
                            .message(e.getMessage())
                            .build());
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
            // Handle application-specific exceptions
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.builder().code(e.getErrorCode()).message(e.getMessage()).build());
        } catch (Exception e) {
            // Handle generic exceptions
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.builder().code("INTERNAL_SERVER_ERROR").message(e.getMessage()).build());
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
            // Handle application-specific exceptions
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.builder().code(e.getErrorCode()).message(e.getMessage()).build());
        } catch (Exception e) {
            // Handle generic exceptions
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.builder().code("INTERNAL_SERVER_ERROR").message(e.getMessage()).build());
        }
    }

    @Override
    public ResponseEntity<?> userinfo(@RequestHeader("Authorization") String bearerToken) {
        try {
            UserInfoResponse response = oAuthService.getUserInfo(bearerToken);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.builder().code(e.getErrorCode()).message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.builder().code("INTERNAL_SERVER_ERROR").message(e.getMessage()).build());
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
