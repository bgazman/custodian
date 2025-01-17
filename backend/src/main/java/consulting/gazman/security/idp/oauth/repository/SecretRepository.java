package consulting.gazman.security.idp.oauth.repository;

import consulting.gazman.security.idp.oauth.entity.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretRepository extends JpaRepository<Secret, Long> {

    Optional<Secret> findByName(String name); // Find a secret by its name
    Optional<Secret> findById(Long id);
    boolean existsByName(String name); // Check if a secret exists by its name

    List<Secret> findAllByActiveTrue();

    List<Secret> findByActiveTrue();
}
