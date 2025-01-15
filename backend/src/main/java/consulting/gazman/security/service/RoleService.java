package consulting.gazman.security.service;

import consulting.gazman.security.entity.Role;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {

    List<Role> getAllRoles(); // Retrieve all roles

    Role findById(Long id);

    Role save(Role role); // Create or update a role

    void delete(Long id); // Delete a role by ID

    Optional<Role> findByName(String name); // Find role by name

    List<Role> searchByName(String partialName); // Search roles by partial name

    Set<UserRole> resolveRolesForUser(User savedUser, Set<Long> roleIds);

    Set<Role> findAllById(Set<Long> roleIds);
}
