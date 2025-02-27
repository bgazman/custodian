package consulting.gazman.security.user.service.impl;

import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.entity.Resource;
import consulting.gazman.security.user.repository.ResourceRepository;
import consulting.gazman.security.user.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public Resource findById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Resource not found with ID: " + id));
    }

    @Override
    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public void delete(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Resource not found with ID: " + id));
        resourceRepository.delete(resource);
    }

    @Override
    public Optional<Resource> findByName(String name) {
        return resourceRepository.findByName(name);
    }

    @Override
    public List<Resource> searchByName(String partialName) {
        return resourceRepository.findByNameContaining(partialName);
    }

    @Override
    public List<Resource> findByType(String type) {
        return resourceRepository.findByType(type);
    }
}
