package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.MfaRequest;
import consulting.gazman.security.idp.model.OAuthSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/auth/mfa")
public interface IMfaController {

    @PostMapping("/resend")
    ResponseEntity<?> resendCode(@RequestBody MfaRequest mfaRequest, String sessionToken);

    @PostMapping("/verify-backup")
    ResponseEntity<?> verifyBackupCode(@RequestBody MfaRequest mfaRequest, String sessionToken);



    @PostMapping("/initiate")
    ResponseEntity<?> initiateMfa(@RequestBody String sessionToken);

    @PostMapping("/verify")
    ResponseEntity<?> verifyMfa(@RequestBody MfaRequest request, String sessionToken);
}
