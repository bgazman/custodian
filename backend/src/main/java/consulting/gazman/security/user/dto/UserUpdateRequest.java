package consulting.gazman.security.user.dto;

import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateRequest {
    private String name;
    private String phoneNumber;
    private Set<Long> roleIds;
    private Set<Long> groupIds;
    private Map<String, String> attributes;
    // Email and password have separate endpoints for security
}