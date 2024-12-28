package consulting.gazman.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String code;        // Auth code to exchange for tokens
    private String redirectUri; // Where to redirect with code
    private String state;       // Original state param
    private String error;       // Optional error message
}