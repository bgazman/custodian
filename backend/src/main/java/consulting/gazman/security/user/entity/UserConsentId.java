package consulting.gazman.security.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserConsentId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "client_id")
    private Long clientId;
}