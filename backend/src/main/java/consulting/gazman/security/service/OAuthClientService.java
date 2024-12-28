package consulting.gazman.security.service;

import consulting.gazman.security.entity.OAuthClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OAuthClientService {
    OAuthClient createClient(OAuthClient client);
    OAuthClient getClientById(Long id);
    Optional<OAuthClient> getClientByClientId(String clientId);
    List<OAuthClient> getAllClients();
    OAuthClient updateClient(Long id, OAuthClient client);
    void deleteClient(Long id);
    void softDeleteClient(Long id);
    boolean validateClientSecret(String clientId, String clientSecret);
    boolean validateRedirectUri(String clientId, String redirectUri);
    boolean validateGrantType(String clientId, String grantType);
    boolean validateScope(String clientId, String scope);

    Map<String, Object> getJwks();

    void delete(Long id);

    boolean existsByName(String name);
}