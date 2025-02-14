package consulting.gazman.security.idp.oauth.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.idp.oauth.dto.ClientRegistrationRequest;
import consulting.gazman.security.idp.oauth.dto.ClientRegistrationResponse;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.entity.Secret;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.oauth.service.ClientRegistrationService;
import consulting.gazman.security.idp.oauth.service.OAuthClientService;
import consulting.gazman.security.idp.oauth.service.SecretService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientRegistrationServiceImpl implements ClientRegistrationService {
    private final SecretService secretService;
    private final OAuthClientService oAuthClientService;
    private final ObjectMapper objectMapper;

    public ClientRegistrationServiceImpl(SecretService secretService, OAuthClientService oAuthClientService, ObjectMapper objectMapper) {
        this.secretService = secretService;
        this.oAuthClientService = oAuthClientService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        // Validate the request
        validateRegistrationRequest(request);
        if (oAuthClientService.existsByName(request.getName())) {
            throw AppException.userAlreadyExists("CLIENT_ALREADY_EXISTS");
        }

        // Generate client_id and client_secret first
        String clientId = StringUtils.hasText(request.getClientId()) ? request.getClientId() : generateClientId();
        String clientSecret = generateClientSecret();
        System.out.println("Client ID==============" + clientId);
        System.out.println("Client Secret==========" + clientSecret);

        // Generate a client-specific signing key
        Secret clientSigningKey = secretService.createClientSigningKey(request.getName());

        // Create the OAuth client
        OAuthClient client = OAuthClient.builder()
                .clientId(clientId) // Use pre-generated client_id
                .clientSecret(clientSecret) // Use pre-generated client_secret
                .clientSecretLastRotated(LocalDateTime.now())
                .name(request.getName())
                .status("active")
                .redirectUris(request.getRedirectUris())
                .grantTypes(request.getGrantTypes())
                .allowedScopes(request.getAllowedScopes())
                .defaultScopes(request.getDefaultScopes())
                .responseTypes(request.getResponseTypes())
                .applicationType(request.getApplicationType() != null ? request.getApplicationType() : "web")
                .accessTokenExpirySeconds(3600)
                .refreshTokenExpirySeconds(86400)
                .algorithm("RS256")
                .tokenEndpointAuthMethod("client_secret_basic")
                .signingKey(clientSigningKey)
                .build();

        // Save the client
        OAuthClient savedClient = oAuthClientService.createClient(client);

        // Return the response
        return ClientRegistrationResponse.builder()
                .clientId(savedClient.getClientId())
                .clientSecret(clientSecret)
                .name(savedClient.getName())
                .redirectUris(savedClient.getRedirectUris())
                .grantTypes(savedClient.getGrantTypes())
                .allowedScopes(savedClient.getAllowedScopes())
                .defaultScopes(savedClient.getDefaultScopes())
                .keyId(clientSigningKey.getId()) // Include the signing key ID
                .build();
    }

    @Override
    public Optional<ClientRegistrationResponse> getClientById(String clientId) {
        return oAuthClientService.getClientByClientId(clientId)
                .map(this::convertToResponse);
    }

    @Override
    public ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request) {
        OAuthClient existingClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.resourceNotFound("OAuth Client not found with clientId: " + clientId));

        validateRegistrationRequest(request);

        existingClient.setRedirectUris(request.getRedirectUris());
        existingClient.setGrantTypes(request.getGrantTypes());
        existingClient.setAllowedScopes(request.getAllowedScopes());
        existingClient.setDefaultScopes(request.getDefaultScopes());
//        existingClient.setTokenEndpointAuthMethod(request.getTokenEndpointAuthMethod());
//        existingClient.setAlgorithm(request.getAlgorithm());
//
//        if (request.getClientSecret() != null) {
//            existingClient.setClientSecret(request.getClientSecret());
//            existingClient.setClientSecretLastRotated(LocalDateTime.now());
//        }

        OAuthClient updatedClient = oAuthClientService.updateClient(existingClient.getId(), existingClient);

        return convertToResponse(updatedClient);
    }

    @Override
    public void deleteClient(String clientId) {
        OAuthClient client = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.resourceNotFound("OAuth Client not found with clientId: " + clientId));
        oAuthClientService.deleteClient(client.getId());
    }

    private ClientRegistrationResponse convertToResponse(OAuthClient client) {
        return ClientRegistrationResponse.builder()
                .clientId(client.getClientId())
                .name(client.getName())
                .redirectUris(client.getRedirectUris())
                .grantTypes(client.getGrantTypes())
                .allowedScopes(client.getAllowedScopes())
                .defaultScopes(client.getDefaultScopes())
                .keyId(client.getSigningKey().getId())
                .build();
    }

    private void validateRegistrationRequest(ClientRegistrationRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name cannot be empty");
        }

        if (request.getRedirectUris() == null || request.getRedirectUris().isEmpty()) {
            throw new IllegalArgumentException("Redirect URIs cannot be empty");
        }

        if (request.getGrantTypes() == null || request.getGrantTypes().isEmpty()) {
            throw new IllegalArgumentException("Grant types cannot be empty");
        }

        // Allowed scopes can be null but if present should not be empty
        if (request.getAllowedScopes() != null && request.getAllowedScopes().isEmpty()) {
            throw new IllegalArgumentException("Allowed scopes list cannot be empty if provided");
        }

        // Default scopes can be null but if present should not be empty
        if (request.getDefaultScopes() != null && request.getDefaultScopes().isEmpty()) {
            throw new IllegalArgumentException("Default scopes list cannot be empty if provided");
        }
    }

    private String generateClientId() {
        return UUID.randomUUID().toString();
    }

    private String generateClientSecret() {
        return UUID.randomUUID().toString();
    }

    private String convertToJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert list to JSON", e);
        }
    }

    private List<String> parseJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to list", e);
        }
    }
}