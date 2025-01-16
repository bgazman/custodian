package consulting.gazman.security.client.user.repository;

import consulting.gazman.security.client.user.entity.UserAttribute;
import consulting.gazman.security.client.user.entity.UserAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, UserAttributeId> {
    List<UserAttribute> findByIdUserId(Long userId);
}
