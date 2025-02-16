package consulting.gazman.security.idp.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OAuthFlowData {
    private String state;
    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;
    private String codeChallenge;
    private String codeChallengeMethod;
    private Instant createdAt;
}