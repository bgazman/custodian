package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiError;
import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.AuthService;
import consulting.gazman.security.service.MfaService;
import consulting.gazman.security.service.OAuthService;
import consulting.gazman.security.service.impl.ClientRegistrationServiceImpl;
import consulting.gazman.security.service.impl.OAuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/oauth")

public class OAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    MfaService mfaService;
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String scope,
            @RequestParam String state,
            HttpServletRequest request
    ) {
        // Check if the user is authenticated
        boolean isAuthenticated = request.getSession().getAttribute("user") != null;

        if (!isAuthenticated) {
            // Save state in session
            request.getSession().setAttribute("state", state);

            // Redirect to login
            String loginUrl = "/login?response_type=" + URLEncoder.encode(response_type, StandardCharsets.UTF_8) +
                    "&client_id=" + URLEncoder.encode(client_id, StandardCharsets.UTF_8) +
                    "&redirect_uri=" + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8) +
                    "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                    "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(loginUrl)).build();
        }

        // Validate state
        String sessionState = (String) request.getSession().getAttribute("state");
        if (!state.equals(sessionState)) {
            throw new AppException("STATE_MISMATCH", "Invalid state parameter");
        }

        // Generate authorization code
        String code = generateAuthorizationCode(response_type, client_id, redirect_uri, scope, state);

        // Redirect to client with code and state
        String redirectUrl = UriComponentsBuilder.fromUriString(redirect_uri)
                .queryParam("code", code)
                .queryParam("state", state)
                .build()
                .toUriString();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthorizeRequest request) {
        try {
            // Step 1: Build the initial login request
            LoginRequest loginRequest = LoginRequest.builder()
                    .redirectUri(request.getRedirectUri())
                    .clientId(request.getClientId())
                    .state(request.getState())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .build();

            // Step 2: Attempt primary login (email and password validation)
            LoginResponse loginResponse = oAuthService.login(loginRequest);

            if (loginResponse.getError() != null && !loginResponse.getError().isBlank()) {
                String redirectUrl = "/login?error=invalid_credentials&message=" +
                        URLEncoder.encode(loginResponse.getError(), StandardCharsets.UTF_8);
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();
            }

            // Step 3: Check if MFA is required for the user
            if (loginResponse.getMfaMethod() != null) {
                // Generate an MFA challenge using the method from the response
                mfaService.initiateMfaChallenge(request.getEmail(), loginResponse.getMfaMethod());

                String redirectUrl = "/mfa?email=" + URLEncoder.encode(request.getEmail(), StandardCharsets.UTF_8) +
                        "&client_id=" + URLEncoder.encode(request.getClientId(), StandardCharsets.UTF_8) +
                        "&redirect_uri=" + URLEncoder.encode(request.getRedirectUri(), StandardCharsets.UTF_8) +
                        "&state=" + URLEncoder.encode(request.getState(), StandardCharsets.UTF_8) +
                        "&response_type=" + URLEncoder.encode(request.getResponseType(), StandardCharsets.UTF_8) +
                        "&mfa_method=" + URLEncoder.encode(loginResponse.getMfaMethod(), StandardCharsets.UTF_8);

                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();
            }

            // Step 4: Generate authorization code
            AuthorizeResponse response = oAuthService.generateAuthCode(AuthorizeRequest.builder()
                    .email(request.getEmail())
                    .responseType(request.getResponseType())
                    .clientId(request.getClientId())
                    .redirectUri(request.getRedirectUri())
                    .scope(request.getScope())
                    .state(request.getState())
                    .build());

            String redirectUrl = request.getRedirectUri() + "?code=" + response.getCode() + "&state=" +
                    URLEncoder.encode(response.getState(), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();

        } catch (Exception e) {
            // Log the error and return a generic error response
            log.error("Error during login process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during the login process. Please try again later.");
        }
    }






    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenRequest request) {
        try {
            TokenResponse response;

            switch (request.getGrantType()) {
                case "authorization_code":
                    response = oAuthService.exchangeToken(request);
                    break;
                case "refresh_token":
                    if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
                        throw new AppException("INVALID_REQUEST", "Refresh token is required");
                    }
                    response = oAuthService.refreshToken(request);
                    break;
                default:
                    throw new AppException("UNSUPPORTED_GRANT_TYPE",
                            "Grant type '" + request.getGrantType() + "' not supported");
            }

            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.builder().code(e.getErrorCode()).message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.builder().code("INTERNAL_SERVER_ERROR").message(e.getMessage()).build());
        }
    }

    @GetMapping("/introspect")
    public ResponseEntity<?> introspect(@RequestBody String bearerToken) {
        try {
            // Validate and parse the token using your service
            IntrospectResponse response = oAuthService.introspectToken(bearerToken);

            // Return the introspection response
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            // Handle application-specific exceptions
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.builder().code(e.getErrorCode()).message(e.getMessage()).build());
        } catch (Exception e) {
            // Handle generic exceptions
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.builder().code("INTERNAL_SERVER_ERROR").message(e.getMessage()).build());
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeToken(@RequestBody String refreshToken) {
        try {
            // Attempt to revoke the token using your service
            oAuthService.revokeToken(refreshToken);

            // Return a success response
            return ResponseEntity.ok("Token successfully revoked.");
        } catch (AppException e) {
            // Handle application-specific exceptions
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.builder().code(e.getErrorCode()).message(e.getMessage()).build());
        } catch (Exception e) {
            // Handle generic exceptions
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.builder().code("INTERNAL_SERVER_ERROR").message(e.getMessage()).build());
        }
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> userinfo(@RequestHeader("Authorization") String bearerToken) {
        try {
            UserInfoResponse response = oAuthService.getUserInfo(bearerToken);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.builder().code(e.getErrorCode()).message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.builder().code("INTERNAL_SERVER_ERROR").message(e.getMessage()).build());
        }
    }

    private String generateAuthorizationCode(String responseType, String clientId, String redirectUri, String scope, String state) {
        return oAuthService.generateAuthCode(AuthorizeRequest.builder()
                .responseType(responseType)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .scope(scope)
                .state(state)
                .build()).getCode();
    }

}
