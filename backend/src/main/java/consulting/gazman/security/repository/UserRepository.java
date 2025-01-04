package consulting.gazman.security.repository;

import consulting.gazman.security.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Object> findByEmailAndIdNot(String email, Long id);
}

