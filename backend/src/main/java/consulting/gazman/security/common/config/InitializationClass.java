package consulting.gazman.security.common.config;

import consulting.gazman.security.oauth.dto.ClientRegistrationRequest;
import consulting.gazman.security.oauth.service.ClientRegistrationService;
import consulting.gazman.security.oauth.service.OAuthClientService;
import consulting.gazman.security.user.entity.*;
import consulting.gazman.security.user.repository.*;
import consulting.gazman.security.user.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Slf4j
public class InitializationClass implements CommandLineRunner {

    private final Environment environment;
    private final OAuthClientService oAuthClientService;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final ClientRegistrationService clientRegistrationService;
    private final PasswordEncoder passwordEncoder;

    private final RolePermissionService rolePermissionService;
    private final UserRoleService userRoleService;
    private final PolicyService policyService;
    private final PolicyAssignmentService policyAssignmentService;
    private final ResourceService resourceService;
    private final ResourcePermissionService resourcePermissionService;
    private final GroupService groupService;
    private final GroupMembershipService groupMembershipService;
    private final GroupPermissionService groupPermissionService;
    // Constructor Injection for all required dependencies
    public InitializationClass(
            Environment environment,
            OAuthClientService oAuthClientService,
            UserService userService,
            RoleService roleService,
            PermissionService permissionService,
            ClientRegistrationService clientRegistrationService,
            PasswordEncoder passwordEncoder,
            RolePermissionService rolePermissionService,
            UserRoleService userRoleService,
            PolicyService policyService,
            PolicyAssignmentService policyAssignmentService,
            ResourceService resourceService,
            ResourcePermissionService resourcePermissionService, RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository, PolicyRepository policyRepository, PolicyAssignmentRepository policyAssignmentRepository, ResourceRepository resourceRepository, ResourcePermissionRepository resourcePermissionRepository, ResourcePermissionService resourcePermissionService1,
            GroupService groupService,
            GroupMembershipService groupMembershipService, GroupPermissionService groupPermissionService) {
        this.environment = environment;
        this.oAuthClientService = oAuthClientService;
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;

        this.clientRegistrationService = clientRegistrationService;
        this.passwordEncoder = passwordEncoder;
        this.rolePermissionService = rolePermissionService;
        this.userRoleService = userRoleService;
        this.policyService = policyService;
        this.policyAssignmentService = policyAssignmentService;
        this.resourceService = resourceService;
        this.resourcePermissionService = resourcePermissionService1;

        this.groupService = groupService;
        this.groupMembershipService = groupMembershipService;
        this.groupPermissionService = groupPermissionService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Starting system initialization...");
            initializeSystem();
            log.info("System initialization complete");
        } catch (Exception e) {
            log.error("System initialization failed", e);
            throw new RuntimeException("Initialization failed", e);
        }
    }

    private void initializeSystem() {
        // Create base roles and permissions
        Role superAdminRole = createRoles();
        List<Permission> permissions = createPermissions();
        assignPermissionsToRoles(superAdminRole, permissions);
        createPolicies();
        Map<String, Group> groups = createGroups(); // Now it's a Map<String, Group>
        assignPermissionsToGroups(groups, permissions);
        User rootUser = createRootUser(superAdminRole);
        assignRoleToUser(rootUser,superAdminRole);
        assignUserToGroup(rootUser, groups.get("ROOT")); // Fetch the ROOT group by name
        createDefaultOAuthClient();
    }


    private Role createRoles() {
        Map<String, String> systemRoles = Map.of(
                "SUPER_ADMIN", "Complete system access",
                "USER_MANAGER", "Manage users and roles",
                "AUDIT_VIEWER", "View audit logs"
        );

        Role superAdminRole = null;
        for (Map.Entry<String, String> entry : systemRoles.entrySet()) {
            Role role = roleService.findByName(entry.getKey())
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(entry.getKey());
                        newRole.setDescription(entry.getValue());
                        return roleService.save(newRole);
                    });

            if ("SUPER_ADMIN".equals(entry.getKey())) {
                superAdminRole = role;
            }
        }
        return superAdminRole;
    }

    private Map<String, Group> createGroups() {
        Map<String, String> systemGroups = Map.of(
                "ROOT", "Default group for root users",
                "SYSTEM_ADMINS", "Group for system administrators",
                "APPLICATION_ADMINS", "Group for application administrators",
                "USERS", "Group for regular users"
        );

        Map<String, Group> groupMap = new HashMap<>();
        systemGroups.forEach((groupName, description) -> {
            Group group = groupService.createIfNotExists(groupName, description);
            groupMap.put(groupName, group);
        });

        return groupMap;
    }







    private List<Permission> createPermissions() {
        Map<String, String> systemPermissions = Map.of(
                "USER_READ", "Read user information",
                "USER_WRITE", "Modify user information",
                "AUDIT_READ", "View audit logs"
        );

        List<Permission> permissions = new ArrayList<>();
        systemPermissions.forEach((name, description) -> {
            Permission permission = permissionService.findByNameOptional(name)
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setName(name);
                        newPermission.setDescription(description);
                        return permissionService.save(newPermission);
                    });
            permissions.add(permission);
        });
        return permissions;
    }

    private List<Policy> createPolicies() {
        Map<String, String> systemPolicies = Map.of(
                "USER_MANAGEMENT", "Policy for managing users",
                "AUDIT_LOG_ACCESS", "Policy for accessing audit logs",
                "DATA_EXPORT", "Policy for exporting system data"
        );

        List<Policy> policies = new ArrayList<>();
        systemPolicies.forEach((name, description) -> {
            Policy policy = policyService.findByNameOptional(name)
                    .orElseGet(() -> {
                        Policy newPolicy = new Policy();
                        newPolicy.setName(name);
                        newPolicy.setDescription(description);
                        newPolicy.setDefinition(createDefaultPolicyDefinition(name)); // Example method for default JSON definition
                        newPolicy.setEffect("allow"); // Default effect
                        return policyService.save(newPolicy);
                    });
            policies.add(policy);
        });
        return policies;
    }
    private String createDefaultPolicyDefinition(String policyName) {
        // Generate default JSON definitions based on the policy name
        switch (policyName) {
            case "USER_MANAGEMENT":
                return """
                {
                    "actions": ["create_user", "delete_user", "update_user"],
                    "resources": ["users/*"]
                }
            """;
            case "AUDIT_LOG_ACCESS":
                return """
                {
                    "actions": ["view_logs"],
                    "resources": ["audit_logs/*"]
                }
            """;
            case "DATA_EXPORT":
                return """
                {
                    "actions": ["export_data"],
                    "resources": ["data/*"]
                }
            """;
            default:
                return """
                {
                    "actions": [],
                    "resources": []
                }
            """;
        }
    }


    private void assignUserToGroup(User user, Group group) {
        if (group == null || group.getId() == null) {
            throw new RuntimeException("Group is null or has not been persisted: " + group);
        }

        // Check if the membership already exists
        boolean membershipExists = groupMembershipService.existsByUserIdAndGroupId(user.getId(), group.getId());
        if (membershipExists) {
            System.out.println("User is already assigned to the group: " + group.getName());
            return; // Exit early if the user is already assigned to the group
        }

        // Create and save the membership
        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(group);
        groupMembershipService.save(membership);
    }





    private void assignRoleToUser(User user, Role role) {
        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());

        // Check if the user-role relationship already exists
        boolean roleExists = userRoleService.existsById(userRoleId);
        if (roleExists) {
            System.out.println("User already has the role: " + role.getName());
            return; // Exit early if the user already has the role
        }

        // Create and save the user-role relationship
        UserRole userRole = new UserRole();
        userRole.setId(userRoleId);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleService.save(userRole);
    }

    private void assignPermissionsToRoles(Role superAdminRole, List<Permission> permissions) {
        permissions.forEach(permission -> {
            RolePermissionId rolePermissionId = new RolePermissionId(superAdminRole.getId(), permission.getId());
            if (!rolePermissionService.existsById(rolePermissionId)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(rolePermissionId);
                rolePermission.setRole(superAdminRole);
                rolePermission.setPermission(permission);
                rolePermissionService.save(rolePermission);
            }
        });
    }

    private void assignPermissionsToGroups(Map<String, Group> groups, List<Permission> permissions) {
        Group adminGroup = groups.get("SYSTEM_ADMINS");
        permissions.forEach(permission -> {
            GroupPermissionId groupPermissionId = new GroupPermissionId(adminGroup.getId(), permission.getId());
            if (!groupPermissionService.existsById(groupPermissionId)) {
                GroupPermission groupPermission = new GroupPermission();
                groupPermission.setId(groupPermissionId);
                groupPermission.setGroup(adminGroup);
                groupPermission.setPermission(permission);
                groupPermissionService.save(groupPermission);
            }
        });
    }

    private void createResourcesAndPermissions() {
        Resource resource = resourceService.findByName("SystemLogs")
                .orElseGet(() -> {
                    Resource newResource = new Resource();
                    newResource.setName("SystemLogs");
                    newResource.setType("log");
                    newResource.setDescription("System-wide audit logs");
                    return resourceService.save(newResource);
                });

        Permission auditReadPermission = permissionService.findByNameOptional("AUDIT_READ").orElseThrow();
        ResourcePermissionId resourcePermissionId = new ResourcePermissionId();
        resourcePermissionId.setPermissionId(auditReadPermission.getId());
        resourcePermissionId.setResourceId(resource.getId());
        resourcePermissionService.findByIdOptional(resourcePermissionId).orElseGet(() -> {
            ResourcePermission resourcePermission = new ResourcePermission();
            resourcePermission.setId(resourcePermissionId);
            resourcePermission.setResource(resource);
            resourcePermission.setPermission(auditReadPermission);
            return resourcePermissionService.save(resourcePermission);
        });
    }

