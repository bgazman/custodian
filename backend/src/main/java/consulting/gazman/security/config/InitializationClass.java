package consulting.gazman.security.config;

import consulting.gazman.security.dto.ClientRegistrationRequest;
import consulting.gazman.security.entity.*;
import consulting.gazman.security.repository.*;
import consulting.gazman.security.service.ClientRegistrationService;
import consulting.gazman.security.service.OAuthClientService;
import consulting.gazman.security.service.impl.ClientRegistrationServiceImpl;
import consulting.gazman.security.service.impl.OAuthClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Slf4j
public class InitializationClass implements CommandLineRunner {
    private final OAuthClientService oAuthClientService;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TenantUserRepository tenantUserRepository;
    private final ClientRegistrationService clientRegistrationService;
    private final PasswordEncoder passwordEncoder;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    // Constructor with all dependencies
    public InitializationClass(
            OAuthClientService oAuthClientService,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            TenantUserRepository tenantUserRepository,
            ClientRegistrationService clientRegistrationService,
            PasswordEncoder passwordEncoder,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository) {
        this.oAuthClientService = oAuthClientService;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.tenantUserRepository = tenantUserRepository;
        this.clientRegistrationService = clientRegistrationService;
        this.passwordEncoder = passwordEncoder;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            initializeRootTenant();
        } catch (Exception e) {
            log.error("Failed to initialize root tenant", e);
            throw new RuntimeException("System initialization failed", e);
        }
    }

    private void initializeRootTenant() {
        // 1. Create Root Tenant
        Tenant rootTenant = tenantRepository.findByName("root")
                .orElseGet(() -> {
                    Tenant tenant = new Tenant();
                    tenant.setName("root");
                    tenant.setDescription("Root System Tenant - Has full system access");
                    return tenantRepository.save(tenant);
                });

        // 2. Initialize System Roles
        Map<String, String> systemRoles = Map.of(
                "SUPER_ADMIN", "Complete system access with all permissions",
                "SYSTEM_ADMIN", "System-wide administrative access with limited restrictions",
                "TENANT_ADMIN", "Full access within assigned tenant",
                "USER_MANAGER", "User management capabilities",
                "AUDIT_VIEWER", "Access to system audit logs",
                "CLIENT_MANAGER", "OAuth client management capabilities"
        );

        Map<Role, Role> roleHierarchy = new HashMap<>();
        Role superAdminRole = null;

        for (Map.Entry<String, String> roleEntry : systemRoles.entrySet()) {
            Role role = roleRepository.findByName(roleEntry.getKey())
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(roleEntry.getKey());
                        newRole.setDescription(roleEntry.getValue());
                        return roleRepository.save(newRole);
                    });

            if ("SUPER_ADMIN".equals(roleEntry.getKey())) {
                superAdminRole = role;
            } else if ("SYSTEM_ADMIN".equals(roleEntry.getKey())) {
                roleHierarchy.put(role, superAdminRole);
            }
        }

        // 3. Initialize System Permissions
        Map<String, String> systemPermissions = new LinkedHashMap<>();

        // Tenant Management
        systemPermissions.put("TENANT_CREATE", "Create new tenants");
        systemPermissions.put("TENANT_READ", "View tenant information");
        systemPermissions.put("TENANT_UPDATE", "Update tenant details");
        systemPermissions.put("TENANT_DELETE", "Delete tenants");

        // User Management
        systemPermissions.put("USER_CREATE", "Create new users");
        systemPermissions.put("USER_READ", "View user information");
        systemPermissions.put("USER_UPDATE", "Update user details");
        systemPermissions.put("USER_DELETE", "Delete users");

        // Role Management
        systemPermissions.put("ROLE_CREATE", "Create new roles");
        systemPermissions.put("ROLE_READ", "View role information");
        systemPermissions.put("ROLE_UPDATE", "Update role details");
        systemPermissions.put("ROLE_DELETE", "Delete roles");

        // Permission Management
        systemPermissions.put("PERMISSION_CREATE", "Create new permissions");
        systemPermissions.put("PERMISSION_READ", "View permission information");
        systemPermissions.put("PERMISSION_UPDATE", "Update permission details");
        systemPermissions.put("PERMISSION_DELETE", "Delete permissions");

        // Group Management
        systemPermissions.put("GROUP_CREATE", "Create new groups");
        systemPermissions.put("GROUP_READ", "View group information");
        systemPermissions.put("GROUP_UPDATE", "Update group details");
        systemPermissions.put("GROUP_DELETE", "Delete groups");

        // OAuth Client Management
        systemPermissions.put("OAUTH_CLIENT_CREATE", "Create new OAuth clients");
        systemPermissions.put("OAUTH_CLIENT_READ", "View OAuth client information");
        systemPermissions.put("OAUTH_CLIENT_UPDATE", "Update OAuth client details");
        systemPermissions.put("OAUTH_CLIENT_DELETE", "Delete OAuth clients");

        // System Management
        systemPermissions.put("SYSTEM_AUDIT", "View system audit logs");
        systemPermissions.put("SYSTEM_CONFIG", "Modify system configuration");
        systemPermissions.put("SYSTEM_BACKUP", "Manage system backups");
        systemPermissions.put("SYSTEM_RESTORE", "Restore system from backup");

        List<Permission> allPermissions = new ArrayList<>();
        systemPermissions.forEach((name, description) -> {
            Permission permission = permissionRepository.findByName(name)
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setName(name);
                        newPermission.setDescription(description);
                        return permissionRepository.save(newPermission);
                    });
            allPermissions.add(permission);
        });

        // 4. Create Root User
        String rootPassword = System.getenv().getOrDefault("ROOT_PASSWORD", "rootpass123!");
        User rootUser = userRepository.findByEmail("root@system.local")
                .orElseGet(() -> {
                    User user = new User();
                    user.setName("Root Admin");
                    user.setEmail("root@system.local");
                    user.setPassword(passwordEncoder.encode(rootPassword));
                    user.setEnabled(true);
                    user.setEmailVerified(true);
//                    user.setMfaEnabled(true);
                    return userRepository.save(user);
                });

        // 5. Assign All Permissions to Super Admin Role
        Role finalSuperAdminRole = superAdminRole;
        allPermissions.forEach(permission -> {
            RolePermissionId rolePermissionId = new RolePermissionId(finalSuperAdminRole.getId(), permission.getId());
            rolePermissionRepository.findById(rolePermissionId)
                    .orElseGet(() -> {
                        RolePermission rp = new RolePermission();
                        rp.setId(rolePermissionId);
                        rp.setRole(finalSuperAdminRole);
                        rp.setPermission(permission);
                        return rolePermissionRepository.save(rp);
                    });
        });

        // 6. Assign Root User to Root Tenant
        TenantUserId tenantUserId = new TenantUserId(rootTenant.getId(), rootUser.getId());
        tenantUserRepository.findById(tenantUserId)
                .orElseGet(() -> {
                    TenantUser tenantUser = new TenantUser();
                    tenantUser.setId(tenantUserId);
                    tenantUser.setTenant(rootTenant);
                    tenantUser.setUser(rootUser);
                    tenantUser.setRole("SUPER_ADMIN");
                    return tenantUserRepository.save(tenantUser);
                });

        // 7. Assign Super Admin Role to Root User
        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setUserId(rootUser.getId());
        userRoleId.setRoleId(superAdminRole.getId());
        Role finalSuperAdminRole1 = superAdminRole;
        userRoleRepository.findById(userRoleId)
                .orElseGet(() -> {
                    UserRole userRole = new UserRole();
                    userRole.setId(userRoleId);
                    userRole.setUser(rootUser);
                    userRole.setRole(finalSuperAdminRole1);
                    return userRoleRepository.save(userRole);
                });

        // 8. Initialize Default OAuth Client
        if (!oAuthClientService.existsByName("root-dashboard")) {
            ClientRegistrationRequest clientRegistrationRequest = ClientRegistrationRequest.builder()
                    .name("root-dashboard")
                    .applicationType("web")
                    .scopes(List.of("openid", "profile", "email", "admin"))
                    .responseTypes(List.of("authorization_code", "refresh_token"))
                    .redirectUris(List.of("https://localhost:5173/callback"))
                    .grantTypes(List.of("authorization_code", "refresh_token"))
                    .tenantId(rootTenant.getId())
                    .build();
            try {
                clientRegistrationService.registerClient(clientRegistrationRequest);
            } catch (Exception e) {
                log.warn("Root dashboard client already exists", e);
            }
        }

        log.info("Root tenant initialization completed successfully!");
    }
}