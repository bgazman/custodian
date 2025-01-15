package consulting.gazman.security.user.repository;

import consulting.gazman.security.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Object> findByEmailAndIdNot(String email, Long id);
}

