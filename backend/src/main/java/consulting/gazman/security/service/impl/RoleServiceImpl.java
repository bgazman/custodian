package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.Role;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.entity.UserRole;
import consulting.gazman.security.entity.UserRoleId;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.RoleRepository;
import consulting.gazman.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() ->  AppException.roleNotFound("Role with ID " + id + " not found"));
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw AppException.resourceNotFound("Role with ID " + id + " does not exist");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> searchByName(String partialName) {
        return roleRepository.findByNameContainingIgnoreCase(partialName);
    }
    @Override

    public Set<UserRole> resolveRolesForUser(User user, Set<Long> roleIds) {
        // Fetch roles from the database based on provided IDs
        Set<Role> roles = roleRepository.findAllById(roleIds).stream()
                .collect(Collectors.toSet());

        // If any of the provided role IDs don't exist, throw an exception
        if (roles.size() != roleIds.size()) {
            throw new AppException("INVALID_ROLE_IDS", "One or more roles do not exist");
        }

        // Create UserRole entities for the user and the roles
        return roles.stream()
                .map(role -> {
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRole.setId(new UserRoleId(user.getId(), role.getId())); // Composite Key
                    return userRole;
                })
                .collect(Collectors.toSet());
    }

    @Override

    public Set<Role> findAllById(Set<Long> roleIds) {
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }

}