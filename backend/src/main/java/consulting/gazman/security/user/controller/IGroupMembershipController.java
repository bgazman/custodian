package consulting.gazman.security.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IGroupMembershipController {
    @PostMapping
    ResponseEntity<?> addMembership(@RequestParam Long userId, @RequestParam Long groupId, @RequestParam Long role);

    @PutMapping
    ResponseEntity<?> updateRole(@RequestParam Long userId, @RequestParam Long groupId, @RequestParam Long newRole);

    @DeleteMapping
    ResponseEntity<?> removeMembership(@RequestParam Long userId, @RequestParam Long groupId);

    @GetMapping("/user/{userId}")
    ResponseEntity<?> getGroupsForUser(@PathVariable Long userId);

    @GetMapping("/group/{groupId}")
    ResponseEntity<?> getUsersInGroup(@PathVariable Long groupId);
}
