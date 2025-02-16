package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

// Supporting DTOs:
@Data
@Builder
public class MfaInitiationResult {
    private String challengeId;
    private String method;
    private LocalDateTime expiresAt;
    private Map<String, Object> additionalData;
    private boolean success;
    private String errorMessage;
}
