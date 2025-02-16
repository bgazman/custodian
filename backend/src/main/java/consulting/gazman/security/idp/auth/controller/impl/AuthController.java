package consulting.gazman.security.idp.auth.controller.impl;

import consulting.gazman.security.client.user.service.UserService;
import consulting.gazman.security.common.config.RedisSessionConfig;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.auth.controller.IAuthController;
import consulting.gazman.security.idp.auth.dto.*;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.MfaService;
import consulting.gazman.security.idp.auth.service.impl.AuthServiceImpl;
import consulting.gazman.security.idp.auth.service.impl.EmailVerificationServiceImpl;
import consulting.gazman.security.idp.model.OAuthSession;
import consulting.gazman.security.idp.oauth.service.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import consulting.gazman.security.idp.oauth.service.AuthCodeService;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@Slf4j
@RestController
public class AuthController extends ApiController implements IAuthController {
    private final AuthCodeService authCodeService;
    private final EmailVerificationServiceImpl emailVerificationServiceImpl;
    private final AuthService authService;
    private final UserService userService;
    private final MfaService mfaService;
    private final JwtService jwtService;
    private final RedisTemplate<String, OAuthSession> sessionRedisTemplate;
    public AuthController(AuthCodeService authCodeService, EmailVerificationServiceImpl emailVerificationServiceImpl, AuthServiceImpl authService, UserService userService, MfaService mfaService, JwtService jwtService, RedisSessionConfig redisSessionConfig, RedisTemplate<String, OAuthSession> sessionRedisTemplate) {
        this.authCodeService = authCodeService;
        this.emailVerificationServiceImpl = emailVerificationServiceImpl;
        this.authService = authService;
        this.userService = userService;
        this.mfaService = mfaService;
        this.jwtService = jwtService;
        this.sessionRedisTemplate = sessionRedisTemplate;
    }

    private static final String ERROR_PATH = "/login";
    private static final String MFA_PATH = "/mfa";

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
            // Perform login
            LoginResponse loginResponse = authService.login(loginRequest);

            // Update session with validated email and clientId
            oauthSession.setEmail(sanitizeEmail(loginRequest.getEmail()));

            // Handle MFA if enabled
            if (loginResponse.isMfaEnabled()) {
                return handleMfaFlow(oauthSession, loginResponse);
            }

            // Direct auth flow: regenerate token and redirect appropriately
            String updatedToken = jwtService.generateSessionToken(oauthSession);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(buildRedirectUri(buildAuthorizeRedirect()))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return handleUnexpectedError();
        }
    }







    private OAuthSession validateAndGetSession(String sessionToken) {
        OAuthSession session = jwtService.parseSessionToken(sessionToken);
        if (session == null || !session.isValid()) {
            throw AppException.sessionException("Invalid or expired session");
        }
        return session;
    }

    private ResponseEntity<?> handleMfaFlow(OAuthSession session, LoginResponse loginResponse) {
        MfaInitiationResult mfaResult = mfaService.initiateMfaChallenge(
                session,
                loginResponse.getMfaMethod()
        );

        if (!mfaResult.isSuccess()) {
            throw AppException.mfaException("MFA initialization failed: " + mfaResult.getErrorMessage());
        }

        session.initiateMfa(mfaResult.getMethod());
        String updatedToken = jwtService.generateSessionToken(session);

        return ResponseEntity.ok(buildMfaResponse(updatedToken, mfaResult));
    }

    private Map<String, Object> buildMfaResponse(String sessionToken, MfaInitiationResult mfaResult) {
        String redirectUrl = UriComponentsBuilder.fromPath(MFA_PATH)
                .build().toUriString();

        return Map.of(
                "redirectUrl", redirectUrl,
                "mfaMethod", mfaResult.getMethod(),
                "challengeId", mfaResult.getChallengeId()
        );
    }

    private ResponseEntity<?> handleAuthError(AuthenticationException e, String sessionToken) {
        String errorRedirect = UriComponentsBuilder.fromPath(ERROR_PATH)
                .queryParam("error", sanitizeErrorMessage(e.getMessage()))
                .queryParam("sessionToken", sessionToken)
                .build().toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(buildRedirectUri(errorRedirect))
                .build();
    }

    private ResponseEntity<?> handleUnexpectedError() {
        String errorRedirect = UriComponentsBuilder.fromPath(ERROR_PATH)
                .queryParam("error", "An unexpected error occurred")
                .build().toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(buildRedirectUri(errorRedirect))
                .build();
    }

    private URI buildRedirectUri(String path) {
        return URI.create(path);
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
    // Helper method for building authorize redirect URL
    private String buildAuthorizeRedirect() {
        return UriComponentsBuilder.fromPath("/oauth/authorize")
                .build().toUriString();
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