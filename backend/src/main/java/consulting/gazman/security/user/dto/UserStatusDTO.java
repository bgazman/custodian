package consulting.gazman.security.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserStatusDTO {
    // Account State
    private boolean enabled;
    private boolean accountLocked;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;

    private LocalDateTime lockedUntil;

    // Authentication Status
    private int failedLoginAttempts;
    private LocalDateTime lastLoginTime;

    // Verification Status
    private boolean emailVerified;
    private boolean mfaEnabled;

    // Expiry Status
    private boolean accountExpired;
    private boolean credentialsExpired;
}