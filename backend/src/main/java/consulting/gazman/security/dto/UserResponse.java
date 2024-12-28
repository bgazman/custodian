package consulting.gazman.security.dto;

import consulting.gazman.security.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Data
@Builder
public class UserResponse {
    private Long id; // User's unique identifier
    private String email; // User's email
    private Set<String> roles; // Changed from single role to Set
    private boolean enabled; // Whether the account is enabled
    private boolean emailVerified; // Whether the email is verified
    private boolean accountNonExpired; // Whether the account is non-expired
    private boolean accountNonLocked; // Whether the account is non-locked
    private boolean credentialsNonExpired; // Whether the credentials are non-expired
    private LocalDateTime lastLoginTime; // Last login time
    private LocalDateTime lastPasswordChange; // Last password change time
    private LocalDateTime createdAt; // Account creation time
    private LocalDateTime updatedAt; // Last update time

    // Static method for building a UserResponse from a User entity
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))

                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .lastLoginTime(user.getLastLoginTime())
                .lastPasswordChange(user.getLastPasswordChange())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}