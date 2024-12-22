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
    private String appName; // Example: "GLOBAL", "APP1", "APP2"

    @Column(name = "access_token_expiration_minutes", nullable = false)
    private Integer accessTokenExpirationMinutes = 15; // Default: 15 minutes

    @Column(name = "refresh_token_expiration_minutes", nullable = false)
    private Integer refreshTokenExpirationMinutes = 10080; // Default: 7 days

    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
