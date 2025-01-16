package consulting.gazman.security.idp.oauth.service;

import consulting.gazman.security.idp.oauth.dto.ClientRegistrationRequest;
import consulting.gazman.security.idp.oauth.dto.ClientRegistrationResponse;

import java.util.Optional;

public interface ClientRegistrationService {
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request);
    Optional<ClientRegistrationResponse> getClientById(String clientId);
    ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request);
    void deleteClient(String clientId);
}