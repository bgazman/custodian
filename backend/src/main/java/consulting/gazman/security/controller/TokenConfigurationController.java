package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.service.TokenConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/token-configurations")
@PreAuthorize("hasRole('ADMIN')")
public class TokenConfigurationController extends ApiController {

    @Autowired
    private TokenConfigurationService tokenConfigurationService;

    @GetMapping
    public ResponseEntity<?> getAllConfigurations() {
        logRequest("GET", "/api/token-configurations");

        ApiResponse<List<TokenConfiguration>> serviceResponse = tokenConfigurationService.getAllConfigurations();
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getConfigurationById(@PathVariable Long id) {
        logRequest("GET", "/api/token-configurations/" + id);

        ApiResponse<TokenConfiguration> serviceResponse = tokenConfigurationService.findById(id);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/app")
    public ResponseEntity<?> getConfigurationByAppName(@RequestParam String appName) {
        logRequest("GET", "/api/token-configurations/app?appName=" + appName);

        ApiResponse<TokenConfiguration> serviceResponse = tokenConfigurationService.findByAppName(appName);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping
    public ResponseEntity<?> createConfiguration(@RequestBody TokenConfiguration configuration) {
        logRequest("POST", "/api/token-configurations");

        ApiResponse<TokenConfiguration> serviceResponse = tokenConfigurationService.save(configuration);
        return handleApiResponse(serviceResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateConfiguration(@PathVariable Long id, @RequestBody TokenConfiguration configuration) {
        logRequest("PUT", "/api/token-configurations/" + id);

        configuration.setId(id); // Ensure the ID is set for update
        ApiResponse<TokenConfiguration> serviceResponse = tokenConfigurationService.save(configuration);
        return handleApiResponse(serviceResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConfiguration(@PathVariable Long id) {
        logRequest("DELETE", "/api/token-configurations/" + id);

        ApiResponse<Void> serviceResponse = tokenConfigurationService.delete(id);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/exists")
    public ResponseEntity<?> checkConfigurationExists(@RequestParam String appName) {
        logRequest("GET", "/api/token-configurations/exists?appName=" + appName);

        ApiResponse<Boolean> serviceResponse = tokenConfigurationService.existsByAppName(appName);
        return handleApiResponse(serviceResponse);
    }
}
