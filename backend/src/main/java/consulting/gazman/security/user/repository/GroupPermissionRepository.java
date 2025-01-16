package consulting.gazman.security.user.repository;

import consulting.gazman.security.user.entity.GroupPermission;
import consulting.gazman.security.user.entity.GroupPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupPermissionRepository extends JpaRepository<GroupPermission, GroupPermissionId> {

    // Find all permissions assigned to a specific group
    List<GroupPermission> findByGroupId(Long groupId);

    // Find all groups associated with a specific permission
    List<GroupPermission> findByPermissionId(Long permissionId);

    // Check if a specific group-permission relationship exists
    Optional<GroupPermission> findById(GroupPermissionId id);

    // Delete all permissions for a specific group
    void deleteByGroupId(Long groupId);

    // Delete all group associations for a specific permission
    void deleteByPermissionId(Long permissionId);

    List<GroupPermission> findByGroupIdIn(List<Long> groupIds);
}
