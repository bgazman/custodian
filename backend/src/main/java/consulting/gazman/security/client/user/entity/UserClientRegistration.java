package consulting.gazman.security.client.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_client_registrations")
@Getter
@Setter
@EqualsAndHashCode
public class UserClientRegistration {

    @EmbeddedId
    private UserClientRegistrationId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("clientId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private OAuthClient client;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "mfa_enabled", nullable = false)
    private Boolean mfaEnabled = false;

    @Column(name = "mfa_method")
    private String mfaMethod;

    @Column(name = "consent_granted_at", nullable = false)
    private LocalDateTime consentGrantedAt;

    @Column(name = "last_used_at", nullable = false)
    private LocalDateTime lastUsedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}