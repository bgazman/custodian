package consulting.gazman.security.dto;

import lombok.*;

@Getter
@Setter

public class GroupDTO {
    private Long id;
    private String name;
    private String description;

    public GroupDTO(Long id, String name) {
    }
}
