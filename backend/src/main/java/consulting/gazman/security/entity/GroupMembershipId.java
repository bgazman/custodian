package consulting.gazman.security.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class GroupMembershipId implements Serializable {

    private Long userId;
    private Long groupId;

    // Default constructor
    public GroupMembershipId() {}

    // Parameterized constructor
    public GroupMembershipId(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    // Getters and setters (if needed)
}
