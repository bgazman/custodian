package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Permission;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.PermissionRepository;
import consulting.gazman.security.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public ApiResponse<List<Permission>> getAllPermissions() {
        try {
            List<Permission> permissions = permissionRepository.findAll();
            return ApiResponse.success(permissions, "Permissions retrieved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving permissions.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Permission> findById(Long id) {
        try {
            Permission permission = permissionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + id));
            return ApiResponse.success(permission, "Permission retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Permission not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the permission.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Permission> save(Permission permission) {
        try {
            Permission savedPermission = permissionRepository.save(permission);
            return ApiResponse.success(savedPermission, "Permission saved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while saving the permission.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Void> delete(Long id) {
        try {
            Permission permission = permissionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + id));
            permissionRepository.delete(permission);
            return ApiResponse.success(null, "Permission deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Permission not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while deleting the permission.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Permission> findByName(String name) {
        try {
            Permission permission = permissionRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with name: " + name));
            return ApiResponse.success(permission, "Permission retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Permission not found with the provided name.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the permission.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Boolean> existsByName(String name) {
        try {
            boolean exists = permissionRepository.existsByName(name);
            return ApiResponse.success(exists, "Existence check completed successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while checking permission existence.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
}
