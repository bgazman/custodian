package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.TokenConfigurationRepository;
import consulting.gazman.security.service.TokenConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenConfigurationServiceImpl implements TokenConfigurationService {

    @Autowired
    private TokenConfigurationRepository tokenConfigurationRepository;

    @Override
    public ApiResponse<List<TokenConfiguration>> getAllConfigurations() {
        try {
            List<TokenConfiguration> configurations = tokenConfigurationRepository.findAll();
            return ApiResponse.success(configurations, "Token configurations retrieved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving configurations.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<TokenConfiguration> findById(Long id) {
        try {
            TokenConfiguration configuration = tokenConfigurationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Token configuration not found with ID: " + id));
            return ApiResponse.success(configuration, "Token configuration retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Token configuration not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<TokenConfiguration> findByAppName(String appName) {
        try {
            TokenConfiguration configuration = tokenConfigurationRepository.findByAppName(appName)
                    .orElseThrow(() -> new ResourceNotFoundException("Token configuration not found for app: " + appName));
            return ApiResponse.success(configuration, "Token configuration retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Token configuration not found with the provided app name.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<TokenConfiguration> save(TokenConfiguration configuration) {
        try {
            TokenConfiguration savedConfiguration = tokenConfigurationRepository.save(configuration);
            return ApiResponse.success(savedConfiguration, "Token configuration saved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while saving the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Void> delete(Long id) {
        try {
            TokenConfiguration configuration = tokenConfigurationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Token configuration not found with ID: " + id));
            tokenConfigurationRepository.delete(configuration);
            return ApiResponse.success(null, "Token configuration deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Token configuration not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while deleting the configuration.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Boolean> existsByAppName(String appName) {
        try {
            boolean exists = tokenConfigurationRepository.existsByAppName(appName);
            return ApiResponse.success(exists, "Existence check completed successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while checking configuration existence.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
}
