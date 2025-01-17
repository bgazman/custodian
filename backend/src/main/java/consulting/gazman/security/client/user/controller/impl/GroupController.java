package consulting.gazman.security.client.user.controller.impl;

import consulting.gazman.security.client.user.controller.IGroupController;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.client.user.entity.Group;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController extends ApiController implements IGroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping
    @Override
    public ResponseEntity<?> getAllGroups() {
        try {
            List<Group> groups = groupService.getAllGroups();
            return wrapSuccessResponse(groups, "Groups retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getGroupById(@PathVariable Long id) {
        try {
            Group group = groupService.findById(id);
            return wrapSuccessResponse(group, "Group retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> createGroup(@RequestBody Group group) {
        try {
            Group createdGroup = groupService.save(group);
            return wrapSuccessResponse(createdGroup, "Group created successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody Group group) {
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

    @Override
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        try {
            groupService.delete(id);
            return wrapSuccessResponse(null, "Group deleted successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> searchGroups(@RequestParam String name) {
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
