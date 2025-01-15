package consulting.gazman.security.user.service;


import consulting.gazman.security.user.entity.Role;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.user.dto.GroupDTO;
import consulting.gazman.security.user.dto.UserAttributeDTO;
import consulting.gazman.security.user.dto.UserSecurityUpdateRequest;
import consulting.gazman.security.user.dto.UserStatusUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();
    User findById(Long id);

    User save(User user);


    void delete(Long id);
    boolean existsByEmail(String email);

    User findByEmail(String email);

    Optional<User> findByEmailOptional(String email);

    void enableUser(Long id); // Enable a user account
    void disableUser(Long id); // Disable a user account

    void changePassword(Long id, String newPassword);

    void verifyEmail(Long id); // Mark a user's email as verified
    void resetFailedLoginAttempts(Long id); // Reset failed login attempts counter
    void trackLogin(Long id); // Update last login time

    User createUser(User user, Set<Long> roleIds, Set<Long> groupIds);

    String getPhoneNumber(String email);

    User updateUser(User user);

    boolean isEmailRegistered(String email);

    void updatePassword(String email, String newPassword);

    List<Role> getUserRoles(Long id);

    List<GroupDTO> getUserGroups(Long id);

    List<UserAttributeDTO> getUserAttributes(Long id);

    void updateUserRoles(Long userId, Set<Long> roleIds);

    void updateUserGroups(Long userId, Set<Long> groupIds);

    User updateUserStatus(Long id, UserStatusUpdateRequest request);

    User updateUserSecurity(Long id, UserSecurityUpdateRequest request);


}

