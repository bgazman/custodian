package consulting.gazman.security.idp.auth.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
public class MfaCode {
    String code;
    LocalDateTime generatedAt;
    LocalDateTime expiresAt;
    String method;
    Map<String, Object> metadata;
}
