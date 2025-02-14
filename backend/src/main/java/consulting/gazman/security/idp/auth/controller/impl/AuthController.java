package consulting.gazman.security.idp.auth.controller.impl;

import consulting.gazman.security.client.user.entity.User;
import consulting.gazman.security.client.user.service.UserService;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.auth.controller.IAuthController;
import consulting.gazman.security.idp.auth.dto.LogoutRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.impl.AuthServiceImpl;
import consulting.gazman.security.idp.auth.service.impl.EmailVerificationServiceImpl;
import consulting.gazman.security.idp.auth.dto.LoginRequest;
import consulting.gazman.security.idp.auth.dto.LoginResponse;
import consulting.gazman.security.idp.model.OAuthSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import consulting.gazman.security.idp.oauth.service.AuthCodeService;
import consulting.gazman.security.idp.oauth.service.OAuthSessionService;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class AuthController extends ApiController implements IAuthController {
    private final AuthCodeService authCodeService;
    private final EmailVerificationServiceImpl emailVerificationServiceImpl;
    private final AuthService authService;
    private final UserService userService;
    private final OAuthSessionService oAuthSessionService;

    public AuthController(AuthCodeService authCodeService, EmailVerificationServiceImpl emailVerificationServiceImpl, AuthServiceImpl authService, UserService userService, OAuthSessionService oAuthSessionService) {
        this.authCodeService = authCodeService;
        this.emailVerificationServiceImpl = emailVerificationServiceImpl;
        this.authService = authService;
        this.userService = userService;
        this.oAuthSessionService = oAuthSessionService;
    }

    @Override
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        try {
            authService.registerUser(request);
            return wrapSuccessResponse(null, "Registration successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            emailVerificationServiceImpl.validateVerificationToken(token);
            return wrapSuccessResponse(null, "Email verified successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);

            if (loginResponse.isMfaEnabled()) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create("/mfa?email=" +
                                URLEncoder.encode(loginRequest.getEmail(), StandardCharsets.UTF_8)))
                        .build();
            }

            User user = userService.findByEmail(loginRequest.getEmail());
            request.getSession().setAttribute("user", user);

            String sessionId = request.getSession().getId();
            OAuthSession oauthSession = oAuthSessionService.getSession(sessionId);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(UriComponentsBuilder.fromPath("/oauth/authorize")
                            .queryParam("response_type", oauthSession.getResponseType())
                            .queryParam("client_id", oauthSession.getClientId())
                            .queryParam("redirect_uri", oauthSession.getRedirectUri())
                            .queryParam("scope", oauthSession.getScope())
                            .queryParam("state", oauthSession.getState())
                            .build().toUriString()))
                    .build();

        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/login?error=" +
                            URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8)))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during the login process. Please try again later.");
        }
    }

    @Override
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        try {
            return wrapSuccessResponse(null, "Logout successful");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}