package consulting.gazman.security.client.user.controller;

import consulting.gazman.security.client.user.entity.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/secure/roles")
public interface IRoleController {
    @GetMapping
    ResponseEntity<?> getAllRoles();

    @GetMapping("/{id}")
    ResponseEntity<?> getRoleById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<?> createRole(@RequestBody Role role);

    @PutMapping("/{id}")
    ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role role);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteRole(@PathVariable Long id);

    @GetMapping("/search")
    ResponseEntity<?> searchRoles(@RequestParam String name);
}
