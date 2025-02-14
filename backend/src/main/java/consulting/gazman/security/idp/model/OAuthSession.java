package consulting.gazman.security.idp.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String state;
    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;
    private String codeChallenge;
    private String codeChallengeMethod;
}