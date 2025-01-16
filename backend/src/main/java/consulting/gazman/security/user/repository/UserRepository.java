package consulting.gazman.security.user.repository;

import consulting.gazman.security.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Object> findByEmailAndIdNot(String email, Long id);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userRoles ur " +
            "LEFT JOIN FETCH ur.role " +
            "LEFT JOIN FETCH u.groupMemberships gm " +
            "LEFT JOIN FETCH gm.group " +
            "WHERE u.id = :id")
    Optional<User> findUserWithRolesAndGroups(@Param("id") Long id);

}

