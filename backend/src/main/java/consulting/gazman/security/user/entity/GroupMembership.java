package consulting.gazman.security.user.entity;

import consulting.gazman.security.client.constants.GroupRole;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "group_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMembership implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private GroupMembershipId id = new GroupMembershipId();

    @NonNull  // Add this
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

        @NonNull  // Add this
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "group_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupRole groupRole = GroupRole.MEMBER; // Default value
    public GroupMembership(User user, Group group) {
    }
}
