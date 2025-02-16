package consulting.gazman.security.idp.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthFlowData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;
    private String codeChallenge;
    private String codeChallengeMethod;
    private Instant createdAt;
}