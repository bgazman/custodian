package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.LoginRequest;
import consulting.gazman.security.idp.auth.dto.LogoutRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;

import consulting.gazman.security.idp.oauth.dto.AuthorizeRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/auth")
public interface IAuthController {

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody UserRegistrationRequest request);

    @PostMapping("/verify-email")
    ResponseEntity<?> verifyEmail(@RequestParam("token") String token);

//    @PostMapping("/login")
//    ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);


    @PostMapping("/login")
    ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    );

    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestBody LogoutRequest request);
}