package consulting.gazman.security.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;



@RequestMapping("/api/secure")
public interface IProtectedController {

    @Operation(
            summary = "Get Protected Data",
            description = "This endpoint requires the authority **SCOPE_PROTECTED_READ**.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/data")
    ResponseEntity<Map<String, String>> getProtectedData();

    @Operation(
            summary = "Admin-Only Action",
            description = "This endpoint requires the role **ADMIN**. Use `hasRole('ADMIN')` for access control.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/admin-only")
    ResponseEntity<Map<String, String>> adminOnlyAction();

    @Operation(
            summary = "Audit-Read Access",
            description = "This endpoint requires the authority **SCOPE_AUDIT_READ**. Use `hasAuthority('SCOPE_AUDIT_READ')` for access control.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/audit-read")
    ResponseEntity<Map<String, String>> auditReadAccess();

    @Operation(
            summary = "Manager Access",
            description = "This endpoint requires the role **MANAGER**.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/manager")
    ResponseEntity<Map<String, String>> managerOnlyAccess();

    @Operation(
            summary = "Custom Permission Access",
            description = "This endpoint requires the custom authority **SCOPE_CUSTOM_PERMISSION**.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/custom-permission")
    ResponseEntity<Map<String, String>> customPermissionAccess();
}
