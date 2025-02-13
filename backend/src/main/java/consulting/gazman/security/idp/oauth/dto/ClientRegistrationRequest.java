// ClientRegistrationRequest.java
package consulting.gazman.security.idp.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRegistrationRequest {
    private String name;
    private String applicationType;
    private List<String> redirectUris;
    private List<String> grantTypes;
    private List<String> allowedScopes;  // Changed from scopes to allowedScopes
    private List<String> defaultScopes;  // Added defaultScopes
    private List<String> responseTypes;
    private String clientId;
}