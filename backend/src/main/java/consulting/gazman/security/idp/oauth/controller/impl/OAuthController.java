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
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.service.AuthCodeService;
import consulting.gazman.security.idp.oauth.service.JwtService;
import consulting.gazman.security.idp.oauth.service.OAuthClientService;
import consulting.gazman.security.idp.oauth.service.OAuthService;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.user.service.UserConsentService;
import consulting.gazman.security.user.service.UserService;
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
import java.util.*;

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
    private final RedisTemplate<String, TokenRequest> tokenRequestRedisTemplate;
    private final OAuthClientService oAuthClientService;
    private final UserConsentService userConsentService;
    private final UserService userService;
    private final AuthCodeService authCodeService;
    private static final String LOGIN_PATH = "/login";
    private static final String CONSENT_PATH = "/consent";
    public OAuthController(RedisTemplate<String, OAuthSession> sessionRedisTemplate, RedisTemplate<String, OAuthSession> flowDataRedisTemplate, RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate1, RedisTemplate<String, TokenRequest> tokenRequestRedisTemplate, OAuthClientService oAuthClientService, UserConsentService userConsentService, UserService userService, AuthCodeService authCodeService) {
        this.sessionRedisTemplate = sessionRedisTemplate;
        this.flowDataRedisTemplate = flowDataRedisTemplate1;
        this.tokenRequestRedisTemplate = tokenRequestRedisTemplate;
        this.oAuthClientService = oAuthClientService;
        this.userConsentService = userConsentService;
        this.userService = userService;
        this.authCodeService = authCodeService;
    }

    public ResponseEntity<?> authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam(required = false) String scope,
            @RequestParam String state,
            @RequestParam(required = false) String code_challenge,
            @RequestParam(required = false) String code_challenge_method,
            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken
    ) {
        OAuthSession session;
        boolean isAuthenticated = false;

        // Fetch the client details
        OAuthClient client = oAuthClientService.getClientByClientId(client_id)
                .orElseThrow(() -> AppException.invalidClientId("Client not found"));

        // Apply default scopes if no scopes are requested
        if (scope == null || scope.isEmpty()) {
            scope = String.join(" ", client.getDefaultScopes()); // Assuming getDefaultScopes() returns a List<String>
        }

        // Validate requested scopes against allowed scopes
        if (scope != null && !scope.isEmpty()) {
            List<String> requestedScopes = Arrays.asList(scope.split(" "));
            List<String> allowedScopes = client.getAllowedScopes(); // Assuming getAllowedScopes() returns a List<String>

            if (!allowedScopes.containsAll(requestedScopes)) {
                // Return an error response if requested scopes are not allowed
                return ResponseEntity.badRequest().body(Map.of("error", "invalid_scope", "error_description", "Requested scopes are not allowed"));
            }
        }

        // Check if there's a valid session token that has user authentication
        if (sessionToken != null && !sessionToken.isEmpty()) {
            try {
                session = jwtService.parseSessionToken(sessionToken);
                if (session.getEmail() != null) {
                    isAuthenticated = true;

                    // Store updated flow data with the state
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

                    // Check user consent
                    User user = userService.findByEmail(session.getEmail());

                    if (!userConsentService.hasValidConsent(user.getId(), client.getId(), scope.split(" "))) {
                        // Redirect to consent page
                        return ResponseEntity.status(HttpStatus.FOUND)
                                .location(UriComponentsBuilder.fromUriString(CONSENT_PATH)
                                        .queryParam("state", state)
                                        .build()
                                        .toUri())
                                .build();
                    }

                    // User has consent, generate auth code
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

                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create(redirectUrl))
                            .build();
                }
            } catch (Exception e) {
                // Invalid token, will create new session below
                log.warn("Invalid session token: {}", e.getMessage());
            }
        }

        if (!isAuthenticated) {
            // Check if this is a returning flow with existing data
            OAuthFlowData existingFlow = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + state);
            if (existingFlow != null && existingFlow.getClientId().equals(client_id)) {
                // User has left consent page and returned to login
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(UriComponentsBuilder.fromUriString(LOGIN_PATH)
                                .queryParam("state", state)
                                .build()
                                .toUri())
                        .build();
            }
        }

        String sessionId = UUID.randomUUID().toString();
        session = OAuthSession.builder()
                .clientId(client_id)
                .oauthSessionId(sessionId)
                .build();

        sessionRedisTemplate.opsForValue().set("oauth:session:" + sessionId, session);

        // Store OAuth flow data
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

        String newToken = jwtService.generateSessionToken(session);
        ResponseCookie cookie = ResponseCookie.from("OAUTH_SESSION", newToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1))
                .build();

        URI location = UriComponentsBuilder.fromUriString(LOGIN_PATH)
                .queryParam("state", state)
                .build()
                .toUri();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(location)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
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
    @Override
    public ResponseEntity<?> getConsentData(@RequestParam String state,
                                            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            throw AppException.sessionException("No valid session found");
        }

        OAuthFlowData flowData = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + state);

        if (flowData == null) {
            throw AppException.sessionException("Invalid or expired state");
        }

        OAuthClient client = oAuthClientService.getClientByClientId(flowData.getClientId())
                .orElseThrow(() -> AppException.invalidClientId("Client not found"));

        Map<String, Object> consentData = new HashMap<>();
        consentData.put("clientName", client.getName());
        consentData.put("scopes", flowData.getScope().split(" "));
        consentData.put("state", state);

        return ResponseEntity.ok(consentData);
    }

    @Override
    public ResponseEntity<?> approveConsent(@RequestBody Map<String, Object> request,
                                            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            throw AppException.sessionException("No valid session found");
        }

        String state = (String) request.get("state");
        @SuppressWarnings("unchecked")
        List<String> approvedScopes = (List<String>) request.get("approvedScopes");

        OAuthSession session = jwtService.parseSessionToken(sessionToken);
        OAuthFlowData flowData = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + state);

        if (flowData == null) {
            throw AppException.sessionException("Invalid or expired state");
        }

        User user = userService.findByEmail(session.getEmail());
        OAuthClient client = oAuthClientService.getClientByClientId(flowData.getClientId())
                .orElseThrow(() -> AppException.invalidClientId("Client not found"));

        userConsentService.saveConsent(user.getId(), client.getId(), approvedScopes);

        String redirectUrl = UriComponentsBuilder.fromUriString(flowData.getRedirectUri())
                .queryParam("code", authCodeService.generateCode(user.getEmail(), flowData.getClientId()))
                .queryParam("state", state)
                .build()
                .toUriString();

        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }
}
