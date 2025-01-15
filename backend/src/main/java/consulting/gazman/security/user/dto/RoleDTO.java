package consulting.gazman.security.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id; // Role ID
    private String name; // Role name (e.g., ADMIN, USER)
    private String description; // Optional description of the role

    public RoleDTO(Long id, String name) {
    }
}