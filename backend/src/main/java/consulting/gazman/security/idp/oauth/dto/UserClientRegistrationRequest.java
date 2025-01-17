package consulting.gazman.security.idp.oauth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserClientRegistrationRequest {
    private String email;
    private String password;
    private String name;
    private String clientId;
    private Boolean requireMfa;
    private String mfaMethod;
}