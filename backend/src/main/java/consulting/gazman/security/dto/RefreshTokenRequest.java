package consulting.gazman.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenRequest {
    private String refreshToken; // The refresh token provided during login
}
