package consulting.gazman.security.user.service.impl;

import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.service.OAuthClientService;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.user.entity.UserConsent;
import consulting.gazman.security.user.entity.UserConsentId;
import consulting.gazman.security.user.repository.UserConsentRepository;
import consulting.gazman.security.user.service.UserConsentService;
import consulting.gazman.security.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserConsentServiceImpl implements UserConsentService {

    @Autowired
    private UserConsentRepository userConsentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OAuthClientService clientService;

    @Override
    public boolean hasValidConsent(Long userId, Long clientId, String[] requestedScopes) {
        Optional<UserConsent> existingConsent = userConsentRepository.findByUserIdAndClientId(userId, clientId);

        if (existingConsent.isEmpty()) {
            return false;
        }

        UserConsent consent = existingConsent.get();

        // Check if consent is expired
        if (consent.getExpiresAt() != null && LocalDateTime.now().isAfter(consent.getExpiresAt())) {
            return false;
        }

        // Check if all requested scopes are already approved
        List<String> approvedScopes = consent.getScopes();
        return Arrays.stream(requestedScopes).allMatch(approvedScopes::contains);
    }
    @Override
    public void saveConsent(Long userId, Long clientId, List<String> scopes) {
        User user = userService.findById(userId);
        Optional<OAuthClient> clientOptional = clientService.findById(clientId);

        if (clientOptional.isEmpty()) {
            throw new IllegalArgumentException("Client not found for id: " + clientId);
        }

        OAuthClient client = clientOptional.get();

        UserConsent consent = userConsentRepository.findByUserIdAndClientId(userId, clientId)
                .orElse(new UserConsent());

        if (consent.getId() == null) {
            UserConsentId id = new UserConsentId(userId, clientId); // Ensure userId and clientId are set
            consent.setId(id);
            consent.setUser(user);
            consent.setClient(client);  // Ensure client is set
        }

        consent.setScopes(scopes);
        consent.setExpiresAt(LocalDateTime.now().plusDays(30));

        userConsentRepository.save(consent);
    }

    @Override
    public void revokeConsent(Long userId, Long clientId) {
        userConsentRepository.deleteByUserIdAndClientId(userId, clientId);
    }

    @Override
    public List<UserConsent> getConsentsForUser(Long userId) {
        return userConsentRepository.findByUserId(userId);
    }
}