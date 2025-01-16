package consulting.gazman.security.user.dto;

import lombok.*;

@Getter
@Setter
public class GroupDTO {
    private Long id;
    private String name;
    private String description;

    public GroupDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}

