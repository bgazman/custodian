package consulting.gazman.security.client.user.repository;


import consulting.gazman.security.client.user.entity.ResourcePermission;
import consulting.gazman.security.client.user.entity.ResourcePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, ResourcePermissionId> {
    List<ResourcePermission> findByIdResourceId(Long resourceId);

    List<ResourcePermission> findByResourceId(Long resourceId);

    List<ResourcePermission> findByPermissionId(Long permissionId);

    Optional<ResourcePermission> findByResourceIdAndPermissionId(Long resourceId, Long permissionId);
}
