package consulting.gazman.security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_clients")
@Getter
@Setter
public class OAuthClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    private String clientId; // Unique identifier for the client

    @Column(name = "client_secret", nullable = false, length = 255)
    private String clientSecret; // Hashed client secret

    @Column(name = "redirect_uris", nullable = false, columnDefinition = "JSONB")
    private String redirectUris; // JSON array of allowed redirect URIs

    @Column(name = "grant_types", nullable = false, columnDefinition = "JSONB")
    private String grantTypes; // JSON array of allowed grant types (e.g., authorization_code)

    @Column(name = "scopes", columnDefinition = "JSONB")
    private String scopes; // JSON array of allowed scopes (e.g., openid, profile, email)

    @Column(name = "token_endpoint_auth_method", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'client_secret_basic'")
    private String tokenEndpointAuthMethod = "client_secret_basic"; // Default auth method

    @Column(name = "algorithm", nullable = false, length = 10)
    private String algorithm = "RS256"; // Default signing algorithm

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "key_id", referencedColumnName = "id")
    private Secret secret; // Reference to the secret entity

    @Column(name = "access_token_expiry_seconds", nullable = false)
    private Integer accessTokenExpirySeconds = 3600;

    @Column(name = "refresh_token_expiry_seconds", nullable = false)
    private Integer refreshTokenExpirySeconds = 86400;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
