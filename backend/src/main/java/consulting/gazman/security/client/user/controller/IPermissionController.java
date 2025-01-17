package consulting.gazman.security.client.user.controller;

import consulting.gazman.security.client.user.entity.Permission;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/permissions")
@PreAuthorize("hasRole('ADMIN')")
public interface IPermissionController {
    @GetMapping
    ResponseEntity<?> getAllPermissions();

    @GetMapping("/{id}")
    ResponseEntity<?> getPermissionById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<?> createPermission(@RequestBody Permission permission);

    @PutMapping("/{id}")
    ResponseEntity<?> updatePermission(@PathVariable Long id, @RequestBody Permission permission);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePermission(@PathVariable Long id);

    @GetMapping("/exists")
    ResponseEntity<?> checkPermissionExists(@RequestParam String name);

    @GetMapping("/search")
    ResponseEntity<?> findPermissionByName(@RequestParam String name);
}
