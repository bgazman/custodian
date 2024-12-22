package consulting.gazman.security.service;

import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Permission;

import java.util.List;

public interface PermissionService {

    // Retrieve all permissions
    ApiResponse<List<Permission>> getAllPermissions();

    // Find a permission by ID
    ApiResponse<Permission> findById(Long id);

    // Create or update a permission
    ApiResponse<Permission> save(Permission permission);

    // Delete a permission by ID
    ApiResponse<Void> delete(Long id);

    // Find a permission by name
    ApiResponse<Permission> findByName(String name);

    // Check if a permission exists by name
    ApiResponse<Boolean> existsByName(String name);
}
