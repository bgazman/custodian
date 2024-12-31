package consulting.gazman.security.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IntrospectResponse {
    private boolean active;
    private String scope;
    private String clientId;
    private String username;
    private String tokenType;
    private long exp;
    private long iat;
    private long nbf;
    private String sub;
    private List<String> aud;
    private String iss;
    private String jti;
}
