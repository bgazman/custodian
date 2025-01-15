package consulting.gazman.security.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IGroupPermissionController {
    @PostMapping
    ResponseEntity<?> addPermissionToGroup(@RequestParam Long groupId, @RequestParam Long permissionId);

    @DeleteMapping
    ResponseEntity<?> removePermissionFromGroup(@RequestParam Long groupId, @RequestParam Long permissionId);

    @GetMapping("/group/{groupId}")
    ResponseEntity<?> getPermissionsForGroup(@PathVariable Long groupId);

    @GetMapping("/permission/{permissionId}")
    ResponseEntity<?> getGroupsForPermission(@PathVariable Long permissionId);
}
