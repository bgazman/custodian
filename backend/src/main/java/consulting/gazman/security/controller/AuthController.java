package consulting.gazman.security.controller;

import org.springframework.http.HttpStatus;

import common.dto.ApiRequest;
import common.dto.ApiResponse;
import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.service.AuthService;
import consulting.gazman.security.utils.JwtUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody ApiRequest<AuthRequest> request) {
        return authService.login(request.getData());
        }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody ApiRequest<AuthRequest> request) {
        return authService.register(request.getData());
    }
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody ApiRequest<AuthRequest> request) {
        return authService.refresh(request.getData());
    }

}
