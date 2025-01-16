package consulting.gazman.security.client.user.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;



@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
public class GroupMembershipId implements Serializable {

    private Long userId;
    private Long groupId;
}
