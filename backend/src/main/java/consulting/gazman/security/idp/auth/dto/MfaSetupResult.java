package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MfaSetupResult {
    private String secret;
    private String method;
    private Map<String, Object> configuration;
    private List<String> backupCodes;
    private boolean success;
    private String errorMessage;
}
