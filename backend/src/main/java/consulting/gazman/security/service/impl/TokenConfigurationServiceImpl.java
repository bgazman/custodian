package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.TokenId;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.TokenConfigurationRepository;
import consulting.gazman.security.service.TokenConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<TokenConfiguration> getAllConfigurations() {
        return tokenConfigurationRepository.findAll();
    }

    @Override
    public TokenConfiguration findById(Long id) {
        return tokenConfigurationRepository.findById(new TokenId())
                .orElseThrow(() -> AppException.resourceNotFound("Token configuration not found with ID: " + id));
    }

    @Override
    public TokenConfiguration findByAppName(String appName) {
        return tokenConfigurationRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> AppException.resourceNotFound("Token configuration not found for app: " + appName));
    }

    @Override
    public TokenConfiguration save(TokenConfiguration configuration) {
        return tokenConfigurationRepository.save(configuration);
    }

    @Override
    public Map<String, Object> getJwks() {
        // Fetch all token configurations and map to JWKS keys
        List<Map<String, Object>> jwksKeys = tokenConfigurationRepository.findAll()
                .stream()
                .map(config -> {
                    try {
                        return convertToJwk(config);
                    } catch (Exception e) {
                        // Log the error and skip invalid keys
                        return null; // Skip invalid keys
                    }
                })
                .filter(Objects::nonNull) // Remove null entries
                .collect(Collectors.toList());

        return Map.of("keys", jwksKeys);
    }

    @Override
    public void delete(Long id) {
        TokenConfiguration configuration = tokenConfigurationRepository.findById(new TokenId())
                .orElseThrow(() -> AppException.resourceNotFound("Token configuration not found with ID: " + id));
        tokenConfigurationRepository.delete(configuration);
    }

    @Override
    public boolean existsByAppName(String appName) {
        return tokenConfigurationRepository.existsByTokenIdAppName(appName);
    }

    // Private utility methods

    private Map<String, Object> convertToJwk(TokenConfiguration config) throws Exception {
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
    }

    private RSAPublicKey getPublicKeyFromPem(String publicKeyPem) throws Exception {
        String processedPem = publicKeyPem.replace("\\n", "\n");

        // Remove PEM headers and all whitespace
        String publicKeyBase64 = processedPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        // Decode Base64 content
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);

        // Generate RSAPublicKey
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    private String base64UrlEncode(byte[] data) {
        // Convert to Base64URL-encoded string without padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
