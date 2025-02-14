package consulting.gazman.security.idp.oauth.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeRequest {
    private String responseType;  // "code"
    private String clientId;
    private String redirectUri;
    private String scope;
    private String state;
    private String email;
    private String password;
    private String codeChallenge;
    private String codeChallengeMethod;
}