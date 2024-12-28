package consulting.gazman.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {
    String grantType;    // "authorization_code" or "refresh_token"
    String code;         // For auth code grant
    String refreshToken; // For refresh token grant
    String clientId;
    String clientSecret;
    String redirectUri;
}