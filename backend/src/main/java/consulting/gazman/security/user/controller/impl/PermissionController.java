package consulting.gazman.security.user.controller.impl;

import consulting.gazman.security.user.controller.IPermissionController;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.user.entity.Permission;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class PermissionController extends ApiController implements IPermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
