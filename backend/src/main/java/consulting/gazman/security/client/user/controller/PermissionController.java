package consulting.gazman.security.client.user.controller;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.client.user.entity.Permission;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        try {
            List<Permission> permissions = permissionService.getAllPermissions();
            return wrapSuccessResponse(permissions, "Permissions retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        logRequest("GET", "/api/permissions/" + id);
        try {
            Permission permission = permissionService.findById(id);
            return wrapSuccessResponse(permission, "Permission retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createPermission(@RequestBody Permission permission) {
        logRequest("POST", "/api/permissions");
        try {
            Permission createdPermission = permissionService.save(permission);
            return wrapSuccessResponse(createdPermission, "Permission created successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        logRequest("PUT", "/api/permissions/" + id);
        try {
            permission.setId(id); // Ensure the ID is set for the update
            Permission updatedPermission = permissionService.save(permission);
            return wrapSuccessResponse(updatedPermission, "Permission updated successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        logRequest("DELETE", "/api/permissions/" + id);
        try {
            permissionService.delete(id);
            return wrapSuccessResponse(null, "Permission deleted successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<?> checkPermissionExists(@RequestParam String name) {
        logRequest("GET", "/api/permissions/exists?name=" + name);
        try {
            boolean exists = permissionService.existsByName(name);
            return wrapSuccessResponse(exists, "Permission existence checked successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> findPermissionByName(@RequestParam String name) {
        logRequest("GET", "/api/permissions/search?name=" + name);
        try {
            Permission permission = permissionService.findByName(name);
            return wrapSuccessResponse(permission, "Permission found successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