//    private void createRootUserAndAssignRole(Role superAdminRole) {
//        // Fetch properties from the environment with defaults
//        String rootUserName = environment.getProperty("app.root-user.name", "Root Admin");
//        String rootUserEmail = environment.getProperty("app.root-user.email", "root@system.local");
//        String rootUserPassword = environment.getProperty("app.root-user.password", "rootpass123!");
//
//        User rootUser = userService.findByEmailOptional("root@system.local")
//                .orElseGet(() -> {
//                    User user = new User();
//                    user.setName(rootUserName);
//                    user.setEmail(rootUserEmail);
//                    user.setPassword(passwordEncoder.encode(rootUserPassword));
//                    user.setEnabled(true);
//                    user.setEmailVerified(true);
//                    return userService.save(user);
//                });
//
//        UserRoleId userRoleId = new UserRoleId();
//        userRoleId.setRoleId(superAdminRole.getId());
//        userRoleId.setUserId(rootUser.getId());
//        userRoleService.findById(userRoleId).orElseGet(() -> {
//            UserRole userRole = new UserRole();
//            userRole.setId(userRoleId);
//            userRole.setUser(rootUser);
//            userRole.setRole(superAdminRole);
//            return userRoleService.save(userRole);
//        });
//    }
private User createRootUser(Role superAdminRole) {
    // Fetch properties from the environment with defaults
    String rootUserName = environment.getProperty("app.root-user.name", "Root Admin");
    String rootUserEmail = environment.getProperty("app.root-user.email", "root@system.local");
    String rootUserPassword = environment.getProperty("app.root-user.password", "rootpass123!");

    User rootUser = userService.findByEmailOptional("root@system.local")
            .orElseGet(() -> {
                User user = new User();
                user.setName(rootUserName);
                user.setEmail(rootUserEmail);
                user.setPassword(passwordEncoder.encode(rootUserPassword));
                user.setEnabled(true);
                user.setEmailVerified(true);
                return userService.save(user);
            });

    return rootUser;
}
    private void createDefaultOAuthClient() {
        if (!oAuthClientService.existsByName("root-dashboard")) {
            String clientId = environment.getProperty("app.client-id", "");
            String redirectUris = environment.getProperty("app.redirect-uris", "");

            log.info("Initializing root client with clientId: " + clientId);
            ClientRegistrationRequest clientRequest = ClientRegistrationRequest.builder()
                    .name("root-dashboard")
                    .applicationType("web")
                    .scopes(List.of("openid", "profile", "email", "admin"))
                    .responseTypes(List.of("authorization_code", "refresh_token"))
                    .redirectUris(List.of(redirectUris))
                    .grantTypes(List.of("authorization_code", "refresh_token"))
                    .clientId(clientId)
                    .build();
            clientRegistrationService.registerClient(clientRequest);
        }
    }
}
