package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.MfaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/mfa")
public interface IMfaController {
    @GetMapping
    ModelAndView showMfaPage(
            @RequestParam String email,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String state,
            @RequestParam String response_type,
            @RequestParam(required = false) String mfa_method,
            @RequestParam(required = false) String scope);

    @PostMapping("/resend")
    ResponseEntity<?> resendCode(@RequestBody MfaRequest mfaRequest);

    @PostMapping("/verify-backup")
    ResponseEntity<?> verifyBackupCode(@RequestBody MfaRequest mfaRequest);

    @PostMapping("/initiate")
    ResponseEntity<?> initiateMfa(@RequestBody MfaRequest mfaRequest);

    @PostMapping("/verify")
    ResponseEntity<?> verifyMfa(@RequestBody MfaRequest request);
}
