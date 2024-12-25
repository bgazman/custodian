package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.Role;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.RoleRepository;
import consulting.gazman.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() ->  AppException.resourceNotFound("Role with ID " + id + " not found"));
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
}
