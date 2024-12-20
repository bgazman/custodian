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
//    void updateFailedLoginAttemptsById(Long userId, Integer attempts);
//    void updateLockedUntilById(Long userId, LocalDateTime lockedUntil);
//@Modifying
//@Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.id = :userId")
//void updateFailedLoginAttempts(@Param("userId") Long userId, @Param("attempts") Integer attempts);
//
//    @Modifying
//    @Query("UPDATE User u SET u.lockedUntil = :lockedUntil WHERE u.id = :userId")
//    void updateLockedUntil(@Param("userId") Long userId, @Param("lockedUntil") LocalDateTime lockedUntil);

}

