package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.security.dto.*;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.AuthService;
import consulting.gazman.security.service.UserService;
import consulting.gazman.security.service.impl.AuthServiceImpl;
import consulting.gazman.security.service.impl.EmailVerificationService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends ApiController {

    private final EmailVerificationService emailVerificationService;
    private final AuthService authService;
    public AuthController(EmailVerificationService emailVerificationService, AuthServiceImpl authService) {
        this.emailVerificationService = emailVerificationService;
        this.authService = authService;
    }


    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logRequest("POST", "/api/auth/login");

        try {
            TokenResponse result = authService.login(request);
            return wrapSuccessResponse(result, "Login successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
            logRequest("POST", "/api/auth/register");
            try {
                authService.registerUser(request);
                return wrapSuccessResponse(null, "Registration successful");
            } catch (AppException e) {
                return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.CONFLICT);
            } catch (Exception e) {
                return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        logRequest("POST", "/api/auth/verify-email");
        try {
            emailVerificationService.validateVerificationToken(token);

            return wrapSuccessResponse(null, "Email verified successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
            logRequest("POST", "/api/auth/refresh");
            try {
                TokenResponse authResponse = authService.refresh(request);
                return wrapSuccessResponse(authResponse, "Token refreshed successfully");
            } catch (AppException e) {
                return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody AuthRequest request) {
        logRequest("POST", "/api/auth/logout");
        try {
//            authService.logout(request);
            return wrapSuccessResponse(null, "Logout successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
