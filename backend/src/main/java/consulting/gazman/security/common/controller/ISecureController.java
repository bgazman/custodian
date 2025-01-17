package consulting.gazman.security.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;




@RequestMapping("/api/secure")
public interface ISecureController {
    @Operation(
            summary = "Test Authenticated User",
            description = "This endpoint is accessible to any authenticated user, regardless of roles or authorities.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()") // Accessible to any authenticated user
    ResponseEntity<Map<String, String>> testAuthenticatedUser();

    @Operation(
            summary = "Test Roles",
            description = "This endpoint checks if the user has the role **SUPER_ADMIN**. Use `hasRole('SUPER_ADMIN')` for access control.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/role-test")
    @PreAuthorize("hasRole('SUPER_ADMIN')") // Accessible only to users with SUPER_ADMIN role
    ResponseEntity<Map<String, String>> testRoles();

    @Operation(
            summary = "Test Authorities",
            description = "This endpoint checks if the user has the authority **SCOPE_USER_READ**.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/authority-test")
    @PreAuthorize("hasAuthority('SCOPE_USER_READ')") // Accessible only to users with SCOPE_USER_READ authority
    ResponseEntity<Map<String, String>> testAuthorities();

    @Operation(
            summary = "Test Forbidden Access",
            description = """
            This endpoint requires the role **NOT_SUPER_ADMIN** or the authority **SCOPE_NOT_ADMIN_WRITE**. <br/>
            The current token does not have these permissions, so it will return a 403 Forbidden response.
        """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/forbidden-test")
    @PreAuthorize("hasRole('NOT_SUPER_ADMIN') or hasAuthority('SCOPE_NOT_ADMIN_WRITE')") // Requires missing role/authority
    ResponseEntity<Map<String, String>> testForbiddenAccess();


    @GetMapping("/debug-auth")
    String debugAuth(Authentication authentication);
}