package consulting.gazman.security.auth.controller;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.auth.dto.AuthRequest;
import consulting.gazman.security.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.auth.service.AuthService;
import consulting.gazman.security.auth.service.impl.AuthServiceImpl;
import consulting.gazman.security.auth.service.impl.EmailVerificationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends ApiController {

    private final EmailVerificationServiceImpl emailVerificationServiceImpl;
    private final AuthService authService;
    public AuthController(EmailVerificationServiceImpl emailVerificationServiceImpl, AuthServiceImpl authService) {
        this.emailVerificationServiceImpl = emailVerificationServiceImpl;
        this.authService = authService;
    }





        @PostMapping("/register")
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
    @PostMapping("/verify-email")
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


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody AuthRequest request) {

        try {
//            authService.logout(request);
            return wrapSuccessResponse(null, "Logout successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
