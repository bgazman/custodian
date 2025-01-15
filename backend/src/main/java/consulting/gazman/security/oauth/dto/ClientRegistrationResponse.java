package consulting.gazman.security.oauth.dto;

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
    private Long keyId;// changed from clientName
    private String applicationType;
    private List<String> redirectUris;
    private List<String> grantTypes;
    private List<String> scopes;          // changed from scope
    private List<String> responseTypes;
    private Long clientSecretExpiresAt;
}