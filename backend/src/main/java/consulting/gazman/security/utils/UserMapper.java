package consulting.gazman.security.utils;

import consulting.gazman.security.dto.UserRequest;
import consulting.gazman.security.dto.UserResponse;
import consulting.gazman.security.entity.Role;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.entity.UserRole;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.RoleService;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(UserRequest userRequest, RoleService roleService) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword()); // Password encoding should be handled in the service layer
        user.setEnabled(userRequest.getEnabled() != null ? userRequest.getEnabled() : false);

        // Map role IDs to UserRole entities
        if (userRequest.getRoleIds() != null && !userRequest.getRoleIds().isEmpty()) {
            Set<UserRole> userRoles = userRequest.getRoleIds().stream()
                    .map(roleId -> {
                        Role role = roleService.findById(roleId);

                        UserRole userRole = new UserRole();
                        userRole.setUser(user);
                        userRole.setRole(role);
                        return userRole;
                    })
                    .collect(Collectors.toSet());
            user.setUserRoles(userRoles);
        } else {
            throw new AppException("MISSING_ROLES", "At least one role is required for the user.");
        }

        return user;
    }


    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        return response;
    }
}
