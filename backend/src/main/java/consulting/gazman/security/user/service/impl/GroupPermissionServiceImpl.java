package consulting.gazman.security.user.service.impl;


import consulting.gazman.security.user.entity.Group;
import consulting.gazman.security.user.entity.GroupPermission;
import consulting.gazman.security.user.entity.GroupPermissionId;
import consulting.gazman.security.user.entity.Permission;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.repository.GroupPermissionRepository;
import consulting.gazman.security.user.repository.GroupRepository;
import consulting.gazman.security.user.repository.PermissionRepository;
import consulting.gazman.security.user.service.GroupPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class GroupPermissionServiceImpl implements GroupPermissionService {

    @Autowired
    private GroupPermissionRepository groupPermissionRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void addPermissionToGroup(Long groupId, Long permissionId) {
        // Validate group and permission existence
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> AppException.resourceNotFound("Group not found with ID: " + groupId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> AppException.resourceNotFound("Permission not found with ID: " + permissionId));

        // Create and save the association
        GroupPermission groupPermission = new GroupPermission();
        groupPermission.setId(new GroupPermissionId());
        groupPermission.setGroup(group);
        groupPermission.setPermission(permission);

        groupPermissionRepository.save(groupPermission);
    }

    @Override
    public List<String> getGroupPermissions(Long groupId) {
        return groupPermissionRepository.findByGroupId(groupId)
                .stream()
                .map(gp -> gp.getPermission().getName())
                .collect(Collectors.toList());
    }

    @Override
    public void removePermissionFromGroup(Long groupId, Long permissionId) {
        GroupPermissionId id = new GroupPermissionId();
        GroupPermission groupPermission = groupPermissionRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound(
                        "Permission not associated with group (Group ID: " + groupId + ", Permission ID: " + permissionId + ")"));

        groupPermissionRepository.delete(groupPermission);
    }

    @Override
    public List<GroupPermission> getPermissionsForGroup(Long groupId) {
        return groupPermissionRepository.findByGroupId(groupId);
    }

    @Override
    public List<GroupPermission> getGroupsForPermission(Long permissionId) {
        return groupPermissionRepository.findByPermissionId(permissionId);
    }
    @Override
    public List<GroupPermission> findPermissionsByGroupIds(List<Long> groupIds) {
        return groupPermissionRepository.findByGroupIdIn(groupIds);
    }

    @Override
    public boolean existsById(GroupPermissionId groupPermissionId) {
        return groupPermissionRepository.existsById(groupPermissionId);
    }

    @Override
    public void save(GroupPermission groupPermission) {
            groupPermissionRepository.save(groupPermission);
    }

}
