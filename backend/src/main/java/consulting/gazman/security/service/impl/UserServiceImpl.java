package consulting.gazman.security.service.impl;

import consulting.gazman.security.dto.GroupDTO;
import consulting.gazman.security.dto.UserAttributeDTO;
import consulting.gazman.security.dto.UserSecurityUpdateRequest;
import consulting.gazman.security.dto.UserStatusUpdateRequest;
import consulting.gazman.security.entity.*;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.service.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
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
    UserRoleService userRoleService;

    @Autowired
    GroupService groupService;
    @Autowired
    GroupMembershipService groupMembershipService;
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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.userNotFound("User with email " + email + " not found"));
    }
    @Override
    public Optional<User> findByEmailOptional(String email) {
        return userRepository.findByEmail(email);
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
    public User createUser(User user, Set<Long> roleIds, Set<Long> groupIds) {
        // Step 1: Save the user (persist the User entity)
        User savedUser = userRepository.save(user);

        // Step 2: Assign roles to the user
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<UserRole> userRoles = roleService.resolveRolesForUser(savedUser, roleIds);
            savedUser.setUserRoles(userRoles); // In-memory update
        }

        // Step 3: Assign groups to the user
        if (groupIds != null && !groupIds.isEmpty()) {
            groupMembershipService.assignUserToGroups(savedUser.getId(), groupIds);
        }

        // Return the saved user
        return savedUser;
    }

    // Update an existing user
    public User updateUser(Long userId, User updatedUser, Set<Long> roleIds, Set<Long> groupIds) {
        // Fetch the existing user
        User existingUser = findById(userId);

        // Update fields (basic details like name, email, etc.)
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setEnabled(updatedUser.isEnabled());

        // Update roles
        Set<UserRole> updatedRoles = roleService.resolveRolesForUser(existingUser, roleIds);
        existingUser.getUserRoles().clear();
        existingUser.getUserRoles().addAll(updatedRoles);


        // Save the updated user
        return userRepository.save(existingUser);
    }



    @Override
    public String getPhoneNumber(String email) {
        return userRepository.findByEmail(email)
                .map(User::getPhoneNumber) // Assuming `phoneNumber` is a field in the `User` entity
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", "User not found with email: " + email));
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> AppException.userNotFound("User not found"));

        BeanUtils.copyProperties(user, existingUser, "id", "userRoles", "password");
        return userRepository.save(existingUser);


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

    @Override
    public List<Role> getUserRoles(Long id) {
        return List.of();
    }

    @Override
    public List<GroupDTO> getUserGroups(Long id) {
        return List.of();
    }

    @Override
    public List<UserAttributeDTO> getUserAttributes(Long id) {
        return List.of();
    }
    @Override
    public void updateUserRoles(Long userId, Set<Long> newRoleIds) {
        // Fetch the user, throw AppException if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.userNotFound("User with ID " + userId + " not found"));

        // Validate the provided role IDs by fetching them from the roles table
        Set<Role> validRoles = roleService.findAllById(newRoleIds).stream().collect(Collectors.toSet());
        if (validRoles.size() != newRoleIds.size()) {
            throw AppException.badRequest("One or more roles do not exist");
        }

        // Use the UserRoleService to update the user's roles
        userRoleService.updateUserRoles(user, newRoleIds);
    }



    @Override
    public void updateUserGroups(Long userId, Set<Long> groupIds) {
        // Fetch the user, throw AppException if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.userNotFound("User with ID " + userId + " not found"));

        // Fetch and validate group IDs by retrieving the actual Group entities from the database
        Set<Group> groups = groupService.findAllById(groupIds).stream().collect(Collectors.toSet());
        if (groups.size() != groupIds.size()) {
            throw AppException.badRequest("One or more groups do not exist");
        }

        // Delete existing group memberships for the user
        groupMembershipService.deleteByUserId(userId);

        // Create new group memberships using the fetched Group entities
        List<GroupMembership> newMemberships = groups.stream()
                .map(group -> new GroupMembership(user, group))  // Constructor without Role
                .collect(Collectors.toList());

        // Save the new group memberships
        groupMembershipService.saveAll(newMemberships);
    }





    @Override
    public User updateUserSecurity(Long userId, UserSecurityUpdateRequest request) {
        // Fetch the user, throw AppException if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.userNotFound("User with ID " + userId + " not found"));

        // Update the user's password (assume the password is hashed elsewhere, e.g., in a service)
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(request.getPassword()); // Assuming passwordEncoder is a Bean
            user.setPassword(hashedPassword);
        }

        // Update other security-related fields (if any)
        if (request.isMfaEnabled()) {
            user.setMfaEnabled(true);
        }

        // Save the updated user
        return userRepository.save(user);
    }

    @Override
    public User updateUserStatus(Long userId, UserStatusUpdateRequest request) {
        // Fetch the user, throw AppException if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.userNotFound("User with ID " + userId + " not found"));

        // Update the user's status fields
        if (request.isEnabled()) {
            user.setEnabled(true);
        }
        if (request.isEmailVerified()) {
            user.setEmailVerified(true);
        }

        // Save the updated user
        return userRepository.save(user);

    }

}


