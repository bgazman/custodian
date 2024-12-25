package consulting.gazman.security.service;


import consulting.gazman.security.entity.Permission;

import java.util.List;

public interface PermissionService {

    // Retrieve all permissions
    List<Permission> getAllPermissions();

    // Find a permission by ID
    Permission findById(Long id);

    // Create or update a permission
    Permission save(Permission permission);

    // Delete a permission by ID
    void delete(Long id);

    // Find a permission by name
    Permission findByName(String name);

    // Check if a permission exists by name
    boolean existsByName(String name);
}
