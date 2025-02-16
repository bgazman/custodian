package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecoveryCodeValidationResult {
    private boolean valid;
    private int remainingCodes;
    private LocalDateTime validatedAt;
    private String errorMessage;
}
