package consulting.gazman.security.client.user.repository;

import consulting.gazman.security.client.user.entity.UserClientRegistration;
import consulting.gazman.security.client.user.entity.UserClientRegistrationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserClientRegistrationRepository extends JpaRepository<UserClientRegistration, UserClientRegistrationId> {
    // Find by user
    List<UserClientRegistration> findByUser_Id(Long userId);
    Optional<UserClientRegistration> findByUser_IdAndClient_ClientId(Long userId, String clientId);

    // Find by client
    List<UserClientRegistration> findByClient_Id(Long clientId);

    // Check existence
    boolean existsByUser_IdAndClient_ClientId(Long userId, String clientId);

    // Find verified registrations
    List<UserClientRegistration> findByUser_IdAndEmailVerifiedTrue(Long userId);
    List<UserClientRegistration> findByClient_IdAndEmailVerifiedTrue(Long clientId);

    // Find MFA enabled registrations
    List<UserClientRegistration> findByUser_IdAndMfaEnabledTrue(Long userId);

    // Find by consent status
    @Query("SELECT ucr FROM UserClientRegistration ucr WHERE ucr.user.id = :userId AND ucr.consentGrantedAt IS NOT NULL")
    List<UserClientRegistration> findConsentedRegistrationsByUserId(@Param("userId") Long userId);

    // Find active registrations (used within last X days)
    @Query("SELECT ucr FROM UserClientRegistration ucr WHERE ucr.lastUsedAt >= :cutoffDate")
    List<UserClientRegistration> findActiveRegistrations(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Additional utility methods
    List<UserClientRegistration> findByEmailVerified(boolean emailVerified);
    List<UserClientRegistration> findByMfaEnabled(boolean mfaEnabled);
    List<UserClientRegistration> findByMfaMethod(String mfaMethod);
    List<UserClientRegistration> findByLastUsedAtAfter(LocalDateTime date);
    List<UserClientRegistration> findByConsentGrantedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}