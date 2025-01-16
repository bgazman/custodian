package consulting.gazman.security.client.user.service.impl;

import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.entity.PolicyAssignment;
import consulting.gazman.security.client.user.repository.PolicyAssignmentRepository;
import consulting.gazman.security.client.user.service.PolicyAssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyAssignmentServiceImpl implements PolicyAssignmentService {

    private final PolicyAssignmentRepository policyAssignmentRepository;

    public PolicyAssignmentServiceImpl(PolicyAssignmentRepository policyAssignmentRepository) {
        this.policyAssignmentRepository = policyAssignmentRepository;
    }

    @Override
    public List<PolicyAssignment> getAllAssignments() {
        return policyAssignmentRepository.findAll();
    }

    @Override
    public PolicyAssignment findById(Long id) {
        return policyAssignmentRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Policy Assignment not found with ID: " + id));
    }

    @Override
    public PolicyAssignment save(PolicyAssignment policyAssignment) {
        return policyAssignmentRepository.save(policyAssignment);
    }

    @Override
    public void delete(Long id) {
        PolicyAssignment policyAssignment = policyAssignmentRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Policy Assignment not found with ID: " + id));
        policyAssignmentRepository.delete(policyAssignment);
    }

    @Override
    public Optional<PolicyAssignment> findByPolicyAndUser(Long policyId, Long userId) {
        return policyAssignmentRepository.findByPolicyIdAndUserId(policyId, userId);
    }

    @Override
    public Optional<PolicyAssignment> findByPolicyAndRole(Long policyId, Long roleId) {
        return policyAssignmentRepository.findByPolicyIdAndRoleId(policyId, roleId);
    }

    @Override
    public Optional<PolicyAssignment> findByPolicyAndGroup(Long policyId, Long groupId) {
        return policyAssignmentRepository.findByPolicyIdAndGroupId(policyId, groupId);
    }

    @Override
    public List<PolicyAssignment> findByPolicy(Long policyId) {
        return policyAssignmentRepository.findByPolicyId(policyId);
    }

    @Override
    public Optional<PolicyAssignment> findByPolicyIdAndRoleId(Long id, Long id1) {
        return policyAssignmentRepository.findByPolicyIdAndRoleId(id,id1);
    }
}
