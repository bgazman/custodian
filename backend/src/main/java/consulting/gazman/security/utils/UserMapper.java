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

    public static User toEntity(UserRequest userRequest, RoleService roleService, User existingUser) {
        User user = existingUser != null ? existingUser : new User();

        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }
        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPassword() != null) {
            user.setPassword(userRequest.getPassword()); // Password encoding should be handled in the service layer
        }
        if (userRequest.getEnabled() != null) {
            user.setEnabled(userRequest.getEnabled());
        }
        if (userRequest.getMfaEnabled() != null) {
            user.setMfaEnabled(userRequest.getMfaEnabled());
        }
        if (userRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(userRequest.getPhoneNumber());
        }
        if (userRequest.getMfaMethod() != null) {
            user.setMfaMethod(userRequest.getMfaMethod());
        }
        if (userRequest.getMfaBackupCodes() != null) {
            user.setMfaBackupCodes(userRequest.getMfaBackupCodes());
        }

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
