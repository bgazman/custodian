package consulting.gazman.security.user.service;

import consulting.gazman.security.user.entity.PolicyAssignment;

import java.util.List;
import java.util.Optional;

public interface PolicyAssignmentService {

    List<PolicyAssignment> getAllAssignments(); // Retrieve all policy assignments

    PolicyAssignment findById(Long id); // Find a policy assignment by its ID

    PolicyAssignment save(PolicyAssignment policyAssignment); // Create or update a policy assignment

    void delete(Long id); // Delete a policy assignment by ID

    Optional<PolicyAssignment> findByPolicyAndUser(Long policyId, Long userId); // Find by policy and user

    Optional<PolicyAssignment> findByPolicyAndRole(Long policyId, Long roleId); // Find by policy and role

    Optional<PolicyAssignment> findByPolicyAndGroup(Long policyId, Long groupId); // Find by policy and group

    List<PolicyAssignment> findByPolicy(Long policyId); // Find all assignments for a given policy

    Optional<PolicyAssignment> findByPolicyIdAndRoleId(Long id, Long id1);
}
