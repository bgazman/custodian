package consulting.gazman.security.client.user.repository;


import consulting.gazman.security.client.user.entity.PolicyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyAssignmentRepository extends JpaRepository<PolicyAssignment, Long> {
    List<PolicyAssignment> findByUserId(Long userId);
    List<PolicyAssignment> findByRoleId(Long roleId);
    List<PolicyAssignment> findByGroupId(Long groupId);

    Optional<PolicyAssignment> findByPolicyIdAndRoleId(Long id, Long id1);

    Optional<PolicyAssignment> findByPolicyIdAndUserId(Long policyId, Long userId);

    Optional<PolicyAssignment> findByPolicyIdAndGroupId(Long policyId, Long groupId);

    List<PolicyAssignment> findByPolicyId(Long policyId);
}
