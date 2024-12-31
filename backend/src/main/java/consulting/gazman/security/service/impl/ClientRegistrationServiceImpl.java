package consulting.gazman.security.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.dto.ClientRegistrationRequest;
import consulting.gazman.security.dto.ClientRegistrationResponse;
import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.Secret;
import consulting.gazman.security.entity.Tenant;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.SecretRepository;
import consulting.gazman.security.repository.TenantRepository;
import consulting.gazman.security.service.ClientRegistrationService;
import consulting.gazman.security.service.OAuthClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientRegistrationServiceImpl implements ClientRegistrationService {
    private final SecretServiceImpl secretService;
    private final OAuthClientService oAuthClientService;
    private final ObjectMapper objectMapper;
    private final TenantRepository tenantRepository;
    public ClientRegistrationServiceImpl(SecretServiceImpl secretService, OAuthClientService oAuthClientService, ObjectMapper objectMapper, TenantRepository tenantRepository) {
        this.secretService = secretService;
        this.oAuthClientService = oAuthClientService;
        this.objectMapper = objectMapper;
        this.tenantRepository = tenantRepository;
    }
//    private Secret getOrCreateDefaultSecret() {
//        return secretRepository.findByName("default-signing-key")
//                .orElseGet(() -> {
//                    Secret secret = new Secret();
//                    secret.setName("default-signing-key");
//                    secret.setPublicKey("-----BEGIN PUBLIC KEY-----...");
//                    secret.setPrivateKey("-----BEGIN PRIVATE KEY-----...");
//                    secret.setType("RSA");
//                    secret.setActive(true);
//                    return secretRepository.save(secret);
//                });
//    }

    @Transactional
    @Override
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        // Validate the request
        validateRegistrationRequest(request);
        if (oAuthClientService.existsByName(request.getName())) {
            throw  AppException.userAlreadyExists("CLIENT_ALREADY_EXISTS");
        }

        // Fetch the tenant
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> AppException.resourceNotFound("TENANT_NOT_FOUND"));

        // Generate client_id and client_secret first
        String clientId = generateClientId();
        String clientSecret = generateClientSecret();

        // Generate a client-specific signing key
        Secret clientSigningKey = secretService.createClientSigningKey(request.getName());

        // Create the OAuth client
        OAuthClient client = OAuthClient.builder()
                .tenant(tenant)
                .clientId(clientId) // Use pre-generated client_id
                .clientSecret(clientSecret) // Use pre-generated client_secret
                .clientSecretLastRotated(LocalDateTime.now())
                .name(request.getName())
                .status("active")
                .redirectUris(request.getRedirectUris())
                .grantTypes(request.getGrantTypes())
                .scopes(request.getScopes())
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
                .scopes(savedClient.getScopes())
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
                .orElseThrow(() -> new AppException("CLIENT_NOT_FOUND", "Client not found"));

        // Update fields
        existingClient.setName(request.getName());
        existingClient.setRedirectUris(request.getRedirectUris());  // Direct List assignment
        existingClient.setGrantTypes(request.getGrantTypes());      // Direct List assignment
        existingClient.setScopes(request.getScopes());              // Direct List assignment
        existingClient.setApplicationType(request.getApplicationType());
        existingClient.setResponseTypes(request.getResponseTypes()); // Direct List assignment

        OAuthClient updatedClient = oAuthClientService.updateClient(existingClient.getId(), existingClient);
        return convertToResponse(updatedClient);
    }

    @Override
    public void deleteClient(String clientId) {
        OAuthClient client = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> new AppException("CLIENT_NOT_FOUND", "Client not found"));
        oAuthClientService.softDeleteClient(client.getId());
    }

    private ClientRegistrationResponse convertToResponse(OAuthClient client) {
        return ClientRegistrationResponse.builder()
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .name(client.getName())
                .redirectUris(client.getRedirectUris())          // Direct List usage
                .grantTypes(client.getGrantTypes())             // Direct List usage
                .scopes(client.getScopes())                     // Direct List usage
                .clientSecretExpiresAt(0L)                      // Example static value
                .build();
    }

    private void validateRegistrationRequest(ClientRegistrationRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw  AppException.invalidRequest("Client name is required");
        }
        if (request.getRedirectUris() == null || request.getRedirectUris().isEmpty()) {
            throw  AppException.invalidRequest("At least one redirect URI is required");

        }

        // Add more validations as needed
    }

    private String generateClientId() {
        return UUID.randomUUID().toString();
    }

    private String generateClientSecret() {
        return UUID.randomUUID().toString();
    }

    private String convertToJson(List<String> list) {
        try {
            return new ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert list to JSON", e);
        }
    }
    private List<String> parseJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {}); // Deserialize JSON string to List
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to List", e);
        }
    }

}