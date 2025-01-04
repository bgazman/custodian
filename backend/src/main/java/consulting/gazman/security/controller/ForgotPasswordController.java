package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.security.dto.ResetPasswordRequest;
import consulting.gazman.security.service.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequestMapping("/forgot-password")
public class ForgotPasswordController extends ApiController {

    private final PasswordResetService passwordResetService;

    public ForgotPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping
    public ModelAndView showResetPasswordPage(
            @RequestParam String email,
            @RequestParam String token) {
        logRequest("GET", "/forgot-password");

        ModelAndView mav = new ModelAndView("password-reset");

        // Add necessary attributes to the model
        mav.addObject("email", email);
        mav.addObject("token", token);

        return mav;
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        logRequest("POST", "/forgot-password/reset-password");

        try {
            // Verify token and email
            if (!passwordResetService.validateResetToken(request.getEmail(), request.getToken())) {
                return wrapErrorResponse(
                        "INVALID_TOKEN",
                        "Invalid or expired token.",
                        HttpStatus.UNAUTHORIZED
                );
            }

            // Update password
            passwordResetService.resetPassword(request.getEmail(), request.getNewPassword());

            return wrapSuccessResponse(
                    Map.of("message", "Password reset successful."),
                    "Password reset successful."
            );
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred while resetting the password.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePasswordReset(@RequestBody Map<String, String> request) {
        logRequest("POST", "/forgot-password/initiate");

        try {
            String email = request.get("email");

            // Validate email input
            if (email == null || email.trim().isEmpty()) {
                return wrapErrorResponse(
                        "INVALID_EMAIL",
                        "Email address is required.",
                        HttpStatus.BAD_REQUEST
                );
            }

            // Generate reset token and send email
            passwordResetService.initiatePasswordReset(email);

            return wrapSuccessResponse(
                    Map.of("message", "Password reset email sent successfully."),
                    "Password reset email sent successfully."
            );
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred while initiating password reset.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
