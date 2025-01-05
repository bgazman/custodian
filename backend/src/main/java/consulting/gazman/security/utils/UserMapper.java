package consulting.gazman.security.utils;

import consulting.gazman.security.dto.UserRequest;
import consulting.gazman.security.dto.UserResponse;
import consulting.gazman.security.entity.Role;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.entity.UserRole;
import consulting.gazman.security.entity.UserRoleId;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.RoleService;

import java.util.HashSet;
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
// Explicitly set mfaMethod, including null values
        user.setMfaMethod(userRequest.getMfaMethod());

        if (userRequest.getMfaBackupCodes() != null) {
            user.setMfaBackupCodes(userRequest.getMfaBackupCodes());
        }

        if (userRequest.getRoleIds() != null) {  // Changed from checking isEmpty()
            Set<UserRole> userRoles = userRequest.getRoleIds().stream()
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        UserRoleId id = new UserRoleId();
                        id.setUserId(user.getId());
                        id.setRoleId(roleId);
                        userRole.setId(id);
                        userRole.setUser(user);
                        userRole.setRole(roleService.findById(roleId));
                        return userRole;
                    })
                    .collect(Collectors.toSet());
            user.getUserRoles().clear();
            user.getUserRoles().addAll(userRoles);
        }

        return user;
    }


}
