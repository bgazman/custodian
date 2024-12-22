package consulting.gazman.security.controller;
import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.service.GroupMembershipService;
import consulting.gazman.security.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-memberships")
@PreAuthorize("hasRole('ADMIN')")
    public class GroupMembershipController extends ApiController {

    @Autowired
    private GroupMembershipService groupMembershipService;

    @PostMapping
    public ResponseEntity<?> addMembership(@RequestParam Long userId, @RequestParam Long groupId, @RequestParam String role) {
        logRequest("POST", "/api/group-memberships?userId=" + userId + "&groupId=" + groupId + "&role=" + role);

        ApiResponse<Void> serviceResponse = groupMembershipService.addMembership(userId, groupId, role);
        return handleApiResponse(serviceResponse);
    }

    @PutMapping
    public ResponseEntity<?> updateRole(@RequestParam Long userId, @RequestParam Long groupId, @RequestParam String newRole) {
        logRequest("PUT", "/api/group-memberships?userId=" + userId + "&groupId=" + groupId + "&newRole=" + newRole);

        ApiResponse<Void> serviceResponse = groupMembershipService.updateRole(userId, groupId, newRole);
        return handleApiResponse(serviceResponse);
    }

    @DeleteMapping
    public ResponseEntity<?> removeMembership(@RequestParam Long userId, @RequestParam Long groupId) {
        logRequest("DELETE", "/api/group-memberships?userId=" + userId + "&groupId=" + groupId);

        ApiResponse<Void> serviceResponse = groupMembershipService.removeMembership(userId, groupId);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getGroupsForUser(@PathVariable Long userId) {
        logRequest("GET", "/api/group-memberships/user/" + userId);

        ApiResponse<List<GroupMembership>> serviceResponse = groupMembershipService.getGroupsForUser(userId);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getUsersInGroup(@PathVariable Long groupId) {
        logRequest("GET", "/api/group-memberships/group/" + groupId);

        ApiResponse<List<GroupMembership>> serviceResponse = groupMembershipService.getUsersInGroup(groupId);
        return handleApiResponse(serviceResponse);
    }
}
