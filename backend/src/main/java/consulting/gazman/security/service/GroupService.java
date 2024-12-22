package consulting.gazman.security.service;

import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;

import java.util.List;

public interface GroupService {

    ApiResponse<List<Group>> getAllGroups(); // Retrieve all groups
    ApiResponse<Group> findById(Long id); // Find group by ID
    ApiResponse<Group> save(Group group); // Create or update a group
    ApiResponse<Void> delete(Long id); // Delete a group by ID
    ApiResponse<Group> findByName(String name); // Find group by name
    ApiResponse<List<Group>> searchByName(String partialName); // Search groups by partial name
}

