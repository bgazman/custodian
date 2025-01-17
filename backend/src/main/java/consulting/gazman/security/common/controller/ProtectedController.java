package consulting.gazman.security.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProtectedController implements IProtectedController {

    private static ResponseEntity<Map<String, String>> createProtectedDataResponse() {
        // Fetch the current authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the username of the authenticated user
        String username = authentication.getName();

        // Build the response
        Map<String, String> response = new HashMap<>();
        response.put("message", username);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }
    @Override
    public ResponseEntity<Map<String, String>> getProtectedData() {


        return createProtectedDataResponse();
    }

    @Override
    public ResponseEntity<Map<String, String>> adminOnlyAction() {
        return createProtectedDataResponse();
    }

    @Override
    public ResponseEntity<Map<String, String>> auditReadAccess() {
        return createProtectedDataResponse();
    }

    @Override
    public ResponseEntity<Map<String, String>> managerOnlyAccess() {
        return createProtectedDataResponse();
    }

    @Override
    public ResponseEntity<Map<String, String>> customPermissionAccess() {
        return createProtectedDataResponse();
    }
}