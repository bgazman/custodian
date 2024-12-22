package consulting.gazman.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "group_memberships")
@Getter
@Setter
public class GroupMembership {

    @EmbeddedId
    private GroupMembershipId id;

    @Column(name = "role", nullable = false)
    private String role; // Example: "ADMIN", "MEMBER"

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // Additional fields or methods, if needed, can go here
}
