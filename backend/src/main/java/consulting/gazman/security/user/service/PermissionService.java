package consulting.gazman.security.user.service;


import consulting.gazman.security.user.entity.Permission;

import java.util.List;
import java.util.Optional;

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

    Optional<Permission> findByNameOptional(String name);
}
