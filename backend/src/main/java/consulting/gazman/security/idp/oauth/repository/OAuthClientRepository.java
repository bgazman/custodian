package consulting.gazman.security.idp.oauth.repository;

import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, Long> {
    Optional<OAuthClient> findByClientId(String clientId);
    List<OAuthClient> findByDeletedAtIsNull();

    boolean existsByName(String name);

}