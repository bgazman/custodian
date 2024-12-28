package consulting.gazman.security.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String idToken;
    String tokenType;    // "Bearer"
    Long expiresIn;
    String error;
    String errorDescription;
}