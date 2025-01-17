package consulting.gazman.security.client.user.controller.impl;

import consulting.gazman.security.client.user.controller.IGroupPermissionController;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.client.user.entity.GroupPermission;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.service.GroupPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secure/group-permissions")
@PreAuthorize("hasRole('ADMIN')")
public class GroupPermissionController extends ApiController implements IGroupPermissionController {

    @Autowired
    private GroupPermissionService groupPermissionService;

    @PostMapping
    @Override
    public ResponseEntity<?> addPermissionToGroup(@RequestParam Long groupId, @RequestParam Long permissionId) {
        logRequest("POST", "/api/group-permissions?groupId=" + groupId + "&permissionId=" + permissionId);
        try {
            groupPermissionService.addPermissionToGroup(groupId, permissionId);
            return wrapSuccessResponse(null, "Permission added to group successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> removePermissionFromGroup(@RequestParam Long groupId, @RequestParam Long permissionId) {
        logRequest("DELETE", "/api/group-permissions?groupId=" + groupId + "&permissionId=" + permissionId);
        try {
            groupPermissionService.removePermissionFromGroup(groupId, permissionId);
            return wrapSuccessResponse(null, "Permission removed from group successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/group/{groupId}")
    @Override
    public ResponseEntity<?> getPermissionsForGroup(@PathVariable Long groupId) {
        logRequest("GET", "/api/group-permissions/group/" + groupId);
        try {
            List<GroupPermission> permissions = groupPermissionService.getPermissionsForGroup(groupId);
            return wrapSuccessResponse(permissions, "Permissions retrieved successfully for the group");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/permission/{permissionId}")
    @Override
    public ResponseEntity<?> getGroupsForPermission(@PathVariable Long permissionId) {
        logRequest("GET", "/api/group-permissions/permission/" + permissionId);
        try {
            List<GroupPermission> groups = groupPermissionService.getGroupsForPermission(permissionId);
            return wrapSuccessResponse(groups, "Groups retrieved successfully for the permission");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}