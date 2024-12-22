package consulting.gazman.security.controller;

import consulting.gazman.common.controller.ApiController;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;
import consulting.gazman.security.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasRole('ADMIN')")
public class GroupController extends ApiController {

    @Autowired
    private GroupService groupService;

    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        logRequest("GET", "/api/groups");

        // Call the service and handle the response
        ApiResponse<List<Group>> serviceResponse = groupService.getAllGroups();
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id) {
        logRequest("GET", "/api/groups/" + id);

        ApiResponse<Group> serviceResponse = groupService.findById(id);
        return handleApiResponse(serviceResponse);
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Group group) {
        logRequest("POST", "/api/groups");

        ApiResponse<Group> serviceResponse = groupService.save(group);
        return handleApiResponse(serviceResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody Group group) {
        logRequest("PUT", "/api/groups/" + id);

        group.setId(id); // Ensure the ID is set for update
        ApiResponse<Group> serviceResponse = groupService.save(group);
        return handleApiResponse(serviceResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        logRequest("DELETE", "/api/groups/" + id);

        ApiResponse<Void> serviceResponse = groupService.delete(id);
        return handleApiResponse(serviceResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchGroups(@RequestParam String name) {
        logRequest("GET", "/api/groups/search?name=" + name);

        ApiResponse<List<Group>> serviceResponse = groupService.searchByName(name);
        return handleApiResponse(serviceResponse);
    }
}
