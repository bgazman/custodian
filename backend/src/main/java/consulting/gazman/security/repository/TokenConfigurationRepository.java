package consulting.gazman.security.repository;

import consulting.gazman.security.entity.TokenConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository
public interface TokenConfigurationRepository extends JpaRepository<TokenConfiguration, Long> {

    // Find configuration by application name
    Optional<TokenConfiguration> findByAppName(String appName);

    // Check if a configuration exists for a specific app name
    boolean existsByAppName(String appName);
}
