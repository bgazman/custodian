package consulting.gazman.security.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonType;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_consents")
@Getter
@Setter
@EqualsAndHashCode
public class UserConsent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserConsentId id; // No default initialization

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"userConsents"})
    private OAuthClient client;

    @Column(name = "scopes")
    @Type(JsonType.class)
    private List<String> scopes;

    @Column(name = "granted_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime grantedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}