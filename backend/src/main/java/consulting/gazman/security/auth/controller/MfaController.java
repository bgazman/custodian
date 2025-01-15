package consulting.gazman.security.auth.controller;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.oauth.dto.AuthorizeRequest;
import consulting.gazman.security.oauth.dto.AuthorizeResponse;
import consulting.gazman.security.auth.dto.MfaRequest;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.auth.service.MfaService;
import consulting.gazman.security.oauth.service.OAuthService;
import consulting.gazman.security.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/mfa")
public class MfaController extends ApiController {

    @Autowired
    UserService userService;
    @Autowired
    private MfaService mfaService;
    @Autowired
    OAuthService oAuthService;
    @GetMapping
    public ModelAndView showMfaPage(
            @RequestParam String email,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String state,
            @RequestParam String response_type,
            @RequestParam(required = false) String mfa_method,
            @RequestParam(required = false) String scope) {
        ModelAndView mav = new ModelAndView("mfa");

        // Add user object to the model
        User user = userService.findByEmail(email);
        mav.addObject("user", user);

        // Add other parameters
        mav.addObject("email", email);
        mav.addObject("clientId", client_id);
        mav.addObject("redirectUri", redirect_uri);
        mav.addObject("state", state);
        mav.addObject("responseType", response_type);
        mav.addObject("scope", scope);
        mav.addObject("mfaMethod", mfa_method);  // Add this to model

        return mav;
    }
    @PostMapping("/resend")
    public ResponseEntity<?> resendCode(@RequestBody MfaRequest mfaRequest) {
        try {
            boolean sent = mfaService.resendMfaCode(mfaRequest.getEmail());
            if (sent) {
                return wrapSuccessResponse(
                        Map.of("message", "Verification code resent successfully"),
                        "Code resent successfully"
                );
            } else {
                return wrapErrorResponse(
                        "RESEND_LIMIT_EXCEEDED",
                        "Too many resend attempts. Please try again later.",
                        HttpStatus.TOO_MANY_REQUESTS
                );
            }
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred while resending code.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/verify-backup")
    public ResponseEntity<?> verifyBackupCode(@RequestBody MfaRequest mfaRequest) {
        try {
            boolean isValid = mfaService.validateBackupCode(
                    mfaRequest.getEmail(),
                    mfaRequest.getToken()
            );

            if (!isValid) {
                return wrapErrorResponse(
                        "INVALID_BACKUP_CODE",
                        "Invalid backup code",
                        HttpStatus.UNAUTHORIZED
                );
            }

            return wrapSuccessResponse(
                    Map.of("message", "Backup code validated successfully"),
                    "Backup code validated successfully"
            );
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred while verifying backup code.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @PostMapping("/initiate")
    public ResponseEntity<?> initiateMfa(@RequestBody MfaRequest mfaRequest) {
        logRequest("POST", "/mfa/initiate");
        try {
            mfaService.initiateMfaChallenge(mfaRequest.getEmail(), mfaRequest.getMethod());
            return wrapSuccessResponse(
                    Map.of("message", "MFA challenge initiated successfully"),
                    "MFA challenge initiated successfully"
            );
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred while initiating MFA.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyMfa(@RequestBody MfaRequest request) {
        try {
            boolean isValid = mfaService.validateMfaToken(request.getEmail(), request.getToken(), request.getMethod());

            if (!isValid) {
                String redirectUrl = "/mfa?error=invalid_token&message=" +
                        URLEncoder.encode("Invalid MFA code", StandardCharsets.UTF_8);

                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();
            }

            // Step 2: Generate authorization code (same as login endpoint)
            AuthorizeResponse response = oAuthService.generateAuthCode(AuthorizeRequest.builder()
                    .email(request.getEmail())
                    .responseType(request.getResponseType())
                    .clientId(request.getClientId())
                    .redirectUri(request.getRedirectUri())
                    .scope(request.getScope())
                    .state(request.getState())
                    .build());

            // Step 3: Redirect to client with auth code (same as login endpoint)
            String redirectUrl = request.getRedirectUri() + "?code=" + response.getCode() +
                    "&state=" + URLEncoder.encode(response.getState(), StandardCharsets.UTF_8);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();

        } catch (Exception e) {
//            log.error("Error during MFA verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during MFA verification. Please try again later.");
        }
    }

}
