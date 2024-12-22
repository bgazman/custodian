package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.GroupRepository;
import consulting.gazman.security.service.GroupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public ApiResponse<List<Group>> getAllGroups() {
        try {
            List<Group> groups = groupRepository.findAll();
            return ApiResponse.success(groups, "Groups retrieved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving groups.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Group> findById(Long id) {
        try {
            Group group = groupRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
            return ApiResponse.success(group, "Group retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Group not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Group> save(Group group) {
        try {
            Group savedGroup = groupRepository.save(group);
            return ApiResponse.success(savedGroup, "Group saved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while saving the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Void> delete(Long id) {
        try {
            Group group = groupRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
            groupRepository.delete(group);
            return ApiResponse.success(null, "Group deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Group not found with the provided ID.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while deleting the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }

    @Override
    public ApiResponse<Group> findByName(String name) {
        try {
            Group group = groupRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found with name: " + name));
            return ApiResponse.success(group, "Group retrieved successfully.");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error(
                    "not_found",
                    "Group not found with the provided name.",
                    ApiError.of("not_found", ex.getMessage())
            );
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while retrieving the group.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }


    @Override
    public ApiResponse<List<Group>> searchByName(String partialName) {
        try {
            List<Group> groups = groupRepository.findByNameContaining(partialName);
            return ApiResponse.success(groups, "Groups retrieved successfully.");
        } catch (Exception ex) {
            return ApiResponse.error(
                    "server_error",
                    "An unexpected error occurred while searching for groups.",
                    ApiError.of("server_error", ex.getMessage())
            );
        }
    }
}
