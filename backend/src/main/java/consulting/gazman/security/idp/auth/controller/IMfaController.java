package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.MfaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth/mfa")
public interface IMfaController {

    @PostMapping("/resend")
    ResponseEntity<?> resendCode(@RequestBody MfaRequest mfaRequest,
                                 @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken);

    @PostMapping("/verify-recovery")
    ResponseEntity<?> verifyRecoveryCode(@RequestBody MfaRequest mfaRequest,
                                       @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken);



    @PostMapping("/initiate")
    ResponseEntity<?> initiateMfa(@CookieValue(name = "OAUTH_SESSION",
            required = false) String sessionToken);


    @PostMapping("/verify")
    ResponseEntity<?> verifyMfa(@RequestBody MfaRequest request,
                                @CookieValue(name = "OAUTH_SESSION",
                                        required = false) String sessionToken);}
