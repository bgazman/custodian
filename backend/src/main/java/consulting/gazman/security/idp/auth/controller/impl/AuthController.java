package consulting.gazman.security.idp.auth.controller.impl;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.auth.controller.IAuthController;
import consulting.gazman.security.idp.auth.dto.LogoutRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.impl.AuthServiceImpl;
import consulting.gazman.security.idp.auth.service.impl.EmailVerificationServiceImpl;
import consulting.gazman.security.idp.oauth.dto.AuthorizeRequest;
import consulting.gazman.security.idp.oauth.dto.AuthorizeResponse;
import consulting.gazman.security.idp.auth.dto.LoginRequest;
import consulting.gazman.security.idp.auth.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import consulting.gazman.security.idp.oauth.service.AuthCodeService;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class AuthController extends ApiController implements IAuthController {
    private final AuthCodeService authCodeService;
    private final EmailVerificationServiceImpl emailVerificationServiceImpl;
    private final AuthService authService;
    public AuthController(AuthCodeService authCodeService, EmailVerificationServiceImpl emailVerificationServiceImpl, AuthServiceImpl authService) {
        this.authCodeService = authCodeService;
        this.emailVerificationServiceImpl = emailVerificationServiceImpl;
        this.authService = authService;
    }





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

    @Override
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Step 1: Build the initial login request
            LoginRequest loginRequest = LoginRequest.builder()
                    .redirectUri(request.getRedirectUri())
                    .clientId(request.getClientId())
                    .state(request.getState())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .build();

            // Step 2: Attempt primary login (email and password validation)
            LoginResponse loginResponse = authService.login(loginRequest);

            if (loginResponse.getError() != null && !loginResponse.getError().isBlank()) {
                String redirectUrl = "/login?error=invalid_credentials&message=" +
                        URLEncoder.encode(loginResponse.getError(), StandardCharsets.UTF_8);
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();
            }

            if (loginResponse.isMfaEnabled()) {
                if (loginResponse.getMfaMethod() == null) {
                    throw AppException.missingConfiguration("MFA is enabled but no method is configured");
                }
                String redirectUrl = "/mfa?email=" + URLEncoder.encode(request.getEmail(), StandardCharsets.UTF_8) +
                        "&client_id=" + URLEncoder.encode(request.getClientId(), StandardCharsets.UTF_8) +
                        "&redirect_uri=" + URLEncoder.encode(request.getRedirectUri(), StandardCharsets.UTF_8) +
                        "&state=" + URLEncoder.encode(request.getState(), StandardCharsets.UTF_8) +
                        "&response_type=" + URLEncoder.encode(request.getResponseType(), StandardCharsets.UTF_8) +
                        "&mfa_method=" + URLEncoder.encode(loginResponse.getMfaMethod(), StandardCharsets.UTF_8);

                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();
            }

            // Step 4: Generate authorization code
LoginResponse response = LoginResponse.builder()
                    .code(authCodeService.generateCode(request.getEmail(), request.getClientId()))
                    .redirectUri(request.getRedirectUri())
                    .state(request.getState())
                    .build();

            String redirectUrl = request.getRedirectUri() + "?code=" + response.getCode() + "&state=" +
                    URLEncoder.encode(response.getState(), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();

        } catch (Exception e) {
            // Log the error and return a generic error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during the login process. Please try again later.");
        }
    }



    @Override
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {

        try {
//            authService.logout(request);
            return wrapSuccessResponse(null, "Logout successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
