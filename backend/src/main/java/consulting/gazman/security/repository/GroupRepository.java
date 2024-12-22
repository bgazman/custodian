package consulting.gazman.security.repository;

import consulting.gazman.security.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Custom query to find a group by name
    Optional<Group> findByName(String name);

    // Custom query to check if a group exists by name
    boolean existsByName(String name);

    List<Group> findByNameContaining(String partialName);

}
