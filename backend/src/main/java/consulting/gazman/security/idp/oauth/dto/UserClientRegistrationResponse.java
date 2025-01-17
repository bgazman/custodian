package consulting.gazman.security.idp.oauth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserClientRegistrationResponse {
    private Long userId;
    private String clientId;
    private Boolean emailVerified;
    private Boolean mfaEnabled;
    private String mfaMethod;
    private LocalDateTime registeredAt;
}