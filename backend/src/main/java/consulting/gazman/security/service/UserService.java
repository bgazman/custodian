package consulting.gazman.security.service;

import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.User;

import java.util.List;

public interface UserService {

    ApiResponse<List<User>> getAllUsers();
    ApiResponse<User> findById(Long id);
    ApiResponse<User> save(User user);
    ApiResponse<User> update(Long id, User user);
    ApiResponse<Void> delete(Long id);

    // Additional methods
    ApiResponse<User> findByEmail(String email); // Fetch user by email
    ApiResponse<Void> enableUser(Long id); // Enable a user account
    ApiResponse<Void> disableUser(Long id); // Disable a user account
    ApiResponse<Void> changePassword(Long id, String newPassword); // Update user password
    ApiResponse<Void> verifyEmail(Long id); // Mark a user's email as verified
    ApiResponse<Void> resetFailedLoginAttempts(Long id); // Reset failed login attempts counter
    ApiResponse<Void> trackLogin(Long id); // Update last login time
}

