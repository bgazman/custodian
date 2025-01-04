package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public List<User> getAllUsers() {
        // Fetch all users from the database
        return  userRepository.findAll();


    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
    }

    @Override
    public User save(User user) {
;
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));

        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); // Assuming the repository method returns Optional<User>
    }

    @Override
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void verifyEmail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public void resetFailedLoginAttempts(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }

    @Override
    public void trackLogin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public User createUser(User user) {
        // Check if the email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw AppException.userAlreadyExists("A user with this email already exists");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default properties
        user.setEmailVerified(false); // Default to unverified
        user.setEnabled(false);       // Default to disabled
//        user.setRegisteredAt(LocalDateTime.now());
        user.setLastPasswordChange(LocalDateTime.now());

        // Save the user to the database
        return userRepository.save(user);
    }

    @Override
    public String getPhoneNumber(String email) {
        return userRepository.findByEmail(email)
                .map(User::getPhoneNumber) // Assuming `phoneNumber` is a field in the `User` entity
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", "User not found with email: " + email));
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new AppException("INVALID_USER", "User ID cannot be null");
        }

        try {
            // First check if user exists
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new AppException("USER_NOT_FOUND", "User not found with id: " + user.getId()));

            // Update only non-null fields
            if (user.getName() != null) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                // Check if new email is already taken by another user
                userRepository.findByEmailAndIdNot(user.getEmail(), user.getId())
                        .ifPresent(u -> {
                            throw new AppException("EMAIL_TAKEN", "Email is already in use");
                        });
                existingUser.setEmail(user.getEmail());
            }
            if (user.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(user.getPhoneNumber());
            }
            if (user.isMfaEnabled()) {
                existingUser.setMfaEnabled(user.isMfaEnabled());
            }
            if (user.getMfaMethod() != null) {
                existingUser.setMfaMethod(user.getMfaMethod());
            }
            if (user.getMfaBackupCodes() != null) {
                existingUser.setMfaBackupCodes(user.getMfaBackupCodes());
            }

            // Don't update password here - should have a separate method for that
            // Don't update email_verified status here - should have a separate method for that

            existingUser.setUpdatedAt(LocalDateTime.now());

            userRepository.save(existingUser);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("UPDATE_FAILED", "Failed to update user: " + e.getMessage());
        }
    }

    @Override
    public boolean isEmailRegistered(String email) {
        // Check if a user with the given email exists
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found in the system."));

        // Hash the new password
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Update the password
        user.setPassword(hashedPassword);
        userRepository.save(user);

    }
}


