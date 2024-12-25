package consulting.gazman.security.service;

import consulting.gazman.security.entity.GroupMembership;

import java.util.List;

public interface GroupMembershipService {

    // Add a user to a group with a specific role
    void addMembership(Long userId, Long groupId, String role);

    // Update a user's role in a group
    void updateRole(Long userId, Long groupId, String newRole);

    // Remove a user from a group
    void removeMembership(Long userId, Long groupId);

    // Get all groups a user belongs to
    List<GroupMembership> getGroupsForUser(Long userId);

    // Get all users in a group
    List<GroupMembership> getUsersInGroup(Long groupId);
}
