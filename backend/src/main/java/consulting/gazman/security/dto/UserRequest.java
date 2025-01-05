package consulting.gazman.security.dto;

import consulting.gazman.security.entity.User;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Long id; // Required for updates

    private String name;

    private String email;

    private String password;

    private Boolean enabled; // Optional; defaults to `false`
    private Boolean mfaEnabled; // Optional; defaults to `false`
    private String phoneNumber; // Optional

    private String mfaMethod; // Optional, can be null
    private String mfaBackupCodes; // Optional, can be null

    private Set<Long> roleIds; // Role IDs to associate with the user
    private int failedLoginAttempts;
}



