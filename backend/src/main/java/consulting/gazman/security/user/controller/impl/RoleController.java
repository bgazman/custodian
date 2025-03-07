package consulting.gazman.security.user.controller.impl;

import consulting.gazman.security.user.controller.IRoleController;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.user.entity.Role;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController extends ApiController implements IRoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @Override
    public ResponseEntity<?> getAllRoles() {
        logRequest("GET", "/api/secure/roles");
        try {
            List<Role> roles = roleService.getAllRoles();
            return wrapSuccessResponse(roles, "Roles retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        logRequest("GET", "/api/secure/roles/" + id);
        try {
            Role role = roleService.findById(id);
            return wrapSuccessResponse(role, "Role retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        logRequest("POST", "/api/secure/roles");
        try {
            Role createdRole = roleService.save(role);
            return wrapSuccessResponse(createdRole, "Role created successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role role) {
        logRequest("PUT", "/api/secure/roles/" + id);
        try {
            role.setId(id); // Ensure the ID is set for update
            Role updatedRole = roleService.save(role);
            return wrapSuccessResponse(updatedRole, "Role updated successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        logRequest("DELETE", "/api/secure/roles/" + id);
        try {
            roleService.delete(id);
            return wrapSuccessResponse(null, "Role deleted successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> searchRoles(@RequestParam String name) {
        logRequest("GET", "/api/secure/roles/search?name=" + name);
        try {
            List<Role> roles = roleService.searchByName(name);
            return wrapSuccessResponse(roles, "Roles retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse(
                    "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
