package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.impl.ClientRegistrationServiceImpl;
import consulting.gazman.security.service.impl.OAuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class OAuthController extends ApiController {

    @Autowired
    OAuthServiceImpl oAuthService;
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String scope,
            @RequestParam String state,
            HttpServletRequest request
    ) {
        logRequest("GET", "/oauth/authorize");
        // Check if the user is authenticated (example placeholder logic)
        boolean isAuthenticated = request.getSession().getAttribute("user") != null;

        if (!isAuthenticated) {
            String loginUrl = "/login?response_type=" + URLEncoder.encode(response_type, StandardCharsets.UTF_8) +
                    "&client_id=" + URLEncoder.encode(client_id, StandardCharsets.UTF_8) +
                    "&redirect_uri=" + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8) +
                    "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                    "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);


            log.info("Redirecting to login page: {}", loginUrl); // Add this log
            return ResponseEntity.status(302).location(URI.create(loginUrl)).build();
        }
        try {
            AuthorizeResponse response = oAuthService.generateAuthCode(AuthorizeRequest.builder()
                    .responseType(response_type)
                    .clientId(client_id)
                    .redirectUri(redirect_uri)
                    .scope(scope)
                    .state(state)
                    .build());

            String redirectUrl = redirect_uri + "?code=" + response.getCode() + "&state=" + state;
            return ResponseEntity.status(302).location(URI.create(redirectUrl)).build();

        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenRequest request) {
        logRequest("POST", "/oauth/token");
        try {
            TokenResponse response;

            switch(request.getGrantType()) {
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

            return wrapSuccessResponse(response, "Token issued successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> userinfo(@RequestHeader("Authorization") String bearerToken) {
        logRequest("GET", "/oauth/userinfo");
        try {
            UserInfoResponse response = oAuthService.getUserInfo(bearerToken);
            return wrapSuccessResponse(response, "User info retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
