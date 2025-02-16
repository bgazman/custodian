package consulting.gazman.security.idp.auth.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.auth.controller.IMfaController;
import consulting.gazman.security.idp.auth.dto.*;
import consulting.gazman.security.idp.model.OAuthFlowData;
import consulting.gazman.security.idp.model.OAuthSession;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.MfaService;

import consulting.gazman.security.idp.oauth.service.JwtService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@RestController
public class MfaController extends ApiController implements IMfaController {
    private final MfaService mfaService;
    private final JwtService jwtService;
    private final RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate;
    private static final String AUTHORIZATION_PATH = "/oauth/authorize";
    private final ObjectMapper objectMapper;

    public MfaController(MfaService mfaService, JwtService jwtService, RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate, ObjectMapper objectMapper) {
        this.mfaService = mfaService;
        this.jwtService = jwtService;
        this.flowDataRedisTemplate = flowDataRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<?> resendCode(@RequestBody MfaRequest mfaRequest,
                                        @CookieValue(name = "OAUTH_SESSION") String sessionToken
    ) {
        try {
            OAuthSession oauthSession = jwtService.parseSessionToken(sessionToken);
            if (oauthSession == null) {
                return wrapErrorResponse("INVALID_SESSION", "Invalid session", HttpStatus.BAD_REQUEST);
            }

            MfaResendResult result = mfaService.resendMfaCode(oauthSession, mfaRequest);

            if (result.isSuccess()) {
                return wrapSuccessResponse(
                        Map.of(
                                "message", "Verification code resent successfully",
                                "nextAllowedAttempt", result.getNextAllowedAttempt(),
                                "remainingAttempts", result.getRemainingResendAttempts()
                        ),
                        "Code resent successfully"
                );
            } else {
                return wrapErrorResponse(
                        "RESEND_LIMIT_EXCEEDED",
                        result.getErrorMessage(),
                        HttpStatus.TOO_MANY_REQUESTS
                );
            }
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> verifyRecoveryCode(@RequestBody MfaRequest mfaRequest,
                                              @CookieValue(name = "OAUTH_SESSION") String sessionToken
    ) {
        try {
            OAuthSession oauthSession = jwtService.parseSessionToken(sessionToken);
            if (oauthSession == null) {
                return wrapErrorResponse("INVALID_SESSION", "Invalid session", HttpStatus.BAD_REQUEST);
            }

            RecoveryCodeValidationResult result = mfaService.validateRecoveryCode(oauthSession, mfaRequest);

            if (!result.isValid()) {
                return wrapErrorResponse(
                        "INVALID_RECOVERY_CODE",
                        result.getErrorMessage(),
                        HttpStatus.UNAUTHORIZED
                );
            }

            return wrapSuccessResponse(
                    Map.of(
                            "message", "Recovery code validated successfully",
                            "remainingCodes", result.getRemainingCodes()
                    ),
                    "Recovery code validated successfully"
            );
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> initiateMfa(@CookieValue(name = "OAUTH_SESSION") String sessionToken
    ) {
        try {
            OAuthSession oauthSession = jwtService.parseSessionToken(sessionToken);
            if (oauthSession == null) {
                return wrapErrorResponse("INVALID_SESSION", "Invalid session", HttpStatus.BAD_REQUEST);
            }

            MfaInitiationResult result = mfaService.initiateMfaChallenge(oauthSession, oauthSession.getMfaMethod());

            if (!result.isSuccess()) {
                return wrapErrorResponse(
                        "MFA_INITIATION_FAILED",
                        result.getErrorMessage(),
                        HttpStatus.BAD_REQUEST
                );
            }

            // Update session with MFA status
            oauthSession.setMfaInitiated(true);
            String updatedToken = jwtService.generateSessionToken(oauthSession);
            ResponseCookie cookie = ResponseCookie.from("OAUTH_SESSION", updatedToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(true) // Set to true if using HTTPS
                    .maxAge(Duration.ofHours(1))
                    .build();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> verifyMfa(
            @RequestBody MfaRequest request,
            @CookieValue(name = "OAUTH_SESSION") String sessionToken
    ) {
        try {
            OAuthSession oauthSession = jwtService.parseSessionToken(sessionToken);
            if (oauthSession == null) {
                return wrapErrorResponse("INVALID_SESSION", "Invalid session", HttpStatus.BAD_REQUEST);
            }

            // Get flow data from Redis
            OAuthFlowData flowData = flowDataRedisTemplate.opsForValue().get("oauth:flow:" + request.getState());
            if (flowData == null) {
                return wrapErrorResponse("INVALID_FLOW", "Invalid OAuth flow", HttpStatus.BAD_REQUEST);
            }

            MfaValidationResult result = mfaService.validateMfaToken(oauthSession, request);

            if (!result.isValid()) {
                if (result.getRemainingAttempts() == 0) {

                    return wrapErrorResponse(result.getErrorCode(), result.getErrorMessage(), HttpStatus.TOO_MANY_REQUESTS);

                }

                String errorMessage = URLEncoder.encode(
                        String.format("Invalid MFA code. %d attempts remaining",
                                result.getRemainingAttempts()),
                        StandardCharsets.UTF_8
                );



                return wrapErrorResponse("invalid_token",  String.format("Invalid MFA code. %d attempts remaining",
                        result.getRemainingAttempts()), HttpStatus.BAD_REQUEST);

            }

            // MFA is valid - update session
            oauthSession.setMfaInitiated(true);
            oauthSession.resetMfa();
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
                    .queryParam("state", request.getState())
                    .queryParam("code_challenge", flowData.getCodeChallenge())
                    .queryParam("code_challenge_method", flowData.getCodeChallengeMethod())
                    .build().toUri();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(location)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();

        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}