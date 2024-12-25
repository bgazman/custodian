package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.GroupMembershipId;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
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
    public void addMembership(Long userId, Long groupId, String role) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + userId));

        // Verify group exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> AppException.resourceNotFound("Group not found with ID: " + groupId));

        // Create and save the membership
        GroupMembership membership = new GroupMembership();
        membership.setId(new GroupMembershipId(userId, groupId));
        membership.setUser(user);
        membership.setGroup(group);
        membership.setRole(role);

        groupMembershipRepository.save(membership);
    }

    @Override
    public void updateRole(Long userId, Long groupId, String newRole) {
        GroupMembership membership = groupMembershipRepository.findById(new GroupMembershipId(userId, groupId))
                .orElseThrow(() -> AppException.resourceNotFound(
                        "Membership not found for User ID: " + userId + " and Group ID: " + groupId));

        membership.setRole(newRole);
        groupMembershipRepository.save(membership);
    }

    @Override
    public void removeMembership(Long userId, Long groupId) {
        GroupMembershipId id = new GroupMembershipId(userId, groupId);
        GroupMembership membership = groupMembershipRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound(
                        "Membership not found for User ID: " + userId + " and Group ID: " + groupId));

        groupMembershipRepository.delete(membership);
    }

    @Override
    public List<GroupMembership> getGroupsForUser(Long userId) {
        return groupMembershipRepository.findByUserId(userId);
    }

    @Override
    public List<GroupMembership> getUsersInGroup(Long groupId) {
        return groupMembershipRepository.findByGroupId(groupId);
    }
}
