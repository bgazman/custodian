package consulting.gazman.security.client.user.controller.impl;
import consulting.gazman.security.client.user.controller.IGroupMembershipController;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.client.user.entity.GroupMembership;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.service.GroupMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-memberships")
@PreAuthorize("hasRole('ADMIN')")
public class GroupMembershipController extends ApiController implements IGroupMembershipController {

    @Autowired
    private GroupMembershipService groupMembershipService;

    @PostMapping
    @Override
    public ResponseEntity<?> addMembership(@RequestParam Long userId, @RequestParam Long groupId, @RequestParam Long role) {
        logRequest("POST", "/api/group-memberships?userId=" + userId + "&groupId=" + groupId + "&role=" + role);
        try {
            groupMembershipService.addMembership(userId, groupId, role);
            return wrapSuccessResponse(null, "Membership added successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    @Override
    public ResponseEntity<?> updateRole(@RequestParam Long userId, @RequestParam Long groupId, @RequestParam Long newRole) {
        logRequest("PUT", "/api/group-memberships?userId=" + userId + "&groupId=" + groupId + "&newRole=" + newRole);
        try {
            groupMembershipService.updateRole(userId, groupId, newRole);
            return wrapSuccessResponse(null, "Membership role updated successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> removeMembership(@RequestParam Long userId, @RequestParam Long groupId) {
        logRequest("DELETE", "/api/group-memberships?userId=" + userId + "&groupId=" + groupId);
        try {
            groupMembershipService.removeMembership(userId, groupId);
            return wrapSuccessResponse(null, "Membership removed successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    @Override
    public ResponseEntity<?> getGroupsForUser(@PathVariable Long userId) {
        logRequest("GET", "/api/group-memberships/user/" + userId);
        try {
            List<GroupMembership> memberships = groupMembershipService.getGroupsForUser(userId);
            return wrapSuccessResponse(memberships, "Groups retrieved successfully for the user");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/group/{groupId}")
    @Override
    public ResponseEntity<?> getUsersInGroup(@PathVariable Long groupId) {
        logRequest("GET", "/api/group-memberships/group/" + groupId);
        try {
            List<GroupMembership> memberships = groupMembershipService.getUsersInGroup(groupId);
            return wrapSuccessResponse(memberships, "Users retrieved successfully for the group");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}