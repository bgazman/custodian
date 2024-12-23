package consulting.gazman.security.controller;



import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.repository.TokenConfigurationRepository;
import consulting.gazman.security.service.TokenConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/.well-known")
public class JwksController extends ApiController {


    @Autowired
    private TokenConfigurationService tokenConfigurationService;
    @GetMapping("/jwks.json")
    public ResponseEntity<?> getJwks() {
        logRequest("GET", "/.well-known/jwks.json");

        // Call the service to get the JWKS response
        ApiResponse<Map<String, Object>> serviceResponse = tokenConfigurationService.getJwks();
        return handleApiResponse(serviceResponse);
    }


}
