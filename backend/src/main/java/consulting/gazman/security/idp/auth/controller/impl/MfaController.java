package consulting.gazman.security.idp.auth.controller.impl;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.auth.controller.IMfaController;
import consulting.gazman.security.idp.auth.dto.*;
import consulting.gazman.security.idp.model.OAuthFlowData;
import consulting.gazman.security.idp.model.OAuthSession;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.MfaService;

import consulting.gazman.security.idp.oauth.service.JwtService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
public class MfaController extends ApiController implements IMfaController {
    private final MfaService mfaService;
    private final JwtService jwtService;
    private final RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate;

    public MfaController(MfaService mfaService, JwtService jwtService, RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate) {
        this.mfaService = mfaService;
        this.jwtService = jwtService;
        this.flowDataRedisTemplate = flowDataRedisTemplate;
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
    public ResponseEntity<?> verifyBackupCode(@RequestBody MfaRequest mfaRequest,
                                              @CookieValue(name = "OAUTH_SESSION") String sessionToken
    ) {
        try {
            OAuthSession oauthSession = jwtService.parseSessionToken(sessionToken);
            if (oauthSession == null) {
                return wrapErrorResponse("INVALID_SESSION", "Invalid session", HttpStatus.BAD_REQUEST);
            }

            BackupCodeValidationResult result = mfaService.validateBackupCode(oauthSession, mfaRequest);

            if (!result.isValid()) {
                return wrapErrorResponse(
                        "INVALID_BACKUP_CODE",
                        result.getErrorMessage(),
                        HttpStatus.UNAUTHORIZED
                );
            }

            return wrapSuccessResponse(
                    Map.of(
                            "message", "Backup code validated successfully",
                            "remainingCodes", result.getRemainingCodes()
                    ),
                    "Backup code validated successfully"
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

            return wrapSuccessResponse(
                    Map.of(
                            "sessionToken", updatedToken,
                            "challengeId", result.getChallengeId(),
                            "expiresAt", result.getExpiresAt(),
                            "method", result.getMethod()
                    ),
                    "MFA challenge initiated successfully"
            );
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
                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                            .body(Map.of(
                                    "error", "Too many attempts",
                                    "errorCode", result.getErrorCode(),
                                    "message", result.getErrorMessage()
                            ));
                }

                String errorMessage = URLEncoder.encode(
                        String.format("Invalid MFA code. %d attempts remaining",
                                result.getRemainingAttempts()),
                        StandardCharsets.UTF_8
                );

                String redirectUrl = UriComponentsBuilder.fromPath("/mfa")
                        .queryParam("error", "invalid_token")
                        .queryParam("message", errorMessage)
                        .queryParam("sessionToken", sessionToken)
                        .build().toUriString();

                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();
            }

            // MFA is valid - update session
            oauthSession.setMfaInitiated(true);
            oauthSession.resetMfa();
            String updatedToken = jwtService.generateSessionToken(oauthSession);

            // Redirect back to authorize endpoint with verified session
            URI location = UriComponentsBuilder.fromPath("/oauth/authorize")
                    .queryParam("response_type", flowData.getResponseType())
                    .queryParam("client_id", flowData.getClientId())
                    .queryParam("redirect_uri", flowData.getRedirectUri())
                    .queryParam("scope", flowData.getScope())
                    .queryParam("state", flowData.getState())
                    .queryParam("code_challenge", flowData.getCodeChallenge())
                    .queryParam("code_challenge_method", flowData.getCodeChallengeMethod())
                    .queryParam("sessionToken", updatedToken)
                    .build().toUri();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(location)
                    .build();

        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}