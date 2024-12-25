package consulting.gazman.security.service;

import consulting.gazman.security.entity.GroupPermission;

import java.util.List;

public interface GroupPermissionService {

    // Assign a permission to a group
    void addPermissionToGroup(Long groupId, Long permissionId);

    List<String> getGroupPermissions(Long groupId);

    // Remove a permission from a group
    void removePermissionFromGroup(Long groupId, Long permissionId);

    // List all permissions for a specific group
    List<GroupPermission> getPermissionsForGroup(Long groupId);

    // List all groups associated with a specific permission
    List<GroupPermission> getGroupsForPermission(Long permissionId);
}
