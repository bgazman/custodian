package consulting.gazman.security.idp.oauth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MfaUpdateRequest {
    private Boolean enabled;
    private String method;
    private String verificationCode;
}