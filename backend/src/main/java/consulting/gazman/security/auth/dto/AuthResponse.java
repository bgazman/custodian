package consulting.gazman.security.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private Long expiresIn;
}