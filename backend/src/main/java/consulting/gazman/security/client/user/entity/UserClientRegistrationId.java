package consulting.gazman.security.client.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserClientRegistrationId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "client_id")
    private Long clientId;
}