package consulting.gazman.security.repository;

import consulting.gazman.security.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, Long> {
    Optional<OAuthClient> findByClientId(String clientId);
    List<OAuthClient> findByDeletedAtIsNull();

}