package consulting.gazman.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupsResponse {
    private Long userId; // User ID
    private List<GroupDTO> groupDTOS; // List of groups the user belongs to
}
