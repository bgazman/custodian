package consulting.gazman.security.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaRequest {
    private String email;
    private String token;
    private String method;
    private String clientId;
    private String redirectUri;
    private String state;
    private String responseType;
    private String scope;
    @JsonProperty("isBackupCode")  // This maps isBackupCode from JSON to backupCode in Java
    private boolean backupCode;}