package consulting.gazman.security.config;

import consulting.gazman.security.dto.ClientRegistrationRequest;
import consulting.gazman.security.entity.*;
import consulting.gazman.security.repository.*;
import consulting.gazman.security.service.impl.ClientRegistrationServiceImpl;
import consulting.gazman.security.service.impl.OAuthClientServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class InitializationClass implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TenantUserRepository tenantUserRepository;
    private final ClientRegistrationServiceImpl clientRegistrationService;
    private final PasswordEncoder passwordEncoder;
    private final RolePermissionRepository rolePermissionRepository;

    public InitializationClass(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            TenantUserRepository tenantUserRepository,
            ClientRegistrationServiceImpl clientRegistrationService, ClientRegistrationServiceImpl clientRegistrationService1,
            PasswordEncoder passwordEncoder, RolePermissionRepository rolePermissionRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.tenantUserRepository = tenantUserRepository;
        this.clientRegistrationService = clientRegistrationService1;

        this.passwordEncoder = passwordEncoder;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Tenant defaultTenant = tenantRepository.findByName("IAM System")
                .orElseGet(() -> {
                    Tenant tenant = new Tenant();
                    tenant.setName("IAM System");
                    tenant.setDescription("Default tenant for the IAM system.");
                    return tenantRepository.save(tenant);
                });

        Map<String, String> defaultRoles = Map.of(
                "SUPER_ADMIN", "Has all permissions",
                "ADMIN", "Can manage resources",
                "VIEWER", "Can view resources"
        );
        defaultRoles.forEach((roleName, description) -> {
            roleRepository.findByName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription(description);
                return roleRepository.save(role);
            });
        });

        Map<String, String> defaultPermissions = Map.of(
                "MANAGE_TENANTS", "Manage all tenants",
                "MANAGE_USERS", "Manage users across tenants",
                "VIEW_REPORTS", "View reports and analytics"
        );
        defaultPermissions.forEach((permissionName, description) -> {
            permissionRepository.findByName(permissionName).orElseGet(() -> {
                Permission permission = new Permission();
                permission.setName(permissionName);
                permission.setDescription(description);
                return permissionRepository.save(permission);
            });
        });

        User adminUser = userRepository.findByEmail("admin@iam.local")
                .orElseGet(() -> {
                    User user = new User();
                    user.setName("Admin User");
                    user.setEmail("admin@iam.local");
                    String adminPassword = System.getenv("ADMIN_PASSWORD");
                    if (adminPassword == null || adminPassword.isBlank()) {
                        adminPassword = "admin123"; // Use with caution
                    }
                    user.setPassword(passwordEncoder.encode(adminPassword));
                    user.setEnabled(true);
                    return userRepository.save(user);
                });

        tenantUserRepository.findByIdTenantIdAndIdUserId(defaultTenant.getId(), adminUser.getId())
                .orElseGet(() -> {
                    TenantUser tenantUser = new TenantUser();
                    tenantUser.setId(new TenantUserId(defaultTenant.getId(), adminUser.getId()));
                    tenantUser.setTenant(defaultTenant);
                    tenantUser.setUser(adminUser);
                    tenantUser.setRole("SUPER_ADMIN");
                    return tenantUserRepository.save(tenantUser);
                });

        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found"));

        List<String> permissionsForSuperAdmin = List.of("MANAGE_TENANTS", "MANAGE_USERS", "VIEW_REPORTS");
        permissionsForSuperAdmin.forEach(permissionName -> {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName));

            RolePermissionId rolePermissionId = new RolePermissionId(superAdminRole.getId(), permission.getId());

            rolePermissionRepository.findById(rolePermissionId)
                    .orElseGet(() -> {
                        RolePermission rp = new RolePermission();
                        rp.setId(rolePermissionId); // Set the composite key explicitly
                        rp.setRole(superAdminRole);
                        rp.setPermission(permission);
                        return rolePermissionRepository.save(rp);
                    });
        });

//        ClientRegistrationRequest clientRegistrationRequest = ClientRegistrationRequest.builder()
//                .name("iam-dashboard")
//                .applicationType("web")
//                .scopes(List.of("openid", "profile", "email"))
//                .responseTypes(List.of("authorization_code", "refresh_token"))
//                .redirectUris(List.of("https://iam.local/callback"))
//                .grantTypes(List.of("authorization_code", "refresh_token"))
//                .tenantId(defaultTenant.getId())
//                .build();
//        try {
//            clientRegistrationService.registerClient(clientRegistrationRequest);
//        }catch(Exception e){
//            System.out.println("Client already initialized");
//
//        }
        System.out.println("IAM System default entities initialized successfully!");
    }
}
