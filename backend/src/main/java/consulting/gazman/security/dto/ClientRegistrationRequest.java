package consulting.gazman.security.dto;

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
    private String name;                   // changed from clientName to match entity
    private String applicationType;
    private List<String> redirectUris;
    private List<String> grantTypes;
    private List<String> scopes;          // changed from String scope to List<String> scopes
    private List<String> responseTypes;

}