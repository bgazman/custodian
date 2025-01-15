package consulting.gazman.security.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserSecurityDTO {
    private boolean enabled;
    private boolean mfaEnabled;
    private String mfaMethod;
    private boolean emailVerified;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private int failedLoginAttempts;
    private LocalDateTime lockedUntil;
    private String mfaBackupCodes;
    private LocalDateTime lastPasswordChange;

}