package consulting.gazman.security.controller;



import consulting.gazman.common.controller.ApiController;

import consulting.gazman.security.exception.AppException;

import consulting.gazman.security.service.impl.OAuthClientServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/.well-known")
public class JwksController extends ApiController {

    @Autowired
    private OAuthClientServiceImpl oAuthClientService;


    @GetMapping("/jwks.json")
    public ResponseEntity<?> getJwks() {
        logRequest("GET", "/.well-known/jwks.json");
        try {
            Map<String, Object> serviceResponse = oAuthClientService.getJwks();
            return wrapSuccessResponse(serviceResponse, "Groups retrieved successfully for the permission");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/openid-configuration")
    public ResponseEntity<?> getOpenIdConfiguration() {
        logRequest("GET", "/.well-known/openid-configuration");

        try {
            // Since there's only one tenant, you can directly use its details
            String issuerUrl = "https://localhost:8080";
            String jwksUri = "https://localhost:8080/.well-known/jwks.json";
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