package consulting.gazman.security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_configuration")
@Getter
@Setter
public class TokenConfiguration {

    @EmbeddedId
    private TokenId tokenId; // Composite ID with key_id and app_name

    @Column(name = "access_token_expiration_minutes", nullable = false)
    private Integer accessTokenExpirationMinutes = 15; // Default: 15 minutes

    @Column(name = "refresh_token_expiration_minutes", nullable = false)
    private Integer refreshTokenExpirationMinutes = 10080; // Default: 7 days

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "secret_id", referencedColumnName = "id")
    private Secret secretKey; // Reference to the `Secret` entity

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey; // Public key in PEM format

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_key_id", referencedColumnName = "id")
    private Secret privateKey;
    @Column(name = "algorithm", nullable = false)
    private String algorithm; // Private key in PEM format
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
