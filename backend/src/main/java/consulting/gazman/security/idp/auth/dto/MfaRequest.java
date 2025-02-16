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
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfaRequest {
    @NotBlank(message = "Token cannot be empty when verifying MFA")
    private Optional<String> token = Optional.empty();

    @NotBlank(message = "State cannot be empty")
    private Optional<String> state = Optional.empty();
    @Pattern(regexp = "^(SMS|EMAIL|TOTP|BACKUP)$", message = "Invalid MFA method")
    private Optional<String> method = Optional.empty();

    @JsonProperty("isBackupCode")
    private Optional<Boolean> backupCode = Optional.empty();

    // Additional recommended fields
    @Builder.Default
    private Optional<String> deviceId = Optional.empty();

    @Builder.Default
    @JsonProperty("rememberDevice")
    private Optional<Boolean> rememberDevice = Optional.empty();

    @Builder.Default
    private Optional<Map<String, String>> metadata = Optional.empty();

    // Validation method
    public void validate() {
        if (backupCode.orElse(false) && token.isEmpty()) {
            throw new IllegalArgumentException("Backup code must be provided");
        }

        if (method.isPresent() && !Arrays.asList("SMS", "EMAIL", "TOTP", "BACKUP")
                .contains(method.get().toUpperCase())) {
            throw new IllegalArgumentException("Invalid MFA method");
        }
    }
}