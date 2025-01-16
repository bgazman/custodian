package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.LogoutRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/auth")
public interface IAuthController {

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody UserRegistrationRequest request);

    @PostMapping("/verify-email")
    ResponseEntity<?> verifyEmail(@RequestParam("token") String token);



    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestBody LogoutRequest request);
}