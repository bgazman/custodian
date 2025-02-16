package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MfaResendResult {
    private boolean success;
    private LocalDateTime nextAllowedAttempt;
    private int remainingResendAttempts;
    private String errorMessage;
}
