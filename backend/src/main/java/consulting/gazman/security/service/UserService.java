package consulting.gazman.security.service;


import consulting.gazman.security.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User findById(Long id);
    User save(User user);
    User update(Long id, User user);
    void delete(Long id);
    boolean existsByEmail(String email);
    // Additional methods
    User findByEmail(String email); // Fetch user by email
    void enableUser(Long id); // Enable a user account
    void disableUser(Long id); // Disable a user account
    void changePassword(Long id, String newPassword); // Update user password
    void verifyEmail(Long id); // Mark a user's email as verified
    void resetFailedLoginAttempts(Long id); // Reset failed login attempts counter
    void trackLogin(Long id); // Update last login time
}

