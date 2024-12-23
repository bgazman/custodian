package consulting.gazman.security.service;

import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.TokenConfiguration;

import java.util.List;
import java.util.Map;

public interface TokenConfigurationService {

    // Retrieve all token configurations
    ApiResponse<List<TokenConfiguration>> getAllConfigurations();

    // Retrieve a token configuration by ID
    ApiResponse<TokenConfiguration> findById(Long id);

    // Retrieve a token configuration by app name
    ApiResponse<TokenConfiguration> findByAppName(String appName);

    // Create or update a token configuration
    ApiResponse<TokenConfiguration> save(TokenConfiguration configuration);

    ApiResponse<Map<String, Object>> getJwks();

    // Delete a token configuration by ID
    ApiResponse<Void> delete(Long id);

    // Check if a configuration exists by app name
    ApiResponse<Boolean> existsByAppName(String appName);


}
