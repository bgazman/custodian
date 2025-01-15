package consulting.gazman.security.oauth.service;

import consulting.gazman.security.oauth.dto.ClientRegistrationRequest;
import consulting.gazman.security.oauth.dto.ClientRegistrationResponse;

import java.util.Optional;

public interface ClientRegistrationService {
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request);
    Optional<ClientRegistrationResponse> getClientById(String clientId);
    ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request);
    void deleteClient(String clientId);
}