package consulting.gazman.security.service;

import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.GroupMembership;

import java.util.List;

public interface GroupMembershipService {

    // Add a user to a group with a specific role
    ApiResponse<Void> addMembership(Long userId, Long groupId, String role);

    // Update a user's role in a group
    ApiResponse<Void> updateRole(Long userId, Long groupId, String newRole);

    // Remove a user from a group
    ApiResponse<Void> removeMembership(Long userId, Long groupId);

    // Get all groups a user belongs to
    ApiResponse<List<GroupMembership>> getGroupsForUser(Long userId);

    // Get all users in a group
    ApiResponse<List<GroupMembership>> getUsersInGroup(Long groupId);
}
