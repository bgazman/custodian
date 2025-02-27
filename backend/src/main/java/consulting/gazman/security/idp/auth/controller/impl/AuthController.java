package consulting.gazman.security.idp.auth.controller.impl;

import consulting.gazman.security.idp.oauth.dto.TokenRequest;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.service.OAuthClientService;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.user.service.UserConsentService;
import consulting.gazman.security.user.service.UserService;
import consulting.gazman.security.common.config.RedisSessionConfig;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.auth.controller.IAuthController;
import consulting.gazman.security.idp.auth.dto.*;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.MfaService;
import consulting.gazman.security.idp.auth.service.impl.AuthServiceImpl;
import consulting.gazman.security.idp.auth.service.impl.EmailVerificationServiceImpl;
import consulting.gazman.security.idp.model.OAuthFlowData;
import consulting.gazman.security.idp.model.OAuthSession;
import consulting.gazman.security.idp.oauth.service.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import consulting.gazman.security.idp.oauth.service.AuthCodeService;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
public class AuthController extends ApiController implements IAuthController {
    private final EmailVerificationServiceImpl emailVerificationServiceImpl;
    private final AuthService authService;
    private final MfaService mfaService;
    private final JwtService jwtService;
    private final RedisTemplate<String, OAuthSession> sessionRedisTemplate;
    private final RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate;

    public AuthController(AuthCodeService authCodeService, EmailVerificationServiceImpl emailVerificationServiceImpl, AuthServiceImpl authService, UserService userService, MfaService mfaService, JwtService jwtService, RedisSessionConfig redisSessionConfig, RedisTemplate<String, OAuthSession> sessionRedisTemplate, RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate, RedisTemplate<String, TokenRequest> tokenRequestRedisTemplate, OAuthClientService oAuthClientService, UserConsentService userConsentService) {
        this.emailVerificationServiceImpl = emailVerificationServiceImpl;
        this.authService = authService;
        this.mfaService = mfaService;
        this.jwtService = jwtService;
        this.sessionRedisTemplate = sessionRedisTemplate;
        this.flowDataRedisTemplate = flowDataRedisTemplate;
    }

    private static final String MFA_PATH = "/mfa";
    private static final String AUTHORIZATION_PATH = "/oauth/authorize";
    @Override
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        try {
            authService.registerUser(request);
            return wrapSuccessResponse(null, "Registration successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            emailVerificationServiceImpl.validateVerificationToken(token);
            return wrapSuccessResponse(null, "Email verified successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest loginRequest,
            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken
    ) {
        try {
            OAuthSession oauthSession = jwtService.parseSessionToken(sessionToken);
            if (oauthSession == null || !oauthSession.isValid()) {
                throw AppException.sessionException("Invalid or expired session");
            }
            String state = loginRequest.getState();
            OAuthFlowData flowData = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + loginRequest.getState());
            if (flowData == null) {
                return wrapErrorResponse("INVALID_FLOW", "Invalid OAuth flow", HttpStatus.BAD_REQUEST);
            }

            // Perform login
            LoginResponse loginResponse = authService.login(loginRequest);

            // Update session with validated email and clientId
            oauthSession.setEmail(sanitizeEmail(loginRequest.getEmail()));
            String oauthSessionId = oauthSession.getOauthSessionId();
            sessionRedisTemplate.opsForValue().set("oauth:session:" + oauthSessionId, oauthSession);

            // Handle MFA if enabled
            if (loginResponse.isMfaEnabled()) {
                return handleMfaFlow(oauthSession, loginResponse,state);
            }
            String updatedToken = jwtService.generateSessionToken(oauthSession);
            ResponseCookie cookie = ResponseCookie.from("OAUTH_SESSION", updatedToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(true) // Set to true if using HTTPS
                    .maxAge(Duration.ofHours(1))
                    .build();
            // Redirect back to authorize endpoint with verified session
            URI location = UriComponentsBuilder.fromPath(AUTHORIZATION_PATH)
                    .queryParam("response_type", flowData.getResponseType())
                    .queryParam("client_id", flowData.getClientId())
                    .queryParam("redirect_uri", flowData.getRedirectUri())
                    .queryParam("scope", flowData.getScope())
                    .queryParam("state", loginRequest.getState())
                    .queryParam("code_challenge", flowData.getCodeChallenge())
                    .queryParam("code_challenge_method", flowData.getCodeChallengeMethod())
                    .build().toUri();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(location)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();
        } catch (AppException e) {
            log.error("Login failed: {}", e.getMessage());
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }




    private ResponseEntity<?> handleMfaFlow(OAuthSession oauthSession, LoginResponse loginResponse,String state) {
        MfaInitiationResult mfaResult = mfaService.initiateMfaChallenge(
                oauthSession,
                loginResponse.getMfaMethod()
        );

        if (!mfaResult.isSuccess()) {
            throw AppException.mfaException("MFA initialization failed: " + mfaResult.getErrorMessage());
        }

        oauthSession.initiateMfa(mfaResult.getMethod());
        sessionRedisTemplate.opsForValue().set("oauth:session:" + oauthSession.getOauthSessionId(), oauthSession);
        String updatedToken = jwtService.generateSessionToken(oauthSession);

        ResponseCookie cookie = ResponseCookie.from("OAUTH_SESSION", updatedToken)
                        .path("/")
                        .httpOnly(true)
                        .secure(true) // Set to true if using HTTPS
                        .maxAge(Duration.ofHours(1))
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(buildMfaResponse(mfaResult, state));
    }

    private Map<String, Object> buildMfaResponse( MfaInitiationResult mfaResult,String state) {
        String redirectUrl = UriComponentsBuilder.fromPath(MFA_PATH)
                .queryParam("state", state)
                .build().toUriString();

        return Map.of(
                "redirectUrl", redirectUrl,
                "mfaMethod", mfaResult.getMethod(),
                "challengeId", mfaResult.getChallengeId()
        );
    }





    private String sanitizeEmail(String email) {
        return email != null ? email.toLowerCase().trim() : "";
    }

    private String sanitizeErrorMessage(String message) {
        // Implement appropriate error message sanitization
        return message != null ? message.replaceAll("[^a-zA-Z0-9_\\s-]", "") : "Invalid request";
    }

    private String maskSessionId(String sessionToken) {
        if (sessionToken == null || sessionToken.length() < 8) {
            return "invalid-token";
        }
        return sessionToken.substring(0, 4) + "..." +
                sessionToken.substring(sessionToken.length() - 4);
    }



    @Override
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        try {
            return wrapSuccessResponse(null, "Logout successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}