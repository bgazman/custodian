package consulting.gazman.security.client.user.repository;


import consulting.gazman.security.client.user.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByName(String name);

    List<Policy> findByNameContaining(String partialName);
}
