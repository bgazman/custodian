package consulting.gazman.security.repository;

import consulting.gazman.security.entity.TokenConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenConfigRepository extends JpaRepository<TokenConfiguration, Long> {
    Optional<TokenConfiguration> findByAppName(String appName);
}
