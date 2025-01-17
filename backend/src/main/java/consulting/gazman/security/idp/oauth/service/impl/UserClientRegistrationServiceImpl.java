package consulting.gazman.security.idp.oauth.service.impl;

import consulting.gazman.security.client.user.entity.UserClientRegistration;
import consulting.gazman.security.client.user.entity.UserClientRegistrationId;
import consulting.gazman.security.client.user.repository.UserClientRegistrationRepository;
import consulting.gazman.security.client.user.service.UserClientRegistrationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserClientRegistrationServiceImpl implements UserClientRegistrationService {

    private final UserClientRegistrationRepository userClientRegistrationRepository;

    public UserClientRegistrationServiceImpl(UserClientRegistrationRepository userClientRegistrationRepository) {
        this.userClientRegistrationRepository = userClientRegistrationRepository;
    }

    @Override
    public List<UserClientRegistration> getAllRegistrations() {
        return userClientRegistrationRepository.findAll();
    }


    @Override
    public Optional<UserClientRegistration> findById(UserClientRegistrationId id) {
        return userClientRegistrationRepository.findById(id);
    }

    @Override
    public void delete(UserClientRegistrationId id) {
        userClientRegistrationRepository.deleteById(id);
    }



    @Override
    public Optional<UserClientRegistration> findByUserIdAndClientId(Long userId, String clientId) {
        return userClientRegistrationRepository.findByUser_IdAndClient_ClientId(userId, clientId);
    }

    @Override
    public List<UserClientRegistration> findByUserId(Long userId) {
        return userClientRegistrationRepository.findByUser_Id(userId);
    }

    @Override
    public List<UserClientRegistration> findByClientId(Long clientId) {
        return userClientRegistrationRepository.findByClient_Id(clientId);
    }

    @Override
    public UserClientRegistration save(UserClientRegistration registration) {
        if (registration.getId() == null) {
            registration.setConsentGrantedAt(LocalDateTime.now());
            registration.setLastUsedAt(LocalDateTime.now());
            registration.setEmailVerified(false);
            registration.setMfaEnabled(false);
        }
        registration.setUpdatedAt(LocalDateTime.now());
        return userClientRegistrationRepository.save(registration);
    }



    @Override
    public List<UserClientRegistration> findByEmailVerified(boolean emailVerified) {
        return userClientRegistrationRepository.findByEmailVerified(emailVerified);
    }

    @Override
    public List<UserClientRegistration> findByMfaEnabled(boolean mfaEnabled) {
        return userClientRegistrationRepository.findByMfaEnabled(mfaEnabled);
    }

    @Override
    public List<UserClientRegistration> findByMfaMethod(String mfaMethod) {
        return userClientRegistrationRepository.findByMfaMethod(mfaMethod);
    }

    @Override
    public List<UserClientRegistration> findByLastUsedAtAfter(LocalDateTime date) {
        return userClientRegistrationRepository.findByLastUsedAtAfter(date);
    }

    @Override
    public List<UserClientRegistration> findByConsentGrantedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return userClientRegistrationRepository.findByConsentGrantedAtBetween(startDate, endDate);
    }



    @Override
    public void updateEmailVerificationStatus(UserClientRegistrationId id, boolean verified) {
        userClientRegistrationRepository.findById(id).ifPresent(registration -> {
            registration.setEmailVerified(verified);
            registration.setUpdatedAt(LocalDateTime.now());
            userClientRegistrationRepository.save(registration);
        });
    }



    @Override
    public void updateMfaSettings(UserClientRegistrationId id, boolean enabled, String mfaMethod) {
        userClientRegistrationRepository.findById(id).ifPresent(registration -> {
            registration.setMfaEnabled(enabled);
            registration.setMfaMethod(mfaMethod);
            registration.setUpdatedAt(LocalDateTime.now());
            userClientRegistrationRepository.save(registration);
        });
    }



    @Override
    public void updateLastUsedAt(UserClientRegistrationId id, LocalDateTime lastUsedAt) {
        userClientRegistrationRepository.findById(id).ifPresent(registration -> {
            registration.setLastUsedAt(lastUsedAt);
            registration.setUpdatedAt(LocalDateTime.now());
            userClientRegistrationRepository.save(registration);
        });
    }
}