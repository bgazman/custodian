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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = true) // Role is optional
    private Role role;

    // Constructor with User and Group
    public GroupMembership(User user, Group group) {
        this.id = new GroupMembershipId();
        this.id.setUserId(user.getId());
        this.id.setGroupId(group.getId());
        this.user = user;
        this.group = group;
    }

    // Constructor with User, Group, and Role
    public GroupMembership(User user, Group group, Role role) {
        this(user, group);
        this.role = role;
    }
}
