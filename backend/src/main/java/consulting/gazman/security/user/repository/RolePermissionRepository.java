package consulting.gazman.security.user.repository;




import consulting.gazman.security.user.entity.Permission;
import consulting.gazman.security.user.entity.Role;
import consulting.gazman.security.user.entity.RolePermission;
import consulting.gazman.security.user.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    boolean existsById(RolePermissionId id);

    List<RolePermission> findByIdRoleId(Long roleId);

    List<RolePermission> findByIdPermissionId(Long permissionId);

    boolean existsByRoleIdAndPermissionId(Long id, Long id1);

    Optional<RolePermission> findByRoleAndPermission(Role superAdminRole, Permission permission);

    List<RolePermission> findByRoleId(Long roleId);

    List<RolePermission> findByPermissionId(Long permissionId);

    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    List<RolePermission> findByRoleIdIn(List<Long> roleIds);
}
