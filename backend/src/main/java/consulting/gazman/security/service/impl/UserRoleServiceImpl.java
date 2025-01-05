package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.User;
import consulting.gazman.security.entity.UserRole;
import consulting.gazman.security.entity.UserRoleId;
import consulting.gazman.security.repository.UserRoleRepository;
import consulting.gazman.security.service.RoleService;
import consulting.gazman.security.service.UserRoleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleService roleService;

    @Override
    public void updateUserRoles(User user, Set<Long> newRoleIds) {
        user.getUserRoles().clear();

        newRoleIds.forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(roleService.findById(roleId));
            user.getUserRoles().add(userRole);
        });
    }
    @Override
    public Optional<UserRole> findById(UserRoleId id) {
        return userRoleRepository.findById(id);
    }

    @Override
    public void addUserRoles(User user, Set<Long> roleIds) {
        Set<UserRole> userRoles = roleIds.stream()
                .map(roleId -> {
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(roleService.findById(roleId));
                    return userRole;
                })
                .collect(Collectors.toSet());

        userRoleRepository.saveAll(userRoles);
    }

    @Override
    public void removeUserRoles(User user, Set<Long> roleIds) {
        userRoleRepository.deleteByUserIdAndRoleIdIn(user.getId(), roleIds);
    }

    @Override
    public void deleteByUserId(Long id) {
        userRoleRepository.deleteByUserId(id);
    }

    @Override
    public void flush() {
        userRoleRepository.flush();
    }
}