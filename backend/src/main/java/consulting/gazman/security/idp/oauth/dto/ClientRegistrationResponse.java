// ClientRegistrationResponse.java
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
public class ClientRegistrationResponse {
    private String clientId;
    private String clientSecret;
    private String name;
    private Long keyId;
    private String applicationType;
    private List<String> redirectUris;
    private List<String> grantTypes;
    private List<String> allowedScopes;  // Changed from scopes to allowedScopes
    private List<String> defaultScopes;  // Added defaultScopes
    private List<String> responseTypes;
    private Long clientSecretExpiresAt;
}