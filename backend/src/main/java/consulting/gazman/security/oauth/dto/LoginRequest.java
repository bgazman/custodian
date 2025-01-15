package consulting.gazman.security.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
    private String clientId;
    private String redirectUri;
    private String state;
}

