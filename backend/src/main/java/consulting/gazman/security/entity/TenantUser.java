package consulting.gazman.security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_users")
@Getter
@Setter
public class TenantUser {

    @EmbeddedId
    private TenantUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tenantId")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @Column(name = "role")
    private String role;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;
}


