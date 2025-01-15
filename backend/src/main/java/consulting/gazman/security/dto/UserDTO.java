package consulting.gazman.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean enabled;
    private boolean mfaEnabled;
    private String mfaMethod;
    private String mfaBackupCodes; // Optional: expose only if needed
    private boolean emailVerified;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private int failedLoginAttempts;
    private LocalDateTime lockedUntil;
    private LocalDateTime lastLoginTime;
    private LocalDateTime lastPasswordChange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoleDTO> roles; // Represents the user's roles
    private Set<Long> roleIds;  // Raw role IDs
 }
