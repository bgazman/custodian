package consulting.gazman.security.repository;

import consulting.gazman.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findByNameContainingIgnoreCase(String partialName);

}
