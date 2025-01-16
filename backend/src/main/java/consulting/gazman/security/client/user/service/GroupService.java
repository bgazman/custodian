package consulting.gazman.security.client.user.service;

import consulting.gazman.security.client.user.entity.Group;

import java.util.List;
import java.util.Set;

public interface GroupService {


    List<Group> getAllGroups(); // Retrieve all groups
    Group findById(Long id); // Find group by ID
    Group save(Group group); // Create or update a group
    void delete(Long id); // Delete a group by ID
    Group findByName(String name); // Find group by name
    List<Group> searchByName(String partialName); // Search groups by partial name

    Set<Group> findAllById(Set<Long> groupIds);

    Group createIfNotExists(String value, String s);
}

