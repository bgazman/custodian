package consulting.gazman.security.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
@Setter
public class UserRoleId implements Serializable {

    private Long userId;
    private Long roleId;


}
