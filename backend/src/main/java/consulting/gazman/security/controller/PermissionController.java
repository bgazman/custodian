package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Permission;
import consulting.gazman.security.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController extends ApiController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {
        logRequest("GET", "/api/permissions");

        ApiResponse<List<Permission>> serviceResponse = permissionService.getAllPermissions();
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        logRequest("GET", "/api/permissions/" + id);

        ApiResponse<Permission> serviceResponse = permissionService.findById(id);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping
    public ResponseEntity<?> createPermission(@RequestBody Permission permission) {
        logRequest("POST", "/api/permissions");

        ApiResponse<Permission> serviceResponse = permissionService.save(permission);
        return handleApiResponse(serviceResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        logRequest("PUT", "/api/permissions/" + id);

        permission.setId(id); // Ensure the ID is set for the update
        ApiResponse<Permission> serviceResponse = permissionService.save(permission);
        return handleApiResponse(serviceResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        logRequest("DELETE", "/api/permissions/" + id);

        ApiResponse<Void> serviceResponse = permissionService.delete(id);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/exists")
    public ResponseEntity<?> checkPermissionExists(@RequestParam String name) {
        logRequest("GET", "/api/permissions/exists?name=" + name);

        ApiResponse<Boolean> serviceResponse = permissionService.existsByName(name);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findPermissionByName(@RequestParam String name) {
        logRequest("GET", "/api/permissions/search?name=" + name);

        ApiResponse<Permission> serviceResponse = permissionService.findByName(name);
        return handleApiResponse(serviceResponse);
    }
}
