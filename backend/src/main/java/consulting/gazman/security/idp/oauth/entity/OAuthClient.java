package consulting.gazman.security.idp.oauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "oauth_clients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "application_type", nullable = false, length = 50)
    private String applicationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_types", nullable = false, columnDefinition = "jsonb")
    private List<String> responseTypes;

    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    private String clientId;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "client_secret", nullable = false, length = 255)
    private String clientSecret;

    @Column(name = "client_secret_last_rotated", nullable = false)
    private LocalDateTime clientSecretLastRotated;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "redirect_uris", nullable = false, columnDefinition = "jsonb")
    private List<String> redirectUris;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "grant_types", nullable = false, columnDefinition = "jsonb")
    private List<String> grantTypes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allowed_scopes", columnDefinition = "jsonb")
    private List<String> allowedScopes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "default_scopes", columnDefinition = "jsonb")
    private List<String> defaultScopes;

    @Column(name = "token_endpoint_auth_method", length = 50, nullable = false)
    private String tokenEndpointAuthMethod;

    @Column(name = "algorithm", length = 10, nullable = false)
    private String algorithm;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "key_id", foreignKey = @ForeignKey(name = "fk_key_id"))
    private Secret signingKey;

    @Column(name = "access_token_expiry_seconds", nullable = false)
    private Integer accessTokenExpirySeconds;

    @Column(name = "refresh_token_expiry_seconds", nullable = false)
    private Integer refreshTokenExpirySeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}