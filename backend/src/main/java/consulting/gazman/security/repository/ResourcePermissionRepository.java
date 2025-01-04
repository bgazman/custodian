package consulting.gazman.security.repository;


import consulting.gazman.security.entity.ResourcePermission;
import consulting.gazman.security.entity.ResourcePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, ResourcePermissionId> {
    List<ResourcePermission> findByIdResourceId(Long resourceId);
}
