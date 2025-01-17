package consulting.gazman.security.common.controller.impl;

import consulting.gazman.security.common.controller.ISecureController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SecureController implements ISecureController {


    @Override
    public ResponseEntity<Map<String, String>> testAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the authenticated user's username

        Map<String, String> response = new HashMap<>();
        response.put("message", "Access granted to authenticated user.");
        response.put("user", username);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> testRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the authenticated user's username

        Map<String, String> response = new HashMap<>();
        response.put("message", "Access granted for SUPER_ADMIN role.");
        response.put("user", username);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> testAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the authenticated user's username

        Map<String, String> response = new HashMap<>();
        response.put("message", "Access granted for SCOPE_USER_READ authority.");
        response.put("user", username);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> testForbiddenAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the authenticated user's username

        Map<String, String> response = new HashMap<>();
        response.put("message", "Access should be forbidden");
        response.put("user", username);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    @Override
    public String debugAuth(Authentication authentication) {
        if (authentication == null) {
            return "No authentication found";
        }

        StringBuilder debug = new StringBuilder();
        debug.append("Authentication class: ").append(authentication.getClass()).append("\n");
        debug.append("Name: ").append(authentication.getName()).append("\n");
        debug.append("Authorities: ").append(authentication.getAuthorities()).append("\n");
        debug.append("Details: ").append(authentication.getDetails()).append("\n");
        debug.append("Principal: ").append(authentication.getPrincipal()).append("\n");

        return debug.toString();
    }
}