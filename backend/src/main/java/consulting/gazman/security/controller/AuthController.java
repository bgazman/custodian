package consulting.gazman.security.controller;

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
        public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
            return ResponseEntity.ok(authService.login(authRequest));
        }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest registerRequest) throws BadRequestException {
            return ResponseEntity.ok(authService.register(registerRequest));
        }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody AuthRequest refreshRequest) throws BadRequestException {
        return ResponseEntity.ok(authService.refresh(refreshRequest));
    }

}
