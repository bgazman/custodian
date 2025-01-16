package consulting.gazman.security.client.user.service.impl;

import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.entity.Policy;
import consulting.gazman.security.client.user.repository.PolicyRepository;
import consulting.gazman.security.client.user.service.PolicyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Override
    public Policy findById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Policy not found with ID: " + id));
    }

    @Override
    public Policy save(Policy policy) {
        return policyRepository.save(policy);
    }

    @Override
    public void delete(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Policy not found with ID: " + id));
        policyRepository.delete(policy);
    }

    @Override
    public Optional<Policy> findByName(String name) {
        return policyRepository.findByName(name);
    }

    @Override
    public List<Policy> searchByName(String partialName) {
        return policyRepository.findByNameContaining(partialName);
    }

    @Override
    public Optional<Policy> findByNameOptional(String name) {
        return policyRepository.findByName(name);
    }
}
