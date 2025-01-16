package consulting.gazman.security.client.user.service;

import consulting.gazman.security.client.user.entity.Policy;

import java.util.List;
import java.util.Optional;

public interface PolicyService {

    List<Policy> getAllPolicies(); // Retrieve all policies

    Policy findById(Long id); // Find a policy by its ID

    Policy save(Policy policy); // Create or update a policy

    void delete(Long id); // Delete a policy by ID

    Optional<Policy> findByName(String name); // Find a policy by its name

    List<Policy> searchByName(String partialName); // Search for policies by partial name

    Optional<Policy> findByNameOptional(String name);
}
