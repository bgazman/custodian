package consulting.gazman.security.client.user.service.impl;

import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.entity.RolePermission;
import consulting.gazman.security.client.user.entity.RolePermissionId;
import consulting.gazman.security.client.user.repository.RolePermissionRepository;

import consulting.gazman.security.client.user.service.RolePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public List<RolePermission> getAllRolePermissions() {
        return rolePermissionRepository.findAll();
    }

    @Override
    public RolePermission findById(RolePermissionId id) {
        return rolePermissionRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Role Permission not found with ID: " + id));
    }

    @Override
    public RolePermission save(RolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }

    @Override
    public void delete(RolePermissionId id) {
        RolePermission rolePermission = rolePermissionRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Role Permission not found with ID: " + id));
        rolePermissionRepository.delete(rolePermission);
    }

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }

    @Override
    public List<RolePermission> findByPermissionId(Long permissionId) {
        return rolePermissionRepository.findByPermissionId(permissionId);
    }

    @Override
    public Optional<RolePermission> findByRoleAndPermission(Long roleId, Long permissionId) {
        return rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId);
    }

    @Override
    public Optional<RolePermission> findByIdOptional(RolePermissionId rolePermissionId) {
        return rolePermissionRepository.findById(rolePermissionId);
    }

    @Override
    public List<RolePermission> findPermissionsByRoleIds(List<Long> roleIds) {
        return rolePermissionRepository.findByRoleIdIn(roleIds);
    }

    @Override
    public boolean existsById(RolePermissionId rolePermissionId) {
        return rolePermissionRepository.existsById(rolePermissionId);
    }


}
