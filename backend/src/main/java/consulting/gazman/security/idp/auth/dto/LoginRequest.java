package consulting.gazman.security.idp.auth.dto;

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
    private String responseType;  // Add this field
    private String scope;         // Add this field
}