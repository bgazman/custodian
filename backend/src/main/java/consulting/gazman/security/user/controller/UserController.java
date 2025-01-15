package consulting.gazman.security.user.controller;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.service.RoleService;
import consulting.gazman.security.user.service.UserRoleService;
import consulting.gazman.security.user.service.UserService;
import consulting.gazman.security.user.dto.*;
import consulting.gazman.security.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController

public class UserController extends ApiController implements IUserController {

    @Autowired
    private UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserMapper userMapper;

    @GetMapping
    @Override
    public ResponseEntity<List<UserBasicDTO>> getAllUsers() {
        try {
            // Fetch users and map to DTOs
            List<User> users = userService.getAllUsers();
            List<UserBasicDTO> userDTOs = userMapper.toBasicDTOList(users);

            // Use the abstract controller's utility method
            return super.wrapSuccessResponse(userDTOs,"User list retrieved");
        } catch (AppException e) {
            // Application-specific error
            return (ResponseEntity) super.wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General error
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserDetailsDTO> getUser(Long id) {
        try {
            User user = userService.findById(id);
            UserDetailsDTO userDetailsDTO = userMapper.toDetailsDTO(user);

            return super.wrapSuccessResponse(userDetailsDTO,"User found");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserDetailsDTO> createUser(UserCreateRequest request) {
        try {
            // Map the UserCreateRequest to a User entity
            User user = userMapper.toEntity(request);

            // Extract role IDs and group IDs from the request
            Set<Long> roleIds = request.getRoleIds();
            Set<Long> groupIds = request.getGroupIds();

            // Call the service method to create the user with roles and groups
            User createdUser = userService.createUser(user, roleIds, groupIds);

            // Map the created user to a UserDetailsDTO
            UserDetailsDTO userDetailsDTO = userMapper.toDetailsDTO(createdUser);

            // Return the success response
            return super.wrapSuccessResponse(userDetailsDTO, "User created successfully");
        } catch (AppException e) {
            // Handle application-specific validation errors
            return (ResponseEntity) super.wrapErrorResponse("VALIDATION_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Handle unexpected errors
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<UserDetailsDTO> updateUser(Long id, UserUpdateRequest request) {
        try {
            // Fetch the existing user by ID
            User existingUser = userService.findById(id);

            // Update the existing user with the fields from UserUpdateRequest using the mapper
            userMapper.updateEntity(request, existingUser);

            // Update roles and groups if provided
            Set<Long> roleIds = request.getRoleIds();
            Set<Long> groupIds = request.getGroupIds();
            if (roleIds != null && !roleIds.isEmpty()) {
                userService.updateUserRoles(existingUser.getId(), roleIds);
            }
            if (groupIds != null && !groupIds.isEmpty()) {
                userService.updateUserGroups(existingUser.getId(), groupIds);
            }

            // Save the updated user
// Update the existing user with the request data
            userMapper.updateEntity(request, existingUser);

// Save the updated user
            User updatedUser = userService.updateUser(existingUser);

            // Map the updated user to UserDetailsDTO
            UserDetailsDTO userDetailsDTO = userMapper.toDetailsDTO(updatedUser);

            // Return the success response
            return super.wrapSuccessResponse(userDetailsDTO, "User updated successfully");
        } catch (AppException e) {
            // Handle case where the user is not found
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle unexpected errors
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build(); // RESTful DELETE with no content
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserStatusDTO> updateUserStatus(Long id, UserStatusUpdateRequest request) {
        try {
            User user = userService.updateUserStatus(id, request);
            UserStatusDTO userStatusDTO = userMapper.toStatusDTO(user);

            return super.wrapSuccessResponse(userStatusDTO,"User status details retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserSecurityDTO> updateUserSecurity(Long id, UserSecurityUpdateRequest request) {
        try {
            User user = userService.updateUserSecurity(id, request);
            UserSecurityDTO userSecurityDTO = userMapper.toSecurityDTO(user);

            return super.wrapSuccessResponse(userSecurityDTO,"User security details retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserAccessDTO> getUserAccess(Long id) {
        try {
            User user = userService.findById(id);

            // Map the User entity to UserAccessDTO using the mapper
            UserAccessDTO userAccessDTO = userMapper.toAccessDTO(user);

            return super.wrapSuccessResponse(userAccessDTO,"User access details retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserProfileDTO> getUserProfile(Long id) {
        try {
            // Fetch the User entity from the service
            User user = userService.findById(id);

            // Map the User entity to UserProfileDTO using the mapper
            UserProfileDTO userProfileDTO = userMapper.toProfileDTO(user);
            return super.wrapSuccessResponse(userProfileDTO,"User profile retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserBasicDTO> getUserByEmail(String email) {
        try {
            User user = userService.findByEmail(email);
            UserBasicDTO userBasicDTO = userMapper.toBasicDTO(user);

            return super.wrapSuccessResponse(userBasicDTO,"User found");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}