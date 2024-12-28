package consulting.gazman.security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<TenantUser> tenantUsers = new ArrayList<>();
}
