package consulting.gazman.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    String grantType;    // "authorization_code" or "refresh_token"
    String state;
    String code;         // For auth code grant
    String refreshToken; // For refresh token grant
    String clientId;
    String clientSecret;
    String redirectUri;
}