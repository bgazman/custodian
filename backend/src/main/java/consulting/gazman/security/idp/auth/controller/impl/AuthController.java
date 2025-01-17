package consulting.gazman.security.idp.auth.controller.impl;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.auth.controller.IAuthController;
import consulting.gazman.security.idp.auth.dto.LogoutRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.impl.AuthServiceImpl;
import consulting.gazman.security.idp.auth.service.impl.EmailVerificationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController extends ApiController implements IAuthController {

    private final EmailVerificationServiceImpl emailVerificationServiceImpl;
    private final AuthService authService;
    public AuthController(EmailVerificationServiceImpl emailVerificationServiceImpl, AuthServiceImpl authService) {
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
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {

        try {
//            authService.logout(request);
            return wrapSuccessResponse(null, "Logout successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
