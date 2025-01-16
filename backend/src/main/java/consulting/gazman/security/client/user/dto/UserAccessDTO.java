package consulting.gazman.security.client.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserAccessDTO {
    private Set<RoleDTO> roles;
    private Set<GroupDTO> groups;
    private Set<String> permissions;
}