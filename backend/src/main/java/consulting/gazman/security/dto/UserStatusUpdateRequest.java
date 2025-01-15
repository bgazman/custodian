package consulting.gazman.security.dto;

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

