package consulting.gazman.security.user.repository;
import consulting.gazman.security.user.entity.UserConsent;
import consulting.gazman.security.user.entity.UserConsentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConsentRepository extends JpaRepository<UserConsent, UserConsentId> {
    List<UserConsent> findByUserId(Long userId);
    Optional<UserConsent> findByUserIdAndClientId(Long userId, Long clientId);
    void deleteByUserIdAndClientId(Long userId, Long clientId);
}