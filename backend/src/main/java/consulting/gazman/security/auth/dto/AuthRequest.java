package consulting.gazman.security.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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