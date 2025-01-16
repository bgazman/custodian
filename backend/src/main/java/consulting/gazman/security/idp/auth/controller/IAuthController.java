package consulting.gazman.security.idp.auth.controller;

import consulting.gazman.security.idp.auth.dto.LogoutRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Authentication", description = "Authentication management APIs")
@RequestMapping("/api/auth")
public interface IAuthController {
    @Operation(
            summary = "Register new user",
            description = "Register a new user account and send verification email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody UserRegistrationRequest request);

    @Operation(
            summary = "Verify email address",
            description = "Verify user's email address using token sent via email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    @PostMapping("/verify-email")
    ResponseEntity<?> verifyEmail(@RequestParam("token") String token);

    @Operation(
            summary = "Logout user",
            description = "Invalidate user's session"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully")
    })
    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestBody LogoutRequest request);
}