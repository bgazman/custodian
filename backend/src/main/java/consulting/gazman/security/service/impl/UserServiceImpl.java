package consulting.gazman.security.service.impl;

import consulting.gazman.security.dto.UserRequest;
import consulting.gazman.security.entity.Role;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.entity.UserRole;
import consulting.gazman.security.entity.UserRoleId;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.repository.UserRoleRepository;
import consulting.gazman.security.service.RoleService;
import consulting.gazman.security.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleService roleService;

    @Autowired
    UserRoleRepository userRoleRepository;

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

    public User update(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));

        // Update basic fields
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setEnabled(userDetails.isEnabled());
        user.setMfaEnabled(userDetails.isMfaEnabled());
        user.setMfaMethod(userDetails.getMfaMethod());
        user.setMfaBackupCodes(userDetails.getMfaBackupCodes());
        user.setEmailVerified(userDetails.isEmailVerified());
        user.setAccountNonExpired(userDetails.isAccountNonExpired());
        user.setAccountNonLocked(userDetails.isAccountNonLocked());
        user.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());
        user.setLastLoginTime(userDetails.getLastLoginTime());
        user.setLastPasswordChange(userDetails.getLastPasswordChange());

        // Update roles safely
        Set<UserRole> existingRoles = user.getUserRoles();
        Set<UserRole> newRoles = userDetails.getUserRoles();

        // Remove roles no longer present
        existingRoles.removeIf(role -> newRoles.stream()
                .noneMatch(newRole -> newRole.getId().equals(role.getId())));

        // Add or update roles
        for (UserRole newRole : newRoles) {
            UserRole existingRole = existingRoles.stream()
                    .filter(role -> role.getId().equals(newRole.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingRole == null) {
                // Fetch a managed entity from the database if it exists
                UserRole managedRole = userRoleRepository.findById(newRole.getId())
                        .orElse(newRole); // If not found, use the new role
                managedRole.setUser(user); // Maintain bidirectional relationship
                existingRoles.add(managedRole);
            }
        }

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

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign roles to the user
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            throw AppException.badRequest("At least one role is required for the user");
        }

        Set<UserRole> userRoles = user.getUserRoles().stream()
                .map(userRole -> {
                    Role role = roleService.findById(userRole.getRole().getId());

                    UserRole newUserRole = new UserRole();
                    newUserRole.setUser(user);
                    newUserRole.setRole(role);
                    return newUserRole;
                })
                .collect(Collectors.toSet());

        user.setUserRoles(userRoles);

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
    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new AppException("INVALID_USER", "User ID cannot be null");
        }

        // Fetch the existing user
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", "User not found with ID: " + user.getId()));

        // Update basic fields only if they are not null
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userRepository.findByEmailAndIdNot(user.getEmail(), user.getId())
                    .ifPresent(existing -> {
                        throw new AppException("EMAIL_TAKEN", "The email is already in use by another user");
                    });
            existingUser.setEmail(user.getEmail());
        }

        if (user.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }

        if (user.isMfaEnabled()) {
            existingUser.setMfaEnabled(true);
        }

        if (user.getMfaMethod() != null) {
            existingUser.setMfaMethod(user.getMfaMethod());
        }

        if (user.getMfaBackupCodes() != null) {
            existingUser.setMfaBackupCodes(user.getMfaBackupCodes());
        }

        // Handle role updates
        if (user.getUserRoles() != null) {
            Set<UserRole> updatedRoles = user.getUserRoles().stream()
                    .map(userRole -> {
                        Role role = roleService.findById(userRole.getRole().getId());
                        UserRoleId userRoleId = new UserRoleId();
                        userRoleId.setUserId(existingUser.getId());
                        userRoleId.setRoleId( role.getId());
                        // Fetch managed UserRole or create new if it doesn't exist
                        return userRoleRepository.findById(userRoleId)
                                .orElseGet(() -> {
                                    UserRole newUserRole = new UserRole();
                                    newUserRole.setId(userRoleId);
                                    newUserRole.setUser(existingUser);
                                    newUserRole.setRole(role);
                                    return newUserRole;
                                });
                    })
                    .collect(Collectors.toSet());

            // Update roles in the existing user
            existingUser.getUserRoles().clear();
            existingUser.getUserRoles().addAll(updatedRoles);
        }

        // Update the last updated timestamp
        existingUser.setUpdatedAt(LocalDateTime.now());

        // Save the updated user
        userRepository.save(existingUser);
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


