package consulting.gazman.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthRequest {
    @JsonIgnore // Ignore this field during serialization/deserialization
    private String name;

    private String email;

    private String password;
    @JsonIgnore // Ignore this field during serialization/deserialization

    private String refreshToken;

    private String client_id;
}