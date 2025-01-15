package consulting.gazman.security.mapper;

import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface UserMapper {

    // Map User to UserBasicDTO
    @Mapping(target = "roleNames", source = "userRoles", qualifiedByName = "mapRoleNames")
    UserBasicDTO toBasicDTO(User user);



    // Map List<User> to List<UserBasicDTO>
    List<UserBasicDTO> toBasicDTOList(List<User> users);

    // Mapping UserCreateRequest to User
    @Mapping(target = "userRoles", ignore = true)  // Roles handled in service layer
    @Mapping(target = "groupMemberships", ignore = true)  // Groups handled in service layer
    User toEntity(UserCreateRequest request);

    // Update an existing User entity using UserUpdateRequest
    @Mapping(target = "userRoles", ignore = true)  // Roles handled in service layer
    @Mapping(target = "groupMemberships", ignore = true)  // Groups handled in service layer
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);

    // Helper method for mapping role names
    @Named("mapRoleNames")
    default Set<String> mapRoleNames(Set<UserRole> userRoles) {
        if (userRoles == null) {
            return Collections.emptySet();
        }
        return userRoles.stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toSet());
    }

    // Custom method to map Set<GroupMembership> to Set<GroupDTO>
    @Named("mapGroupNames")
    default Set<GroupDTO> mapGroupNames(Set<GroupMembership> groupMemberships) {
        if (groupMemberships == null) {
            return Collections.emptySet();
        }
        return groupMemberships.stream()
                .map(groupMembership -> new GroupDTO(groupMembership.getGroup().getId(), groupMembership.getGroup().getName()))
                .collect(Collectors.toSet());
    }


    @Mapping(target = "profile", source = "user", qualifiedByName = "toProfileDTO")
    @Mapping(target = "status", source = "user", qualifiedByName = "toStatusDTO")
    @Mapping(target = "security", source = "user", qualifiedByName = "toSecurityDTO")
    @Mapping(target = "access", source = "user", qualifiedByName = "toAccessDTO")
    @Mapping(target = "lastPasswordChange", source = "lastPasswordChange")
    UserDetailsDTO toDetailsDTO(User user);

    // Map UserProfileDTO
    @Named("toProfileDTO")
    default UserProfileDTO toProfileDTO(User user) {
        return UserProfileDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    // Map UserStatusDTO
    @Named("toStatusDTO")
    default UserStatusDTO toStatusDTO(User user) {
        return UserStatusDTO.builder()
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .accountNonLocked(user.isAccountNonLocked())
                .accountNonExpired(user.isAccountNonExpired())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .build();
    }

    // Map UserSecurityDTO
    @Named("toSecurityDTO")
    default UserSecurityDTO toSecurityDTO(User user) {
        return UserSecurityDTO.builder()
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lockedUntil(user.getLockedUntil())
                .mfaEnabled(user.isMfaEnabled())
                .mfaMethod(user.getMfaMethod())
                .mfaBackupCodes(user.getMfaBackupCodes())
                .lastPasswordChange(user.getLastPasswordChange())
                .build();
    }

    // Map UserAccessDTO
    @Named("toAccessDTO")
    default UserAccessDTO toAccessDTO(User user) {
        return UserAccessDTO.builder()
                .roles(user.getUserRoles().stream()
                        .map(userRole -> new RoleDTO(userRole.getRole().getId(), userRole.getRole().getName()))
                        .collect(Collectors.toSet()))
                .groups(user.getGroupMemberships().stream()
                        .map(groupMembership -> new GroupDTO(groupMembership.getGroup().getId(), groupMembership.getGroup().getName()))
                        .collect(Collectors.toSet()))
                .build();
    }}
