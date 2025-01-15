package consulting.gazman.security.user.repository;

import consulting.gazman.security.user.entity.UserAttribute;
import consulting.gazman.security.user.entity.UserAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, UserAttributeId> {
    List<UserAttribute> findByIdUserId(Long userId);
}
