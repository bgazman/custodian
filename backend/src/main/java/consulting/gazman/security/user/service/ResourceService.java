package consulting.gazman.security.user.service;

import consulting.gazman.security.user.entity.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    List<Resource> getAllResources(); // Retrieve all resources

    Resource findById(Long id); // Find a resource by its ID

    Resource save(Resource resource); // Create or update a resource

    void delete(Long id); // Delete a resource by ID

    Optional<Resource> findByName(String name); // Find a resource by its name

    List<Resource> searchByName(String partialName); // Search resources by partial name

    List<Resource> findByType(String type); // Find all resources by type (e.g., "log", "document")
}
