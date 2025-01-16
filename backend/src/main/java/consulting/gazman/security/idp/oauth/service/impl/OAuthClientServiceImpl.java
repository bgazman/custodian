package consulting.gazman.security.idp.oauth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.entity.Secret;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.oauth.repository.OAuthClientRepository;
import consulting.gazman.security.idp.oauth.service.OAuthClientService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OAuthClientServiceImpl implements OAuthClientService {

    private final OAuthClientRepository oAuthClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Autowired
    public OAuthClientServiceImpl(OAuthClientRepository oAuthClientRepository,
                                  PasswordEncoder passwordEncoder,
                                  ObjectMapper objectMapper) {
        this.oAuthClientRepository = oAuthClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    public OAuthClient createClient(OAuthClient client) {
        validateClientData(client);
        client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
        return oAuthClientRepository.save(client);
    }

    @Override
    public OAuthClient getClientById(Long id) {
        return oAuthClientRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("OAuth Client not found with id: " + id));
    }

    @Override
    public Optional<OAuthClient> getClientByClientId(String clientId) {
        return oAuthClientRepository.findByClientId(clientId);
    }

    @Override
    public List<OAuthClient> getAllClients() {
        return oAuthClientRepository.findByDeletedAtIsNull();
    }

    @Override
    public OAuthClient updateClient(Long id, OAuthClient updatedClient) {
        OAuthClient existingClient = getClientById(id);
        validateClientData(updatedClient);

        existingClient.setRedirectUris(updatedClient.getRedirectUris());
        existingClient.setGrantTypes(updatedClient.getGrantTypes());
        existingClient.setScopes(updatedClient.getScopes());
        existingClient.setTokenEndpointAuthMethod(updatedClient.getTokenEndpointAuthMethod());
        existingClient.setAlgorithm(updatedClient.getAlgorithm());
        existingClient.setClientSecret(updatedClient.getClientSecret());

        if (updatedClient.getClientSecret() != null) {
            existingClient.setClientSecret(passwordEncoder.encode(updatedClient.getClientSecret()));
        }

        return oAuthClientRepository.save(existingClient);
    }

    @Override
    public void deleteClient(Long id) {
        if (!oAuthClientRepository.existsById(id)) {
            throw AppException.resourceNotFound("OAuth Client not found with id: " + id);
        }
        oAuthClientRepository.deleteById(id);
    }

    @Override
    public void softDeleteClient(Long id) {
        OAuthClient client = getClientById(id);
        client.setDeletedAt(LocalDateTime.now());
        oAuthClientRepository.save(client);
    }

    @Override
    public boolean validateClientSecret(String clientId, String clientSecret) {
        return getClientByClientId(clientId)
                .map(client -> passwordEncoder.matches(clientSecret, client.getClientSecret()))
                .orElse(false);
    }

    @Override
    public boolean validateRedirectUri(String clientId, String redirectUri) {
        return getClientByClientId(clientId)
                .map(client -> client.getRedirectUris().contains(redirectUri))
                .orElse(false);
    }

    @Override
    public boolean validateGrantType(String clientId, String grantType) {
        return getClientByClientId(clientId)
                .map(client -> client.getGrantTypes().contains(grantType))
                .orElse(false);
    }

    @Override
    public boolean validateScope(String clientId, String scope) {
        return getClientByClientId(clientId)
                .map(client -> client.getScopes() != null && client.getScopes().contains(scope))
                .orElse(false);
    }

    private void validateClientData(OAuthClient client) {
        if (client.getClientId() == null || client.getClientId().trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be empty");
        }

        if (client.getRedirectUris() == null || client.getRedirectUris().isEmpty()) {
            throw new IllegalArgumentException("Redirect URIs cannot be empty");
        }

        if (client.getGrantTypes() == null || client.getGrantTypes().isEmpty()) {
            throw new IllegalArgumentException("Grant types cannot be empty");
        }

        // Scopes can be null but if present should not be empty
        if (client.getScopes() != null && client.getScopes().isEmpty()) {
            throw new IllegalArgumentException("Scopes list cannot be empty if provided");
        }
    }
    @Override
    public Map<String, Object> getJwks() {
        // Fetch all token configurations and map to JWKS keys
        List<Map<String, Object>> jwksKeys = oAuthClientRepository.findByDeletedAtIsNull()
                .stream()
                .map(oAuthClient -> {
                    try {
                        return convertToJwk(oAuthClient);
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
        OAuthClient oAuthClient = oAuthClientRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Token configuration not found with ID: " + id));
        oAuthClientRepository.delete(oAuthClient);
    }

    @Override
    public boolean existsByName(String name) {
        return oAuthClientRepository.existsByName(name);
    }


    // Private utility methods

    private Map<String, Object> convertToJwk(OAuthClient oAuthClient) throws Exception {
        // Extract RSAPublicKey from PEM
        RSAPublicKey publicKey = getPublicKeyFromPem(getPublicKeyByClientId(oAuthClient.getClientId()));

        // Construct the JWK
        return Map.of(
                "kty", "RSA", // Key type
                "kid", String.valueOf(oAuthClient.getSigningKey().getId()), // Key ID
                "use", "sig", // Key usage: signing
                "alg", oAuthClient.getAlgorithm(), // Algorithm (e.g., RS256)
                "n", base64UrlEncode(publicKey.getModulus().toByteArray()), // Modulus (Base64URL-encoded)
                "e", base64UrlEncode(publicKey.getPublicExponent().toByteArray()) // Exponent (Base64URL-encoded)
        );
    }
    public String getPublicKeyByClientId(String clientId) throws Exception {
        OAuthClient oAuthClient = oAuthClientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        Secret secret = oAuthClient.getSigningKey();
        return secret.getPublicKey();
    }
    private RSAPublicKey getPublicKeyFromPem(String publicKeyPem) throws Exception {

        // Remove PEM headers and all whitespace
        String cleanBase64 = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\R", "")
                .replaceAll("\\s+", "")
                .trim();

        // Decode Base64 content
        byte[] keyBytes = Base64.getDecoder().decode(cleanBase64);

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