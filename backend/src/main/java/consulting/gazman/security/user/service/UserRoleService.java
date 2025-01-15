package consulting.gazman.security.user.service;

import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.user.entity.UserRole;
import consulting.gazman.security.user.entity.UserRoleId;

import java.util.Optional;
import java.util.Set;

public interface UserRoleService {
    void addUserRoles(User user, Set<Long> roleIds);
    void updateUserRoles(User user, Set<Long> newRoleIds);

    Optional<UserRole> findById(UserRoleId id);

    void removeUserRoles(User user, Set<Long> roleIds);

    void deleteByUserId(Long id);

    void flush();
}
