package consulting.gazman.security.repository;

import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.GroupMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, GroupMembershipId> {

    // Find all memberships for a specific user
    List<GroupMembership> findByUserId(Long userId);

    // Find all members of a specific group
    List<GroupMembership> findByGroupId(Long groupId);

    // Find a specific membership by user ID and group ID
    Optional<GroupMembership> findById(GroupMembershipId id);

    // Delete all memberships for a specific group
    void deleteByGroupId(Long groupId);

    // Delete all memberships for a specific user
    void deleteByUserId(Long userId);
}
