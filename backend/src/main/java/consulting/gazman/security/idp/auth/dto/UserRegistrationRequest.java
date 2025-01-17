package consulting.gazman.security.idp.auth.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    // Core user information
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    // Basic registration metadata
    private String registrationSource;
    private Map<String, String> attributes;  // Custom user attributes
    private Map<String, Object> metadata;    // Registration metadata

    // Legal/Compliance
    private boolean termsAccepted;
    private LocalDateTime termsAcceptedAt;

    // Authorization requests
    private List<String> requestedRoles;
    private List<String> requestedGroups;

    // Client identification (needed to establish relationship later)
    private String clientId;  // Keep this to know which client initiated registration
}