package consulting.gazman.security.user.repository;

import consulting.gazman.security.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Find a permission by its name
    Optional<Permission> findByName(String name);

    // Check if a permission exists by its name
    boolean existsByName(String name);
}
