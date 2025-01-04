package consulting.gazman.security.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
public class UserRoleId implements Serializable {

    private Long userId;
    private Long roleId;


}
