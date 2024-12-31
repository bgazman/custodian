package consulting.gazman.security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenants")
@Getter
@Setter
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<TenantUser> tenantUsers = new ArrayList<>();

    @Column(name = "issuer_url", nullable = false, unique = true)
    private String issuerUrl;

    @Column(name = "jwks_uri", nullable = false)
    private String jwksUri;

    @Column(name = "token_lifetime", nullable = false)
    private Integer tokenLifetime;

    @Column(name = "refresh_token_lifetime", nullable = false)
    private Integer refreshTokenLifetime;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
