package consulting.gazman.security.client.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserSecurityUpdateRequest {
    private boolean mfaEnabled;
    private String mfaMethod;
    private String password;
    private String newPassword;
    private String mfaBackupCodes;
    private boolean resetFailedAttempts;
}