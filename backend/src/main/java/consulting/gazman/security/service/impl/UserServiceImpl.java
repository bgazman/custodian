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
        try {
            // Fetch all users from the database
            List<User> users = userRepository.findAll();

            // Check if the list is empty and return appropriate response
            if (users.isEmpty()) {
                return ApiResponse.error(
                        "not_found",
                        "No users are currently registered."
                );
            }

            // Return success response with the list of users
            return ApiResponse.success(users, "Users retrieved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "SERVER_ERROR",
                    "An unexpected error occurred while retrieving users.",
                    ApiError.of("SERVER_ERROR", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<User> findById(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
            return ApiResponse.success(user, "User retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "User not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the user.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<User> save(User user) {
        try {
            User savedUser = userRepository.save(user);
            return ApiResponse.success(savedUser, "User created successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "SERVER_ERROR",
                    "An unexpected error occurred while saving the user.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<User> update(Long id, User userDetails) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

            user.setEmail(userDetails.getEmail());
            // Save updated user
            User updatedUser = userRepository.save(user);

            return ApiResponse.success(updatedUser, "User updated successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "USER_NOT_FOUND",
                    "User not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "SERVER_ERROR",
                    "An unexpected error occurred while updating the user.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Void> delete(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

            userRepository.delete(user);
            return ApiResponse.success(null, "User deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "USER_NOT_FOUND",
                    "User not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "SERVER_ERROR",
                    "An unexpected error occurred while deleting the user.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
}

