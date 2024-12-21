package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import consulting.gazman.common.utils.StatusMapper;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends ApiController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        logRequest("POST", "/api/auth/login");

        // Call AuthService and handle response
        ApiResponse<AuthResponse> serviceResponse = authService.login(request);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        logRequest("POST", "/api/auth/register");

        // Call AuthService and handle response
        ApiResponse<AuthResponse> serviceResponse = authService.register(request);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody AuthRequest request) {
        logRequest("POST", "/api/auth/refresh");

        // Call AuthService and handle response
        ApiResponse<AuthResponse> serviceResponse = authService.refresh(request);
        return handleApiResponse(serviceResponse);
    }
}




