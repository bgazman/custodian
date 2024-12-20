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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name", nullable = false, unique = true)
    private String appName;

    @Column(name = "access_token_expiration_minutes", nullable = false)
    private int accessTokenExpirationMinutes;

    @Column(name = "refresh_token_expiration_minutes", nullable = false)
    private int refreshTokenExpirationMinutes;

    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
