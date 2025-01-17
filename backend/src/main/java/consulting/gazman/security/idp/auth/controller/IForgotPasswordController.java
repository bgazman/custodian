package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("/forgot-password")
public interface IForgotPasswordController {
    @GetMapping
    ModelAndView showResetPasswordPage(
            @RequestParam String email,
            @RequestParam String token);

    @PostMapping("/reset")
    ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request);

    @PostMapping("/initiate")
    ResponseEntity<?> initiatePasswordReset(@RequestBody Map<String, String> request);
}
