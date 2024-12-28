package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.security.dto.*;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends ApiController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logRequest("POST", "/api/auth/login");

        TokenResponse result = authService.login(request);

        return wrapSuccessResponse(result, "Login successful");
    }

        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody UserRegistartionRequest request) {
            logRequest("POST", "/api/auth/register");
            try {
                UserRegistrationResponse authResponse = authService.register(request);
                return wrapSuccessResponse(authResponse, "Registration successful");
            } catch (AppException e) {
                return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.CONFLICT);
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
