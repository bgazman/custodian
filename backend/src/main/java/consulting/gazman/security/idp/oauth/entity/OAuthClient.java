package consulting.gazman.security.idp.oauth.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.type.SqlTypes;


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

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "revoked_at " )
    private LocalDateTime revokedAt;

    @Column(name = "client_secret", nullable = false, length = 255)
    private String clientSecret;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "redirect_uris", nullable = false, columnDefinition = "jsonb")
    private List<String> redirectUris;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "grant_types", nullable = false, columnDefinition = "jsonb")
    private List<String> grantTypes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scopes", columnDefinition = "jsonb", nullable = false)
    private List<String> scopes;

    @Column(name = "token_endpoint_auth_method", length = 50, nullable = false)
    private String tokenEndpointAuthMethod;

    @Column(name = "algorithm", length = 10, nullable = false)
    private String algorithm;

    @ManyToOne(fetch = FetchType.EAGER) // Many clients can use the same secret
    @JoinColumn(name = "key_id", foreignKey = @ForeignKey(name = "fk_key_id"))
    private Secret signingKey; // Association to the Secret entity

    @Column(name = "access_token_expiry_seconds", nullable = false)
    private Integer accessTokenExpirySeconds;

    @Column(name = "refresh_token_expiry_seconds", nullable = false)
    private Integer refreshTokenExpirySeconds;
    @Column(name = "client_secret_last_rotated", nullable = false, updatable = false)
    private LocalDateTime clientSecretLastRotated;


    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
