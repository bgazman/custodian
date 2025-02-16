package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BackupCodeValidationResult {
    private boolean valid;
    private int remainingCodes;
    private LocalDateTime validatedAt;
    private String errorMessage;
}
