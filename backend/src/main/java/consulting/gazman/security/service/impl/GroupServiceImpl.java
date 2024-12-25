package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.Group;
import consulting.gazman.security.exception.AppException;
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
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Group not found with ID: " + id));
    }

    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public void delete(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("Group not found with ID: " + id));
        groupRepository.delete(group);
    }

    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name)
                .orElseThrow(() -> AppException.resourceNotFound("Group not found with name: " + name));
    }

    @Override
    public List<Group> searchByName(String partialName) {
        return groupRepository.findByNameContaining(partialName);
    }
}
