package consulting.gazman.security.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;



@Embeddable
@EqualsAndHashCode
public class GroupMembershipId implements Serializable {

    private Long userId;
    private Long groupId;
}
