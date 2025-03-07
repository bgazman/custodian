package consulting.gazman.security.user.repository;

import consulting.gazman.security.user.entity.UserRole;
import consulting.gazman.security.user.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    // Fetch all roles for a given user
    List<UserRole> findByUserId(Long userId);

    // Fetch all users for a given role
    List<UserRole> findByRoleId(Long roleId);

    // Check if a specific user has a specific role
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    // Delete a specific user-role association
    void deleteByUserIdAndRoleId(Long userId, Long roleId);

    void deleteByUserIdAndRoleIdIn(Long id, Set<Long> roleIds);

    void deleteByUserId(Long userId);}
