package consulting.gazman.security.user.mapper;

import consulting.gazman.security.user.entity.*;
import consulting.gazman.security.user.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Map User to UserBasicDTO
    @Mapping(target = "roleNames", source = "userRoles", qualifiedByName = "mapRoleNames")
    UserBasicDTO toBasicDTO(User user);



    // Map List<User> to List<UserBasicDTO>
    List<UserBasicDTO> toBasicDTOList(List<User> users);

    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "groupMemberships", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "mfaEnabled", constant = "false")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "failedLoginAttempts", constant = "0")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "mfaMethod", ignore = true)
    @Mapping(target = "mfaBackupCodes", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "lastLoginTime", ignore = true)
    @Mapping(target = "lastPasswordChange", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "mfaMethod", ignore = true)
    @Mapping(target = "mfaBackupCodes", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "lastLoginTime", ignore = true)
    @Mapping(target = "lastPasswordChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "authorities", ignore = true)
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
                .map(groupMembership -> new GroupDTO(groupMembership.getGroup().getId(), groupMembership.getGroup().getName(),groupMembership.getGroup().getDescription()))
                .collect(Collectors.toSet());
    }

    @Mapping(target = "profile", source = "user", qualifiedByName = "toProfileDTO")
    @Mapping(target = "status", source = "user", qualifiedByName = "toStatusDTO")
    @Mapping(target = "security", source = "user", qualifiedByName = "toSecurityDTO")
    @Mapping(target = "access", expression = "java(toAccessDTO(user.getUserRoles(), user.getGroupMemberships(), rolePermissions, groupPermissions))")
    @Mapping(target = "lastPasswordChange", source = "user.lastPasswordChange")
    UserDetailsDTO toDetailsDTO(User user, Map<Long, List<String>> rolePermissions, Map<Long, List<String>> groupPermissions);

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

    @Named("toAccessDTO")
    default UserAccessDTO toAccessDTO(
            Set<UserRole> userRoles,
            Set<GroupMembership> groupMemberships,
            Map<Long, List<String>> rolePermissions,
            Map<Long, List<String>> groupPermissions
    ) {

        Set<RoleDTO> roles = userRoles.stream()
                .map(userRole -> new RoleDTO(
                        userRole.getRole().getId(),
                        userRole.getRole().getName(),
                        userRole.getRole().getDescription()
                ))
                .collect(Collectors.toSet());

        Set<GroupDTO> groupDTOs = groupMemberships.stream()
                .map(groupMembership -> {
                    Group group = groupMembership.getGroup();
                    return new GroupDTO(group.getId(), group.getName(), group.getDescription());

                })
                .collect(Collectors.toSet());

        Set<String> allPermissions = Stream.concat(
                rolePermissions.values().stream().flatMap(List::stream),
                groupPermissions.values().stream().flatMap(List::stream)
        ).collect(Collectors.toSet());



        return UserAccessDTO.builder()
                .roles(roles)
                .groups(groupDTOs)
                .permissions(allPermissions)
                .build();
    }

}
