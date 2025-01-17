package consulting.gazman.security.idp.oauth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateRegistrationRequest {
    private Boolean mfaEnabled;
    private String mfaMethod;
    private List<String> scopes;
}