package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.GroupPermission;
import consulting.gazman.security.service.GroupPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-permissions")
@PreAuthorize("hasRole('ADMIN')")
public class GroupPermissionController extends ApiController {

    @Autowired
    private GroupPermissionService groupPermissionService;

    @PostMapping
    public ResponseEntity<?> addPermissionToGroup(@RequestParam Long groupId, @RequestParam Long permissionId) {
        logRequest("POST", "/api/group-permissions?groupId=" + groupId + "&permissionId=" + permissionId);

        ApiResponse<Void> serviceResponse = groupPermissionService.addPermissionToGroup(groupId, permissionId);
        return handleApiResponse(serviceResponse);
    }

    @DeleteMapping
    public ResponseEntity<?> removePermissionFromGroup(@RequestParam Long groupId, @RequestParam Long permissionId) {
        logRequest("DELETE", "/api/group-permissions?groupId=" + groupId + "&permissionId=" + permissionId);

        ApiResponse<Void> serviceResponse = groupPermissionService.removePermissionFromGroup(groupId, permissionId);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getPermissionsForGroup(@PathVariable Long groupId) {
        logRequest("GET", "/api/group-permissions/group/" + groupId);

        ApiResponse<List<GroupPermission>> serviceResponse = groupPermissionService.getPermissionsForGroup(groupId);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/permission/{permissionId}")
    public ResponseEntity<?> getGroupsForPermission(@PathVariable Long permissionId) {
        logRequest("GET", "/api/group-permissions/permission/" + permissionId);

        ApiResponse<List<GroupPermission>> serviceResponse = groupPermissionService.getGroupsForPermission(permissionId);
        return handleApiResponse(serviceResponse);
    }
}
