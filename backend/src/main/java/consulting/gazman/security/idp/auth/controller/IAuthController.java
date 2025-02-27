package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.LoginRequest;
import consulting.gazman.security.idp.auth.dto.LogoutRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;

import consulting.gazman.security.idp.oauth.dto.AuthorizeRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/auth")
public interface IAuthController {

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody UserRegistrationRequest request);

    @PostMapping("/verify-email")
    ResponseEntity<?> verifyEmail(@RequestParam("token") String token);



    @PostMapping("/login")
    ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken);

    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestBody LogoutRequest request);
}