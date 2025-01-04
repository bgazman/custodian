package consulting.gazman.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String code;        // Authorization code to exchange for tokens
    private String redirectUri; // Where to redirect with the code after login
    private String state;       // Original state parameter for CSRF protection
    private String error;       // Optional error message if login fails
    private String mfaMethod;   // Type of MFA required (SMS, EMAIL, etc.) - null if MFA not required
}
