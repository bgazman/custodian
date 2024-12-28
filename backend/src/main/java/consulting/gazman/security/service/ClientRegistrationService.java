package consulting.gazman.security.service;

import consulting.gazman.security.dto.ClientRegistrationRequest;
import consulting.gazman.security.dto.ClientRegistrationResponse;

import java.util.Optional;

public interface ClientRegistrationService {
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request);
    Optional<ClientRegistrationResponse> getClientById(String clientId);
    ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request);
    void deleteClient(String clientId);
}