package consulting.gazman.security.client.user.service;

import consulting.gazman.security.client.user.entity.UserClientRegistration;
import consulting.gazman.security.client.user.entity.UserClientRegistrationId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserClientRegistrationService {
    List<UserClientRegistration> getAllRegistrations();

    Optional<UserClientRegistration> findById(UserClientRegistrationId id);

    Optional<UserClientRegistration> findByUserIdAndClientId(Long userId, String clientId);

    List<UserClientRegistration> findByUserId(Long userId);

    List<UserClientRegistration> findByClientId(Long clientId);

    UserClientRegistration save(UserClientRegistration registration);

    void delete(UserClientRegistrationId id);

    List<UserClientRegistration> findByEmailVerified(boolean emailVerified);

    List<UserClientRegistration> findByMfaEnabled(boolean mfaEnabled);

    List<UserClientRegistration> findByMfaMethod(String mfaMethod);

    List<UserClientRegistration> findByLastUsedAtAfter(LocalDateTime date);

    List<UserClientRegistration> findByConsentGrantedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    void updateEmailVerificationStatus(UserClientRegistrationId id, boolean verified);

    void updateMfaSettings(UserClientRegistrationId id, boolean enabled, String mfaMethod);

    void updateLastUsedAt(UserClientRegistrationId id, LocalDateTime lastUsedAt);
}