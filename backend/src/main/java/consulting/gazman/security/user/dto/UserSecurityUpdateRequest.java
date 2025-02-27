package consulting.gazman.security.user.dto;

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
    private String mfaRecoveryCodes;
    private boolean resetFailedAttempts;
}