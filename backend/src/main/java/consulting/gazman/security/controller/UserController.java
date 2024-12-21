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
}
