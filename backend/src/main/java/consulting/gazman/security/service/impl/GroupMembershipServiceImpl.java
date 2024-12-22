package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.GroupMembershipId;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.GroupMembershipRepository;
import consulting.gazman.security.repository.GroupRepository;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.service.GroupMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMembershipServiceImpl implements GroupMembershipService {

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public ApiResponse<Void> addMembership(Long userId, Long groupId, String role) {
        try {
            // Verify user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Verify group exists
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));

            // Create and save the membership
            GroupMembership membership = new GroupMembership();
            membership.setId(new GroupMembershipId(userId, groupId));
            membership.setUser(user);
            membership.setGroup(group);
            membership.setRole(role);

            groupMembershipRepository.save(membership);

            return ApiResponse.success(null, "Membership added successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    ex.getMessage(),
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while adding the membership.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Void> updateRole(Long userId, Long groupId, String newRole) {
        try {
            GroupMembership membership = groupMembershipRepository.findById(new GroupMembershipId(userId, groupId))
                    .orElseThrow(() -> new ResourceNotFoundException("Membership not found for User ID: " + userId + " and Group ID: " + groupId));

            membership.setRole(newRole);
            groupMembershipRepository.save(membership);

            return ApiResponse.success(null, "Membership role updated successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    ex.getMessage(),
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while updating the role.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Void> removeMembership(Long userId, Long groupId) {
        try {
            GroupMembershipId id = new GroupMembershipId(userId, groupId);
            GroupMembership membership = groupMembershipRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Membership not found for User ID: " + userId + " and Group ID: " + groupId));

            groupMembershipRepository.delete(membership);

            return ApiResponse.success(null, "Membership removed successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    ex.getMessage(),
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while removing the membership.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<List<GroupMembership>> getGroupsForUser(Long userId) {
        try {
            List<GroupMembership> memberships = groupMembershipRepository.findByUserId(userId);
            return ApiResponse.success(memberships, "Groups retrieved successfully for the user.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving groups for the user.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<List<GroupMembership>> getUsersInGroup(Long groupId) {
        try {
            List<GroupMembership> memberships = groupMembershipRepository.findByGroupId(groupId);
            return ApiResponse.success(memberships, "Users retrieved successfully for the group.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving users for the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
}
