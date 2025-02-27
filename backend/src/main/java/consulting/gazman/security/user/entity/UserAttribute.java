package consulting.gazman.security.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_attributes")
@Getter
@Setter
@EqualsAndHashCode
public class UserAttribute {

    @EmbeddedId
    private UserAttributeId id = new UserAttributeId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    @Column(name = "value", nullable = false)
    private String value; // Example: "US", "Engineering"

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
