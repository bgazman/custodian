package consulting.gazman.security.idp.oauth.entity;

import consulting.gazman.security.user.entity.User;
import jakarta.persistence.*;

import lombok.*;


import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_client_id"))
    private OAuthClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_id"))
    private User user;

    @Column(name = "token_type", nullable = false, length = 50)
    private String tokenType; // e.g., 'access_token' or 'refresh_token'

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token; // The token value (hashed for security)

    @Column(name = "issued_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rotated_to", foreignKey = @ForeignKey(name = "fk_rotated_to"))
    private Token rotatedTo; // Token ID for rotation tracking


}
