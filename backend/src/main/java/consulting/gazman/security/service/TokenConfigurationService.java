package consulting.gazman.security.service;


import consulting.gazman.security.entity.TokenConfiguration;

import java.util.List;
import java.util.Map;

public interface TokenConfigurationService {

    // Retrieve all token configurations
    List<TokenConfiguration> getAllConfigurations();

    // Retrieve a token configuration by ID
    TokenConfiguration findById(Long id);

    // Retrieve a token configuration by app name
    TokenConfiguration findByAppName(String appName);

    // Create or update a token configuration
    TokenConfiguration save(TokenConfiguration configuration);

    Map<String, Object> getJwks();

    // Delete a token configuration by ID
    void delete(Long id);

    // Check if a configuration exists by app name
    boolean existsByAppName(String appName);


}
