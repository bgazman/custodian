package consulting.gazman.security.user.service.impl;


import consulting.gazman.security.user.entity.Permission;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.repository.PermissionRepository;
import consulting.gazman.security.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() ->  AppException.resourceNotFound("Permission not found with ID: " + id));
    }

    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public void delete(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Permission not found with ID: " + id));
        permissionRepository.delete(permission);
    }

    @Override
    public Permission findByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> AppException.resourceNotFound("Permission not found with name: " + name));
    }

    @Override
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
