package consulting.gazman.security.controller;



import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.impl.OAuthClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private OAuthClientServiceImpl oAuthClientService;
    @GetMapping("/jwks.json")
    public ResponseEntity<?> getJwks() {
        logRequest("GET", "/.well-known/jwks.json");
        try {
            Map<String, Object> serviceResponse = oAuthClientService.getJwks();
            return wrapSuccessResponse(serviceResponse, "Groups retrieved successfully for the permission");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
