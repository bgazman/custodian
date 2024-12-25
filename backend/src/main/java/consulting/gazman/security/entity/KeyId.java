package consulting.gazman.security.entity;

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
public class KeyId implements Serializable {

    @Column(name = "key_id", nullable = false, length = 100)
    private String keyId; // Unique identifier for the key


}
