package consulting.gazman.security.controller;



import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Tenant;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.TenantRepository;
import consulting.gazman.security.service.TokenService;
import consulting.gazman.security.service.impl.OAuthClientServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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

    @Autowired
    TenantRepository tenantRepository;
    @GetMapping("/{tenantId}/jwks.json")
    public ResponseEntity<?> getJwks(@PathVariable("tenantId") Long tenantId) {
        logRequest("GET", "/.well-known/jwks.json");
        try {
            Map<String, Object> serviceResponse = oAuthClientService.getJwks(tenantId);
            return wrapSuccessResponse(serviceResponse, "Groups retrieved successfully for the permission");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{tenantId}/openid-configuration")
    public ResponseEntity<?> getOpenIdConfiguration(@PathVariable("tenantId") Long tenantId) {
        logRequest("GET", "/" + tenantId + "/.well-known/openid-configuration");

        try {
            // Retrieve the tenant data by tenantId
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new EntityNotFoundException("Tenant with ID " + tenantId + " not found"));

            // Build the configuration using tenant-specific data
            Map<String, Object> config = new HashMap<>();
            String issuerUrl = tenant.getIssuerUrl();

            config.put("issuer", issuerUrl);
            config.put("authorization_endpoint", issuerUrl + "/oauth/authorize");
            config.put("token_endpoint", issuerUrl + "/oauth/token");
            config.put("jwks_uri", tenant.getJwksUri());
            config.put("token_endpoint_auth_methods_supported", List.of("client_secret_basic"));
            config.put("id_token_signing_alg_values_supported", List.of("RS256")); // Use tenant-specific algorithm if applicable
            config.put("scopes_supported", List.of("openid", "profile", "email"));
            config.put("grant_types_supported", List.of("authorization_code", "refresh_token"));
            config.put("claims_supported", List.of("sub", "name", "email", "email_verified"));
            config.put("response_types_supported", List.of("code"));
            config.put("subject_types_supported", List.of("public"));

            // Add tenant-specific token lifetimes
            config.put("access_token_lifetime", tenant.getTokenLifetime());
            config.put("refresh_token_lifetime", tenant.getRefreshTokenLifetime());

            return wrapSuccessResponse(config, "OpenID Configuration retrieved successfully");
        } catch (EntityNotFoundException e) {
            return wrapErrorResponse("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}