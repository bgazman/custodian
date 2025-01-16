package consulting.gazman.security.client.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserBasicDTO {
    // Identity
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;

    // Essential Status (needed for user lists/tables)
    private boolean enabled;
    private boolean accountNonLocked;
    private LocalDateTime lockedUntil;

    // Access Summary (for quick view)
    private Set<String> roleNames;  // Just role names instead of full RoleDTO

    // Timestamps
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}