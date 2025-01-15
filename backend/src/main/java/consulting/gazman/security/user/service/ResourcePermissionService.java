package consulting.gazman.security.user.service;

import consulting.gazman.security.user.entity.ResourcePermission;
import consulting.gazman.security.user.entity.ResourcePermissionId;

import java.util.List;
import java.util.Optional;

public interface ResourcePermissionService {

    List<ResourcePermission> getAllResourcePermissions(); // Retrieve all resource permissions

    ResourcePermission findById(ResourcePermissionId id); // Find a resource permission by its composite ID

    ResourcePermission save(ResourcePermission resourcePermission); // Create or update a resource permission

    void delete(ResourcePermissionId id); // Delete a resource permission by its composite ID

    List<ResourcePermission> findByResourceId(Long resourceId); // Find permissions for a specific resource

    List<ResourcePermission> findByPermissionId(Long permissionId); // Find all resource-permission mappings by permission

    Optional<ResourcePermission> findByResourceAndPermission(Long resourceId, Long permissionId); // Find a specific mapping

    Optional<ResourcePermission> findByIdOptional(ResourcePermissionId resourcePermissionId);
}
