package consulting.gazman.security.user.service.impl;

import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.repository.GroupMembershipRepository;
import consulting.gazman.security.user.repository.GroupRepository;
import consulting.gazman.security.user.repository.UserRepository;
import consulting.gazman.security.user.service.GroupMembershipService;
import consulting.gazman.security.user.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

        groupMembershipRepository.save(membership);
    }

    @Override
    public void updateRole(Long userId, Long groupId, Long newRoleId) {
        GroupMembership membership = groupMembershipRepository.findById(new GroupMembershipId())
                .orElseThrow(() -> AppException.resourceNotFound(
                        "Membership not found for User ID: " + userId + " and Group ID: " + groupId));
        Role role = roleService.findById(newRoleId);

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

    @Override
    @Transactional
    public void assignUserToGroups(Long userId, Set<Long> groupIds) {
        // Fetch the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Fetch the groups from the database
        List<Group> groups = groupRepository.findAllById(groupIds);

        // Check if memberships already exist and create new ones if necessary
        List<GroupMembership> groupMemberships = groups.stream()
                .filter(group -> !groupMembershipRepository.existsByUserIdAndGroupId(userId, group.getId())) // Avoid duplicates
                .map(group -> {
                    GroupMembership membership = new GroupMembership();
                    membership.setUser(user); // Set the user
                    membership.setGroup(group); // Set the group
                    return membership;
                })
                .toList();

        // Save all new memberships
        groupMembershipRepository.saveAll(groupMemberships);
    }


    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // Fetch the user to ensure they exist, throw exception if not found
        userRepository.findById(userId)
                .orElseThrow(() -> AppException.userNotFound("User with ID " + userId + " not found"));

        // Use the repository to delete all group memberships for the user
        groupMembershipRepository.deleteByUserId(userId);
    }

    /**
     * Save a list of new group memberships to the database.
     *
     * @param newMemberships A list of new group memberships to save.
     */
    @Transactional
    @Override
    public void saveAll(List<GroupMembership> newMemberships) {
        // Use the repository to save all group memberships
        groupMembershipRepository.saveAll(newMemberships);
    }

    @Override
    public void save(GroupMembership membership) {
        groupMembershipRepository.save(membership);
    }

    @Override
    public boolean existsByUserIdAndGroupId(Long id, Long id1) {
        return groupMembershipRepository.existsByUserIdAndGroupId(id,id1);
    }
}
