package consulting.gazman.security.client.user.service;

import consulting.gazman.security.client.user.entity.RolePermission;
import consulting.gazman.security.client.user.entity.RolePermissionId;

import java.util.List;
import java.util.Optional;

public interface RolePermissionService {

    List<RolePermission> getAllRolePermissions(); // Retrieve all role permissions

    RolePermission findById(RolePermissionId id); // Find a role permission by its composite ID

    RolePermission save(RolePermission rolePermission); // Create or update a role permission

    void delete(RolePermissionId id); // Delete a role permission by its composite ID

    List<RolePermission> findByRoleId(Long roleId); // Find all permissions for a specific role

    List<RolePermission> findByPermissionId(Long permissionId); // Find all roles associated with a specific permission

    Optional<RolePermission> findByRoleAndPermission(Long roleId, Long permissionId); // Find a specific mapping

    Optional<RolePermission> findByIdOptional(RolePermissionId rolePermissionId);

    List<RolePermission> findPermissionsByRoleIds(List<Long> roleIds);

    boolean existsById(RolePermissionId rolePermissionId);
}
