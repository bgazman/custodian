package consulting.gazman.security.client.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserAccessUpdateRequest {
    private Set<Long> roleIds;
    private Set<Long> groupIds;
    private Set<String> permissions;
}