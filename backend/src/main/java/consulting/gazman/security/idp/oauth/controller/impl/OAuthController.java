package consulting.gazman.security.idp.oauth.controller.impl;

import consulting.gazman.security.common.dto.ApiError;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.MfaService;
import consulting.gazman.security.idp.model.OAuthSession;
import consulting.gazman.security.idp.oauth.service.OAuthSessionService;
import consulting.gazman.security.idp.oauth.controller.IOAuthController;
import consulting.gazman.security.idp.oauth.dto.*;
import consulting.gazman.security.idp.oauth.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController

public class OAuthController implements IOAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OAuthService oAuthService;
    @Autowired
    MfaService mfaService;
    @Autowired
    OAuthSessionService oAuthSessionService;
    @Override
    public ResponseEntity<?> authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String scope,
            @RequestParam String state,
            @RequestParam(required = false) String code_challenge,
            @RequestParam(required = false) String code_challenge_method,
            HttpServletRequest request
    ) {
        String sessionId = request.getSession().getId();
        OAuthSession session = oAuthSessionService.getSession(sessionId);

        if (session == null) {
            OAuthSession oauthSession = OAuthSession.builder()
                    .state(state)
                    .clientId(client_id)
                    .redirectUri(redirect_uri)
                    .responseType(response_type)
                    .scope(scope)
                    .codeChallenge(code_challenge)
                    .codeChallengeMethod(code_challenge_method)
                    .build();
            oAuthSessionService.saveSession(sessionId, oauthSession);

            request.getSession().setAttribute("oauth_session", oauthSession);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/login"))
                    .build();
        }
        OAuthSession oauthSession = (OAuthSession) request.getSession().getAttribute("oauth_session");
        if (!state.equals(oauthSession.getState())) {
            throw new AppException("STATE_MISMATCH", "Invalid state parameter");
        }

        String code = oAuthService.generateAuthCode(AuthorizeRequest.builder()
                        .email(session.getEmail())
                .responseType(response_type)
                .clientId(client_id)
                .redirectUri(redirect_uri)
                .scope(scope)
                .codeChallenge(code_challenge)
                .codeChallengeMethod(code_challenge_method)
                .build()).getCode();

        request.getSession().removeAttribute("oauth_session");

// Instead of returning a 302 redirect:
        Map<String, String> responseBody = new HashMap<>();
        String redirectUrl = UriComponentsBuilder.fromUriString(redirect_uri)
                .queryParam("code", code)
                .queryParam("state", state)
                .build()
                .toUriString();
        responseBody.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok(responseBody);

    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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
