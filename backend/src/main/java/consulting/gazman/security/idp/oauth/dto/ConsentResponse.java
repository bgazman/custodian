package consulting.gazman.security.idp.oauth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ConsentResponse {
    private List<String> grantedScopes;
    private LocalDateTime consentGrantedAt;
    private LocalDateTime lastUpdatedAt;
}