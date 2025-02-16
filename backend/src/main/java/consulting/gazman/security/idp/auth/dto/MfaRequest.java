
package consulting.gazman.security.idp.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfaRequest {
    @NotBlank(message = "Token cannot be empty when verifying MFA")
    private String token;

    @NotBlank(message = "State cannot be empty")
    private String state;

    @Pattern(regexp = "^(SMS|EMAIL|TOTP|BACKUP)$", message = "Invalid MFA method")
    private String method;

    @JsonProperty("isRecoveryCode")
    private Boolean backupCode;

    // Additional recommended fields
    private String deviceId;

    @JsonProperty("rememberDevice")
    private Boolean rememberDevice;

    private Map<String, String> metadata;

    // Validation method
    public void validate() {
        if (Boolean.TRUE.equals(backupCode) && token == null) {
            throw new IllegalArgumentException("Backup code must be provided");
        }

        if (method != null && !Arrays.asList("SMS", "EMAIL", "TOTP", "BACKUP")
                .contains(method.toUpperCase())) {
            throw new IllegalArgumentException("Invalid MFA method");
        }
    }
}