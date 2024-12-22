package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;
import consulting.gazman.security.entity.GroupPermission;
import consulting.gazman.security.entity.GroupPermissionId;
import consulting.gazman.security.entity.Permission;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.GroupPermissionRepository;
import consulting.gazman.security.repository.GroupRepository;
import consulting.gazman.security.repository.PermissionRepository;
import consulting.gazman.security.service.GroupPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupPermissionServiceImpl implements GroupPermissionService {

    @Autowired
    private GroupPermissionRepository groupPermissionRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public ApiResponse<Void> addPermissionToGroup(Long groupId, Long permissionId) {
        try {
            // Validate group and permission existence
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + permissionId));

            // Create and save the association
            GroupPermission groupPermission = new GroupPermission();
            groupPermission.setId(new GroupPermissionId(groupId, permissionId));
            groupPermission.setGroup(group);
            groupPermission.setPermission(permission);

            groupPermissionRepository.save(groupPermission);

            return ApiResponse.success(null, "Permission assigned to group successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    ex.getMessage(),
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while assigning the permission to the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Void> removePermissionFromGroup(Long groupId, Long permissionId) {
        try {
            GroupPermissionId id = new GroupPermissionId(groupId, permissionId);
            GroupPermission groupPermission = groupPermissionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Permission not associated with group (Group ID: " + groupId + ", Permission ID: " + permissionId + ")"));

            groupPermissionRepository.delete(groupPermission);

            return ApiResponse.success(null, "Permission removed from group successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    ex.getMessage(),
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while removing the permission from the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<List<GroupPermission>> getPermissionsForGroup(Long groupId) {
        try {
            List<GroupPermission> permissions = groupPermissionRepository.findByGroupId(groupId);
            return ApiResponse.success(permissions, "Permissions retrieved successfully for the group.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving permissions for the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<List<GroupPermission>> getGroupsForPermission(Long permissionId) {
        try {
            List<GroupPermission> groups = groupPermissionRepository.findByPermissionId(permissionId);
            return ApiResponse.success(groups, "Groups retrieved successfully for the permission.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving groups for the permission.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
}
