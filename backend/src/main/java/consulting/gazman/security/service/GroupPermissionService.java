package consulting.gazman.security.service;

import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.GroupPermission;

import java.util.List;

public interface GroupPermissionService {

    // Assign a permission to a group
    ApiResponse<Void> addPermissionToGroup(Long groupId, Long permissionId);

    // Remove a permission from a group
    ApiResponse<Void> removePermissionFromGroup(Long groupId, Long permissionId);

    // List all permissions for a specific group
    ApiResponse<List<GroupPermission>> getPermissionsForGroup(Long groupId);

    // List all groups associated with a specific permission
    ApiResponse<List<GroupPermission>> getGroupsForPermission(Long permissionId);
}
