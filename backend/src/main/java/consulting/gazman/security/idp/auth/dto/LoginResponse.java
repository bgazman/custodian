package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String state;       // Original state parameter for CSRF protection
    private String error;       // Optional error message if login fails
    private String mfaMethod;   // Type of MFA required (SMS, EMAIL, etc.) - null if MFA not required
    private boolean mfaEnabled;
}
