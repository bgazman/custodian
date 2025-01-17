package consulting.gazman.security.client.user.controller;

import consulting.gazman.security.client.user.entity.Group;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/secure/groups")
public interface IGroupController {
    @GetMapping
    ResponseEntity<?> getAllGroups();

    @GetMapping("/{id}")
    ResponseEntity<?> getGroupById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<?> createGroup(@RequestBody Group group);

    @PutMapping("/{id}")
    ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody Group group);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteGroup(@PathVariable Long id);

    @GetMapping("/search")
    ResponseEntity<?> searchGroups(@RequestParam String name);
}
