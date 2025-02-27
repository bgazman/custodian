package consulting.gazman.security.user.service;

import consulting.gazman.security.user.entity.UserConsent;

import java.util.List;

public interface UserConsentService {
    boolean hasValidConsent(Long userId, Long clientId, String[] scopes);
    void saveConsent(Long userId, Long clientId, List<String> scopes);
    void revokeConsent(Long userId, Long clientId);
    List<UserConsent> getConsentsForUser(Long userId);
}