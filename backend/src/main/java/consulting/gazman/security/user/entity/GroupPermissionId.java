package consulting.gazman.security.user.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;



@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class GroupPermissionId implements Serializable {

    private Long groupId;
    private Long permissionId;
}
