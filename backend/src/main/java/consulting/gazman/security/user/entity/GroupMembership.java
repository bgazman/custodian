package consulting.gazman.security.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_memberships")
@Getter
@Setter
@NoArgsConstructor
public class GroupMembership {

    @EmbeddedId
    private GroupMembershipId id = new GroupMembershipId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;


    public GroupMembership(User user, Group group) {
    }
}
