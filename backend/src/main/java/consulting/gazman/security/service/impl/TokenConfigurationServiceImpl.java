package consulting.gazman.security.service.impl;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.TokenId;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.TokenConfigurationRepository;
import consulting.gazman.security.service.TokenConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenConfigurationServiceImpl implements TokenConfigurationService {

    @Autowired
    private TokenConfigurationRepository tokenConfigurationRepository;

    @Override
    public ApiResponse<List<TokenConfiguration>> getAllConfigurations() {
        try {
            List<TokenConfiguration> configurations = tokenConfigurationRepository.findAll();
            return ApiResponse.success(configurations, "Token configurations retrieved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving configurations.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<TokenConfiguration> findById(Long id) {
        try {
            TokenConfiguration configuration = tokenConfigurationRepository.findById(new TokenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Token configuration not found with ID: " + id));
            return ApiResponse.success(configuration, "Token configuration retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Token configuration not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<TokenConfiguration> findByAppName(String appName) {
        try {
            TokenConfiguration configuration = tokenConfigurationRepository.findByTokenIdAppName(appName)
                    .orElseThrow(() -> new ResourceNotFoundException("Token configuration not found for app: " + appName));
            return ApiResponse.success(configuration, "Token configuration retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Token configuration not found with the provided app name.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<TokenConfiguration> save(TokenConfiguration configuration) {
        try {
            TokenConfiguration savedConfiguration = tokenConfigurationRepository.save(configuration);
            return ApiResponse.success(savedConfiguration, "Token configuration saved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while saving the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
    public ApiResponse<Map<String, Object>> getJwks() {
        try {
            // Fetch all token configurations and map to JWKS keys
            List<Map<String, Object>> jwksKeys = tokenConfigurationRepository.findAll()
                    .stream()
                    .map(config -> {
                        try {
                            return convertToJwk(config);
                        } catch (Exception e) {
//                            log.error("Failed to convert TokenConfiguration to JWK for app: {}. Error: {}", config.getAppName(), e.getMessage());
                            return null; // Skip invalid keys
                        }
                    })
                    .filter(Objects::nonNull) // Remove null entries
                    .collect(Collectors.toList());

            // Build JWKS response
            return ApiResponse.success(Map.of("keys", jwksKeys), "JWKS fetched successfully.");
        } catch (Exception e) {
            return ApiResponse.error(
                    "server_error",
                    "Failed to fetch JWKS: " + e.getMessage(),
                    ApiError.of("server_error", e.getMessage())
            );
        }
    }


    private Map<String, Object> convertToJwk(TokenConfiguration config) throws Exception {
        try {
            // Extract RSAPublicKey from PEM
            RSAPublicKey publicKey = getPublicKeyFromPem(config.getPublicKey());

            // Construct the JWK
            return Map.of(
                    "kty", "RSA", // Key type
                    "kid", config.getTokenId().getKeyId(), // Key ID
                    "use", "sig", // Key usage: signing
                    "alg", config.getAlgorithm(), // Algorithm (e.g., RS256)
                    "n", base64UrlEncode(publicKey.getModulus().toByteArray()), // Modulus (Base64URL-encoded)
                    "e", base64UrlEncode(publicKey.getPublicExponent().toByteArray()) // Exponent (Base64URL-encoded)
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting TokenConfiguration to JWK: " + e.getMessage(), e);
        }
    }


    private RSAPublicKey getPublicKeyFromPem(String publicKeyPem) throws Exception {
        String processedPem = publicKeyPem.replace("\\n", "\n");

        // Remove PEM headers and all whitespace
        String publicKeyBase64 = processedPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        // Decode Base64 content
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 content in the public key PEM.", e);
        }

        // Generate RSAPublicKey
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to generate RSAPublicKey from PEM content.", e);
        }
    }

    private String base64UrlEncode(byte[] data) {
        // Convert to Base64URL-encoded string without padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
    @Override
    public ApiResponse<Void> delete(Long id) {
        try {
            TokenConfiguration configuration = tokenConfigurationRepository.findById(new TokenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Token configuration not found with ID: " + id));
            tokenConfigurationRepository.delete(configuration);
            return ApiResponse.success(null, "Token configuration deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Token configuration not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while deleting the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Boolean> existsByAppName(String appName) {
        try {
            boolean exists = tokenConfigurationRepository.existsByTokenIdAppName(appName);
            return ApiResponse.success(exists, "Existence check completed successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while checking configuration existence.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
}
