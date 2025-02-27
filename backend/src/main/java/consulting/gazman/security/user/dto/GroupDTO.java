package consulting.gazman.security.user.dto;

import consulting.gazman.security.client.constants.GroupRole;
import lombok.*;

@Getter
@Setter
public class GroupDTO {
    private Long id;
    private String name;
    private String description;
    private GroupRole groupRole;  // Added field

    public GroupDTO(Long id, String name, String description, GroupRole groupRole) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.groupRole = groupRole;
    }
}

