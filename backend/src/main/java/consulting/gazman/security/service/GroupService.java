package consulting.gazman.security.service;

import consulting.gazman.security.entity.Group;

import java.util.List;

public interface GroupService {


    List<Group> getAllGroups(); // Retrieve all groups
    Group findById(Long id); // Find group by ID
    Group save(Group group); // Create or update a group
    void delete(Long id); // Delete a group by ID
    Group findByName(String name); // Find group by name
    List<Group> searchByName(String partialName); // Search groups by partial name
}

