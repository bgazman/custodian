package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.impl.ClientRegistrationServiceImpl;
import consulting.gazman.security.service.impl.OAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/oauth2")
public class OAuthController extends ApiController {

    @Autowired
    OAuthServiceImpl oAuthService;
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String scope,
            @RequestParam String state
    ) {
        logRequest("GET", "/oauth/authorize");
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
