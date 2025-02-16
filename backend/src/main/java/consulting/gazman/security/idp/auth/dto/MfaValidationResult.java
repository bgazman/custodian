package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MfaValidationResult {
    private boolean valid;
    private String errorCode;
    private String errorMessage;
    private int remainingAttempts;
    private boolean requiresReinitialization;
}
