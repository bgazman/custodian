package consulting.gazman.security.repository;

import consulting.gazman.security.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, Long> {
    Optional<OAuthClient> findByClientId(String clientId);
    List<OAuthClient> findByDeletedAtIsNull();

    boolean existsByName(String name);

    List<OAuthClient> findByTenantIdAndDeletedAtIsNull(Long tenantId);
}