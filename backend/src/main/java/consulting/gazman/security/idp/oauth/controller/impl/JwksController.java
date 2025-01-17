package consulting.gazman.security.idp.oauth.controller.impl;



import consulting.gazman.security.common.controller.ApiController;

import consulting.gazman.security.common.exception.AppException;

import consulting.gazman.security.idp.oauth.service.impl.OAuthClientServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class JwksController extends ApiController implements consulting.gazman.security.idp.oauth.controller.IJwksController {

    @Autowired
    private OAuthClientServiceImpl oAuthClientService;

    // Inject the base URL from application.properties
    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public ResponseEntity<?> getJwks() {
        try {
            Map<String, Object> serviceResponse = oAuthClientService.getJwks();
            return wrapSuccessResponse(serviceResponse, "Groups retrieved successfully for the permission");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getOpenIdConfiguration() {
        logRequest("GET", "/.well-known/openid-configuration");

        try {
            // Use the injected baseUrl
            String issuerUrl = baseUrl;
            String jwksUri = baseUrl + "/.well-known/jwks.json";
            int tokenLifetime = 3600; // Access token expiration time
            int refreshTokenLifetime = 86400; // Refresh token expiration time

            // Build the configuration
            Map<String, Object> config = new HashMap<>();
            config.put("issuer", issuerUrl);
            config.put("authorization_endpoint", issuerUrl + "/oauth/authorize");
            config.put("token_endpoint", issuerUrl + "/oauth/token");
            config.put("jwks_uri", jwksUri);
            config.put("token_endpoint_auth_methods_supported", List.of("client_secret_basic"));
            config.put("id_token_signing_alg_values_supported", List.of("RS256"));
            config.put("scopes_supported", List.of("openid", "profile", "email"));
            config.put("grant_types_supported", List.of("authorization_code", "refresh_token"));
            config.put("claims_supported", List.of("sub", "name", "email", "email_verified"));
            config.put("response_types_supported", List.of("code"));
            config.put("subject_types_supported", List.of("public"));

            // Add token lifetimes
            config.put("access_token_lifetime", tokenLifetime);
            config.put("refresh_token_lifetime", refreshTokenLifetime);

            return wrapSuccessResponse(config, "OpenID Configuration retrieved successfully");
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
