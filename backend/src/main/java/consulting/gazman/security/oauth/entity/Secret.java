package consulting.gazman.security.oauth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "secrets")
@Getter
@Setter
public class Secret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name; // Example: "dashboard-private-key"

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey; // Encrypted value for private keys or raw for public keys

    @Column(name = "private_key", nullable = false, columnDefinition = "TEXT")
    private String privateKey; // Encrypted value for private keys or raw for public keys

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "last_rotated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastRotatedAt;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}