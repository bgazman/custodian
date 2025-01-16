package consulting.gazman.security.client.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Map<String, String> attributes;  // Custom user attributes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

