package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ApiResponse<List<User>> getAllUsers() {
        // Fetch all users from the database
        List<User> users = userRepository.findAll();

        // Check if the list is empty and return appropriate response
        if (users.isEmpty()) {
            return ApiResponse.error(
                    "NO_USERS_FOUND",
                    "No users are currently registered."
            );
        }

        // Return success response with the list of users
        return ApiResponse.success(users, "Users retrieved successfully.");
    }

    @Override
    public ApiResponse<User> findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return ApiResponse.success(user, "User retrieved successfully.");
    }

    @Override
    public ApiResponse<User> save(User user) {
        User savedUser = userRepository.save(user);
        return ApiResponse.success(savedUser, "User created successfully.");
    }

    @Override
    public ApiResponse<User> update(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setEmail(userDetails.getEmail());
        // Save updated user
        User updatedUser = userRepository.save(user);

        return ApiResponse.success(updatedUser, "User updated successfully.");
    }

    @Override
    public ApiResponse<Void> delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        userRepository.delete(user);
        return ApiResponse.success(null, "User deleted successfully.");
    }
}
