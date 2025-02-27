package consulting.gazman.security.user.service.impl;

import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.entity.ResourcePermission;
import consulting.gazman.security.user.entity.ResourcePermissionId;
import consulting.gazman.security.user.repository.ResourcePermissionRepository;
import consulting.gazman.security.user.service.ResourcePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourcePermissionServiceImpl implements ResourcePermissionService {

    private final ResourcePermissionRepository resourcePermissionRepository;

    public ResourcePermissionServiceImpl(ResourcePermissionRepository resourcePermissionRepository) {
        this.resourcePermissionRepository = resourcePermissionRepository;
    }

    @Override
    public List<ResourcePermission> getAllResourcePermissions() {
        return resourcePermissionRepository.findAll();
    }

    @Override
    public ResourcePermission findById(ResourcePermissionId id) {
        return resourcePermissionRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Resource Permission not found with ID: " + id));
    }

    @Override
    public ResourcePermission save(ResourcePermission resourcePermission) {
        return resourcePermissionRepository.save(resourcePermission);
    }

    @Override
    public void delete(ResourcePermissionId id) {
        ResourcePermission resourcePermission = resourcePermissionRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Resource Permission not found with ID: " + id));
        resourcePermissionRepository.delete(resourcePermission);
    }

    @Override
    public List<ResourcePermission> findByResourceId(Long resourceId) {
        return resourcePermissionRepository.findByResourceId(resourceId);
    }

    @Override
    public List<ResourcePermission> findByPermissionId(Long permissionId) {
        return resourcePermissionRepository.findByPermissionId(permissionId);
    }

    @Override
    public Optional<ResourcePermission> findByResourceAndPermission(Long resourceId, Long permissionId) {
        return resourcePermissionRepository.findByResourceIdAndPermissionId(resourceId, permissionId);
    }

    @Override
    public Optional<ResourcePermission> findByIdOptional(ResourcePermissionId resourcePermissionId) {
        return resourcePermissionRepository.findById(resourcePermissionId);
    }
}
