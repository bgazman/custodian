package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TotpSetupResult {
    private String secret;
    private String qrCodeUri;
    private List<String> backupCodes;
    private Map<String, Object> configuration;
}
