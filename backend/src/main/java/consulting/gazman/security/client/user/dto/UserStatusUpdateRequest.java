package consulting.gazman.security.client.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserStatusUpdateRequest {
    private boolean enabled;
    private boolean accountLocked;
    private boolean resetFailedAttempts;
    private boolean emailVerified;
}

