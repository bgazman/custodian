package consulting.gazman.security.idp.oauth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserClientStatusResponse {
    private Boolean isRegistered;
    private Boolean emailVerified;
    private Boolean mfaEnabled;
    private String mfaMethod;
    private LocalDateTime lastUsedAt;
    private List<String> grantedScopes;
}