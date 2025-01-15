package consulting.gazman.security.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class UserAttributeId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "key", nullable = false)
    private String key; // Composite key includes the attribute key
}
