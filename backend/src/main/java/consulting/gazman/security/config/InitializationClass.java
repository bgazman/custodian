package consulting.gazman.security.config;

import consulting.gazman.security.dto.ClientRegistrationRequest;
import consulting.gazman.security.entity.*;
import consulting.gazman.security.repository.*;
import consulting.gazman.security.service.ClientRegistrationService;
import consulting.gazman.security.service.OAuthClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class InitializationClass implements CommandLineRunner {

    private final OAuthClientService oAuthClientService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ClientRegistrationService clientRegistrationService;
    private final PasswordEncoder passwordEncoder;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final PolicyRepository policyRepository;
    private final PolicyAssignmentRepository policyAssignmentRepository;
    private final ResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;

    public InitializationClass(
            OAuthClientService oAuthClientService,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            ClientRegistrationService clientRegistrationService,
            PasswordEncoder passwordEncoder,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository,
            PolicyRepository policyRepository,
            PolicyAssignmentRepository policyAssignmentRepository,
            ResourceRepository resourceRepository,
            ResourcePermissionRepository resourcePermissionRepository) {
        this.oAuthClientService = oAuthClientService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.clientRegistrationService = clientRegistrationService;
        this.passwordEncoder = passwordEncoder;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.policyRepository = policyRepository;
        this.policyAssignmentRepository = policyAssignmentRepository;
        this.resourceRepository = resourceRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            initializeSystem();
        } catch (Exception e) {
            log.error("System initialization failed", e);
            throw new RuntimeException("Initialization failed", e);
        }
    }

    private void initializeSystem() {
        // Initialize Roles
        Role superAdminRole = createRoles();

        // Initialize Permissions
        List<Permission> permissions = createPermissions();

        // Initialize Policies
        createPolicies(superAdminRole, permissions);

        // Initialize Resources and Permissions
        createResourcesAndPermissions();

        // Create Root User and Assign Role
        createRootUserAndAssignRole(superAdminRole);

        // Initialize Default OAuth Client
        createDefaultOAuthClient();

        log.info("System initialization completed successfully!");
    }

    private Role createRoles() {
        Map<String, String> systemRoles = Map.of(
                "SUPER_ADMIN", "Complete system access",
                "USER_MANAGER", "Manage users and roles",
                "AUDIT_VIEWER", "View audit logs"
        );

        Role superAdminRole = null;
        for (Map.Entry<String, String> entry : systemRoles.entrySet()) {
            Role role = roleRepository.findByName(entry.getKey())
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(entry.getKey());
                        newRole.setDescription(entry.getValue());
                        return roleRepository.save(newRole);
                    });

            if ("SUPER_ADMIN".equals(entry.getKey())) {
                superAdminRole = role;
            }
        }
        return superAdminRole;
    }

    private List<Permission> createPermissions() {
        Map<String, String> systemPermissions = Map.of(
                "USER_READ", "Read user information",
                "USER_WRITE", "Modify user information",
                "AUDIT_READ", "View audit logs"
        );

        List<Permission> permissions = new ArrayList<>();
        systemPermissions.forEach((name, description) -> {
            Permission permission = permissionRepository.findByName(name)
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setName(name);
                        newPermission.setDescription(description);
                        return permissionRepository.save(newPermission);
                    });
            permissions.add(permission);
        });
        return permissions;
    }

    private void createPolicies(Role superAdminRole, List<Permission> permissions) {
        Policy policy = policyRepository.findByName("FullAccessPolicy")
                .orElseGet(() -> {
                    Policy newPolicy = new Policy();
                    newPolicy.setName("FullAccessPolicy");
                    newPolicy.setDescription("Grants all permissions");
                    newPolicy.setDefinition("{\"effect\": \"allow\"}");
                    newPolicy.setEffect("allow");
                    return policyRepository.save(newPolicy);
                });

        PolicyAssignment assignment = policyAssignmentRepository.findByPolicyIdAndRoleId(policy.getId(), superAdminRole.getId())
                .orElseGet(() -> {
                    PolicyAssignment newAssignment = new PolicyAssignment();
                    newAssignment.setPolicy(policy);
                    newAssignment.setRole(superAdminRole);
                    return policyAssignmentRepository.save(newAssignment);
                });

        permissions.forEach(permission -> {
            RolePermissionId rolePermissionId = new RolePermissionId(superAdminRole.getId(), permission.getId());
            rolePermissionRepository.findById(rolePermissionId).orElseGet(() -> {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(rolePermissionId);
                rolePermission.setRole(superAdminRole);
                rolePermission.setPermission(permission);
                return rolePermissionRepository.save(rolePermission);
            });
        });
    }

    private void createResourcesAndPermissions() {
        Resource resource = resourceRepository.findByName("SystemLogs")
                .orElseGet(() -> {
                    Resource newResource = new Resource();
                    newResource.setName("SystemLogs");
                    newResource.setType("log");
                    newResource.setDescription("System-wide audit logs");
                    return resourceRepository.save(newResource);
                });

        Permission auditReadPermission = permissionRepository.findByName("AUDIT_READ").orElseThrow();
        ResourcePermissionId resourcePermissionId = new ResourcePermissionId();
        resourcePermissionId.setPermissionId(auditReadPermission.getId());
        resourcePermissionId.setResourceId(resource.getId());
        resourcePermissionRepository.findById(resourcePermissionId).orElseGet(() -> {
            ResourcePermission resourcePermission = new ResourcePermission();
            resourcePermission.setId(resourcePermissionId);
            resourcePermission.setResource(resource);
            resourcePermission.setPermission(auditReadPermission);
            return resourcePermissionRepository.save(resourcePermission);
        });
    }

    private void createRootUserAndAssignRole(Role superAdminRole) {
        String rootPassword = System.getenv().getOrDefault("ROOT_PASSWORD", "rootpass123!");
        User rootUser = userRepository.findByEmail("root@system.local")
                .orElseGet(() -> {
                    User user = new User();
                    user.setName("Root Admin");
                    user.setEmail("root@system.local");
                    user.setPassword(passwordEncoder.encode(rootPassword));
                    user.setEnabled(true);
                    user.setEmailVerified(true);
                    return userRepository.save(user);
                });

        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setRoleId(superAdminRole.getId());
        userRoleId.setUserId(rootUser.getId());
        userRoleRepository.findById(userRoleId).orElseGet(() -> {
            UserRole userRole = new UserRole();
            userRole.setId(userRoleId);
            userRole.setUser(rootUser);
            userRole.setRole(superAdminRole);
            return userRoleRepository.save(userRole);
        });
    }

    private void createDefaultOAuthClient() {
        if (!oAuthClientService.existsByName("root-dashboard")) {
            ClientRegistrationRequest clientRequest = ClientRegistrationRequest.builder()
                    .name("root-dashboard")
                    .applicationType("web")
                    .scopes(List.of("openid", "profile", "email", "admin"))
                    .responseTypes(List.of("authorization_code", "refresh_token"))
                    .redirectUris(List.of("https://localhost:5173/callback"))
                    .grantTypes(List.of("authorization_code", "refresh_token"))
                    .build();
            clientRegistrationService.registerClient(clientRequest);
        }
    }
}
