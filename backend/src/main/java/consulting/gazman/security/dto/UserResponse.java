package consulting.gazman.security.dto;

import consulting.gazman.security.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id; // User ID
    private String name; // User's name
    private String email; // User's email
    private Boolean enabled; // Whether the user is enabled
    private Boolean mfaEnabled; // Whether MFA is enabled
    private String phoneNumber; // User's phone number (if provided)
    private LocalDateTime createdAt; // When the user was created
    private LocalDateTime updatedAt; // Last updated timestamp

    // Roles associated with the user
    private Set<String> roles; // Role names instead of IDs for better readability
}
