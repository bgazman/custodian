package consulting.gazman.security.repository;

import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.TokenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository
public interface TokenConfigurationRepository extends JpaRepository<TokenConfiguration, TokenId> {


    Optional<TokenConfiguration> findByTokenIdAppName(String appName); // Find by app name in the composite key

    boolean existsByTokenIdAppName(String appName); // Check existence by app name in the composite key

}
