package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.*;
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
    @Autowired
    RoleServiceImpl roleService;
    @Override
    public void addMembership(Long userId, Long groupId, Long roleId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + userId));

        // Verify group exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> AppException.resourceNotFound("Group not found with ID: " + groupId));
        // Verify role exists
        Role role = roleService.findById(roleId);

        // Create and save the membership
        GroupMembership membership = new GroupMembership();
        membership.setId(new GroupMembershipId());
        membership.setUser(user);
        membership.setGroup(group);
        membership.setRole(role);

        groupMembershipRepository.save(membership);
    }

    @Override
    public void updateRole(Long userId, Long groupId, Long newRoleId) {
        GroupMembership membership = groupMembershipRepository.findById(new GroupMembershipId())
                .orElseThrow(() -> AppException.resourceNotFound(
                        "Membership not found for User ID: " + userId + " and Group ID: " + groupId));
        Role role = roleService.findById(newRoleId);

        membership.setRole(role);
        groupMembershipRepository.save(membership);
    }

    @Override
    public void removeMembership(Long userId, Long groupId) {
        GroupMembershipId id = new GroupMembershipId();
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
