package consulting.gazman.security.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class AuthRequest {
    private String email;
    private String password;
    private String refreshToken;
}