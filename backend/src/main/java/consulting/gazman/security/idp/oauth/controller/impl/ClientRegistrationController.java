package consulting.gazman.security.idp.oauth.controller.impl;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.idp.oauth.dto.ClientRegistrationRequest;
import consulting.gazman.security.idp.oauth.dto.ClientRegistrationResponse;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.oauth.service.impl.ClientRegistrationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller
@RestController
public class ClientRegistrationController extends ApiController implements consulting.gazman.security.idp.oauth.controller.IClientRegistrationController {

    private final ClientRegistrationServiceImpl clientRegistrationService;

    public ClientRegistrationController(ClientRegistrationServiceImpl clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    @Override
    public ResponseEntity<?> registerClient(@RequestBody ClientRegistrationRequest request) {
        try {
            ClientRegistrationResponse response = clientRegistrationService.registerClient(request);
            return wrapSuccessResponse(response, "Client registered successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}




