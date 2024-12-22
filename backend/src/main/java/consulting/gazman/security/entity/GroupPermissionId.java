package consulting.gazman.security.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class GroupPermissionId implements Serializable {

    private Long groupId;
    private Long permissionId;

    // Default constructor
    public GroupPermissionId() {}

    // Parameterized constructor
    public GroupPermissionId(Long groupId, Long permissionId) {
        this.groupId = groupId;
        this.permissionId = permissionId;
    }
}
