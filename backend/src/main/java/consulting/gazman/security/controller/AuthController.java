package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiError;
import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.dto.AuthResponseWrapper;
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
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        logRequest("POST", "/api/auth/login");

        AuthResponseWrapper result = authService.login(request);

        if ("locked".equals(result.getResult())) {
            return wrapErrorResponse("ACCOUNT_LOCKED",result.getMessage() , HttpStatus.BAD_REQUEST);



        }
        if("invalid_credentials".equals(result.getResult())){
            return wrapErrorResponse("INVALID_CREDENTIALS",result.getMessage() , HttpStatus.BAD_REQUEST);

        }

        return wrapSuccessResponse(result.getAuthResponse(), "Login successful");
    }

        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody AuthRequest request) {
            logRequest("POST", "/api/auth/register");
            try {
                AuthResponse authResponse = authService.register(request);
                return wrapSuccessResponse(authResponse, "Registration successful");
            } catch (AppException e) {
                return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.CONFLICT);
            } catch (Exception e) {
                return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@RequestBody AuthRequest request) {
            logRequest("POST", "/api/auth/refresh");
            try {
                AuthResponse authResponse = authService.refresh(request);
                return wrapSuccessResponse(authResponse, "Token refreshed successfully");
            } catch (AppException e) {
                return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
