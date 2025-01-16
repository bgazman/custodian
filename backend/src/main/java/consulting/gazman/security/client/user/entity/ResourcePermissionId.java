package consulting.gazman.security.client.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ResourcePermissionId implements Serializable {

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;
}
