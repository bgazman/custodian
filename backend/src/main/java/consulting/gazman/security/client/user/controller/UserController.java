package consulting.gazman.security.client.user.controller;

import consulting.gazman.security.client.user.dto.*;
import consulting.gazman.security.client.user.service.*;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.client.user.entity.GroupMembership;
import consulting.gazman.security.client.user.entity.User;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.client.user.entity.UserRole;

import consulting.gazman.security.client.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController

public class UserController extends ApiController implements IUserController {

    @Autowired
    private UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    GroupMembershipService groupMembershipService;
    @Autowired
    GroupPermissionService groupPermissionService;
    @Autowired
    RolePermissionService rolePermissionService;
    @GetMapping
    @Override
    public ResponseEntity<List<UserBasicDTO>> getAllUsers() {
        try {
            // Fetch users and map to DTOs
            List<User> users = userService.getAllUsers();
            List<UserBasicDTO> userDTOs = userMapper.toBasicDTOList(users);

            // Use the abstract controller's utility method
            return super.wrapSuccessResponse(userDTOs,"User list retrieved");
        } catch (AppException e) {
            // Application-specific error
            return (ResponseEntity) super.wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General error
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserDetailsDTO> getUser(Long id) {
        try {
            User user = userService.findById(id);

            // Get user roles
            List<UserRole> userRoles = userRoleService.getRolesForUser(id);
            List<Long> roleIds = userRoles.stream()
                    .map(userRole -> userRole.getRole().getId())
                    .collect(Collectors.toList());

            // Get user groups
            List<GroupMembership> userGroups = groupMembershipService.getGroupsForUser(id);
            List<Long> groupIds = userGroups.stream()
                    .map(groupMembership -> groupMembership.getGroup().getId())
                    .collect(Collectors.toList());


            Map<Long, List<String>> rolePermissions = rolePermissionService.findPermissionsByRoleIds(roleIds)
                    .stream()
                    .collect(Collectors.groupingBy(
                            rp -> rp.getRole().getId(),
                            Collectors.mapping(rp -> rp.getPermission().getName(), Collectors.toList())
                    ));
            Map<Long, List<String>> groupPermissions = groupPermissionService.findPermissionsByGroupIds(groupIds)
                    .stream()
                    .collect(Collectors.groupingBy(
                            gp -> gp.getGroup().getId(),
                            Collectors.mapping(gp -> gp.getPermission().getName(), Collectors.toList())
                    ));

            UserDetailsDTO userDetailsDTO = userMapper.toDetailsDTO(user, rolePermissions, groupPermissions);
            return super.wrapSuccessResponse(userDetailsDTO, "User found");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<UserDetailsDTO> createUser(UserCreateRequest request) {
        try {
            User user = userMapper.toEntity(request);
            User createdUser = userService.createUser(user, request.getRoleIds(), request.getGroupIds());

            // Fetch role permissions
            Map<Long, List<String>> rolePermissions = rolePermissionService.findPermissionsByRoleIds(request.getRoleIds().stream().toList())
                    .stream()
                    .collect(Collectors.groupingBy(
                            rp -> rp.getRole().getId(),
                            Collectors.mapping(rp -> rp.getPermission().getName(), Collectors.toList())
                    ));

            // Fetch group permissions
            Map<Long, List<String>> groupPermissions = groupPermissionService.findPermissionsByGroupIds(request.getGroupIds().stream().toList())
                    .stream()
                    .collect(Collectors.groupingBy(
                            gp -> gp.getGroup().getId(),
                            Collectors.mapping(gp -> gp.getPermission().getName(), Collectors.toList())
                    ));

            UserDetailsDTO userDetailsDTO = userMapper.toDetailsDTO(createdUser, rolePermissions, groupPermissions);
            return super.wrapSuccessResponse(userDetailsDTO, "User created successfully");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("VALIDATION_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<UserDetailsDTO> updateUser(Long id, UserUpdateRequest request) {
        try {
            User existingUser = userService.findById(id);
            userMapper.updateEntity(request, existingUser);

            Set<Long> roleIds = request.getRoleIds();
            Set<Long> groupIds = request.getGroupIds();

            if (roleIds != null && !roleIds.isEmpty()) {
                userService.updateUserRoles(existingUser.getId(), roleIds);
            }
            if (groupIds != null && !groupIds.isEmpty()) {
                userService.updateUserGroups(existingUser.getId(), groupIds);
            }

            User updatedUser = userService.updateUser(existingUser);

            // Fetch role permissions
            Map<Long, List<String>> rolePermissions = rolePermissionService.findPermissionsByRoleIds(new ArrayList<>(roleIds))
                    .stream()
                    .collect(Collectors.groupingBy(
                            rp -> rp.getRole().getId(),
                            Collectors.mapping(rp -> rp.getPermission().getName(), Collectors.toList())
                    ));

            // Fetch group permissions
            Map<Long, List<String>> groupPermissions = groupPermissionService.findPermissionsByGroupIds(new ArrayList<>(groupIds))
                    .stream()
                    .collect(Collectors.groupingBy(
                            gp -> gp.getGroup().getId(),
                            Collectors.mapping(gp -> gp.getPermission().getName(), Collectors.toList())
                    ));

            UserDetailsDTO userDetailsDTO = userMapper.toDetailsDTO(updatedUser, rolePermissions, groupPermissions);
            return super.wrapSuccessResponse(userDetailsDTO, "User updated successfully");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build(); // RESTful DELETE with no content
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserStatusDTO> updateUserStatus(Long id, UserStatusUpdateRequest request) {
        try {
            User user = userService.updateUserStatus(id, request);
            UserStatusDTO userStatusDTO = userMapper.toStatusDTO(user);

            return super.wrapSuccessResponse(userStatusDTO,"User status details retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserSecurityDTO> updateUserSecurity(Long id, UserSecurityUpdateRequest request) {
        try {
            User user = userService.updateUserSecurity(id, request);
            UserSecurityDTO userSecurityDTO = userMapper.toSecurityDTO(user);

            return super.wrapSuccessResponse(userSecurityDTO,"User security details retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserAccessDTO> getUserAccess(Long id) {
        try {
            Set<UserRole> userRoles = new HashSet<>(userRoleService.getRolesForUser(id));
            Set<GroupMembership> groupMemberships = new HashSet<>(groupMembershipService.getGroupsForUser(id));

// Get permissions for each role and group
            Map<Long, List<String>> rolePermissions = userRoles.stream()
                    .collect(Collectors.toMap(
                            ur -> ur.getRole().getId(),
                            ur -> {
                                List<String> permissions = rolePermissionService.findByRoleId(ur.getRole().getId())
                                        .stream()
                                        .map(rp -> rp.getPermission().getName())
                                        .collect(Collectors.toList());
                                System.out.println("Role " + ur.getRole().getId() + " permissions: " + permissions);
                                return permissions;
                            }
                    ));

            Map<Long, List<String>> groupPermissions = groupMemberships.stream()
                    .collect(Collectors.toMap(
                            gm -> gm.getGroup().getId(),
                            gm -> {
                                List<String> permissions = groupPermissionService.getGroupPermissions(gm.getGroup().getId());
                                System.out.println("Group " + gm.getGroup().getId() + " permissions: " + permissions);
                                return permissions;
                            }
                    ));

            System.out.println("Role permissions map: " + rolePermissions);
            System.out.println("Group permissions map: " + groupPermissions);

            UserAccessDTO userAccessDTO = userMapper.toAccessDTO(userRoles, groupMemberships, rolePermissions, groupPermissions);

            return super.wrapSuccessResponse(userAccessDTO, "User access details retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<UserProfileDTO> getUserProfile(Long id) {
        try {
            // Fetch the User entity from the service
            User user = userService.findById(id);

            // Map the User entity to UserProfileDTO using the mapper
            UserProfileDTO userProfileDTO = userMapper.toProfileDTO(user);
            return super.wrapSuccessResponse(userProfileDTO,"User profile retrieved");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserBasicDTO> getUserByEmail(String email) {
        try {
            User user = userService.findByEmail(email);
            UserBasicDTO userBasicDTO = userMapper.toBasicDTO(user);

            return super.wrapSuccessResponse(userBasicDTO,"User found");
        } catch (AppException e) {
            return (ResponseEntity) super.wrapErrorResponse("USER_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return (ResponseEntity) super.wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}