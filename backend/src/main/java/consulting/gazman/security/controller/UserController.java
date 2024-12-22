package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController extends ApiController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logRequest("GET", "/api/users");

        // Call the service and handle the response
        ApiResponse<List<User>> serviceResponse = userService.getAllUsers();
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        logRequest("GET", "/api/users/" + id);

        // Call the service and handle the response
        ApiResponse<User> serviceResponse = userService.findById(id);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logRequest("POST", "/api/users");

        // Call the service and handle the response
        ApiResponse<User> serviceResponse = userService.save(user);
        return handleApiResponse(serviceResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        logRequest("PUT", "/api/users/" + id);

        // Call the service and handle the response
        ApiResponse<User> serviceResponse = userService.update(id, user);
        return handleApiResponse(serviceResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logRequest("DELETE", "/api/users/" + id);

        // Call the service and handle the response
        ApiResponse<Void> serviceResponse = userService.delete(id);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        logRequest("GET", "/api/users/email/" + email);

        ApiResponse<User> serviceResponse = userService.findByEmail(email);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping("/enable/{id}")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        logRequest("POST", "/api/users/enable/" + id);

        ApiResponse<Void> serviceResponse = userService.enableUser(id);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping("/disable/{id}")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        logRequest("POST", "/api/users/disable/" + id);

        ApiResponse<Void> serviceResponse = userService.disableUser(id);
        return handleApiResponse(serviceResponse);
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        logRequest("PUT", "/api/users/password/" + id);

        ApiResponse<Void> serviceResponse = userService.changePassword(id, newPassword);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping("/verify-email/{id}")
    public ResponseEntity<?> verifyEmail(@PathVariable Long id) {
        logRequest("POST", "/api/users/verify-email/" + id);

        ApiResponse<Void> serviceResponse = userService.verifyEmail(id);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping("/reset-failed-attempts/{id}")
    public ResponseEntity<?> resetFailedLoginAttempts(@PathVariable Long id) {
        logRequest("POST", "/api/users/reset-failed-attempts/" + id);

        ApiResponse<Void> serviceResponse = userService.resetFailedLoginAttempts(id);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping("/track-login/{id}")
    public ResponseEntity<?> trackLogin(@PathVariable Long id) {
        logRequest("POST", "/api/users/track-login/" + id);

        ApiResponse<Void> serviceResponse = userService.trackLogin(id);
        return handleApiResponse(serviceResponse);
    }
}
