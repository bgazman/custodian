package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.dto.UserRequest;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.RoleService;
import consulting.gazman.security.service.UserService;
import consulting.gazman.security.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/secure/users")

public class UserController extends ApiController {

    @Autowired
    private UserService userService;
    @Autowired
    RoleService roleService;
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logRequest("GET", "/api/secure/users");
        try {
            List<User> users = userService.getAllUsers();
            return wrapSuccessResponse(users, "Users retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        logRequest("GET", "/api/users/" + id);
        try {
            User user = userService.findById(id);
            return wrapSuccessResponse(user, "User retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        logRequest("POST", "/api/secure/users");
        try {

            User createdUser = userService.createUser(UserMapper.toEntity(userRequest,roleService,null));
            return wrapSuccessResponse(createdUser, "User created successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        logRequest("PUT", "/api/users/" + id);
        try {
            User existingUser = userService.findById(id);
            User updatedUser = userService.update(id, UserMapper.toEntity(userRequest,roleService,existingUser));
            return wrapSuccessResponse(updatedUser, "User updated successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logRequest("DELETE", "/api/users/" + id);
        try {
            userService.delete(id);
            return wrapSuccessResponse(null, "User deleted successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        logRequest("GET", "/api/users/email/" + email);
        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new AppException("USER_NOT_FOUND", "No user found with email: " + email));
            return wrapSuccessResponse(user, "User retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/enable/{id}")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        logRequest("POST", "/api/users/enable/" + id);
        try {
            userService.enableUser(id);
            return wrapSuccessResponse(null, "User enabled successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/disable/{id}")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        logRequest("POST", "/api/users/disable/" + id);
        try {
            userService.disableUser(id);
            return wrapSuccessResponse(null, "User disabled successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        logRequest("PUT", "/api/users/password/" + id);
        try {
            userService.changePassword(id, newPassword);
            return wrapSuccessResponse(null, "Password changed successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify-email/{id}")
    public ResponseEntity<?> verifyEmail(@PathVariable Long id) {
        logRequest("POST", "/api/users/verify-email/" + id);
        try {
            userService.verifyEmail(id);
            return wrapSuccessResponse(null, "Email verified successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset-failed-attempts/{id}")
    public ResponseEntity<?> resetFailedLoginAttempts(@PathVariable Long id) {
        logRequest("POST", "/api/users/reset-failed-attempts/" + id);
        try {
            userService.resetFailedLoginAttempts(id);
            return wrapSuccessResponse(null, "Failed login attempts reset successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/track-login/{id}")
    public ResponseEntity<?> trackLogin(@PathVariable Long id) {
        logRequest("POST", "/api/users/track-login/" + id);
        try {
            userService.trackLogin(id);
            return wrapSuccessResponse(null, "Login tracked successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
