package consulting.gazman.security.repository;




import consulting.gazman.security.entity.Permission;
import consulting.gazman.security.entity.Role;
import consulting.gazman.security.entity.RolePermission;
import consulting.gazman.security.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    boolean existsById(RolePermissionId id);

    List<RolePermission> findByIdRoleId(Long roleId);

    List<RolePermission> findByIdPermissionId(Long permissionId);

    boolean existsByRoleIdAndPermissionId(Long id, Long id1);

    Optional<RolePermission> findByRoleAndPermission(Role superAdminRole, Permission permission);
}
