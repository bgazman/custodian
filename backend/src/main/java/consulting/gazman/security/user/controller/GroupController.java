package consulting.gazman.security.user.controller;

import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.user.entity.Group;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secure/groups")
public class GroupController extends ApiController {

    @Autowired
    private GroupService groupService;

    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        logRequest("GET", "/api/secure/groups");
        try {
            List<Group> groups = groupService.getAllGroups();
            return wrapSuccessResponse(groups, "Groups retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id) {
        logRequest("GET", "/api/secure/groups/" + id);
        try {
            Group group = groupService.findById(id);
            return wrapSuccessResponse(group, "Group retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Group group) {
        logRequest("POST", "/api/secure/groups");
        try {
            Group createdGroup = groupService.save(group);
            return wrapSuccessResponse(createdGroup, "Group created successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody Group group) {
        logRequest("PUT", "/api/secure/groups/" + id);
        try {
            group.setId(id); // Ensure the ID is set for update
            Group updatedGroup = groupService.save(group);
            return wrapSuccessResponse(updatedGroup, "Group updated successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        logRequest("DELETE", "/api/secure/groups/" + id);
        try {
            groupService.delete(id);
            return wrapSuccessResponse(null, "Group deleted successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchGroups(@RequestParam String name) {
        logRequest("GET", "/api/secure/groups/search?name=" + name);
        try {
            List<Group> groups = groupService.searchByName(name);
            return wrapSuccessResponse(groups, "Groups retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
