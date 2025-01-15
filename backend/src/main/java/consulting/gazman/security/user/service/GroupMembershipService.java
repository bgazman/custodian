package consulting.gazman.security.user.service;

import consulting.gazman.security.user.entity.GroupMembership;

import java.util.List;
import java.util.Set;

public interface GroupMembershipService {

    // Add a user to a group with a specific role


    void addMembership(Long userId, Long groupId, Long roleId);

    // Update a user's role in a group

    void updateRole(Long userId, Long groupId, Long  newRoleId);

    // Remove a user from a group
    void removeMembership(Long userId, Long groupId);

    // Get all groups a user belongs to
    List<GroupMembership> getGroupsForUser(Long userId);

    // Get all users in a group
    List<GroupMembership> getUsersInGroup(Long groupId);

    void assignUserToGroups(Long id, Set<Long> groupIds);

    void deleteByUserId(Long userId);

    void saveAll(List<GroupMembership> newMemberships);

    void save(GroupMembership membership);
}
