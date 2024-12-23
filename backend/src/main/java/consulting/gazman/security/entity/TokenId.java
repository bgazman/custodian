package consulting.gazman.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class TokenId implements Serializable {

    @Column(name = "key_id", nullable = false, length = 100)
    private String keyId; // Unique identifier for the key

    @Column(name = "app_name", nullable = false, length = 100)
    private String appName; // Application name

    // Override equals and hashCode for proper behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenId tokenId = (TokenId) o;

        if (!keyId.equals(tokenId.keyId)) return false;
        return appName.equals(tokenId.appName);
    }

    @Override
    public int hashCode() {
        int result = keyId.hashCode();
        result = 31 * result + appName.hashCode();
        return result;
    }
}
