package consulting.gazman.security.idp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String oauthSessionId;
    private String clientId;
    private String email;

    // Enhanced MFA fields
    private boolean mfaInitiated;
    @Builder.Default
    private int mfaAttempts = 0;          // Track verification attempts
    private LocalDateTime mfaInitiatedAt;  // Track when MFA was initiated
    private String mfaMethod;              // Track which MFA method is being used
    private boolean valid;                 // Add this field
    private boolean mfaExpired;            // Add this field

    public boolean isValid() {
        // Implement your validation logic here
        return !isMfaExpired() && canAttemptMfa();
    }

    // Utility methods for MFA
    public boolean isMfaExpired() {
        return mfaInitiatedAt != null &&
                mfaInitiatedAt.plusMinutes(5).isBefore(LocalDateTime.now());
    }

    public boolean canAttemptMfa() {
        return mfaAttempts < 3;  // Limit attempts
    }

    public void incrementMfaAttempts() {
        this.mfaAttempts++;
    }

    public void initiateMfa(String method) {
        this.mfaInitiated = true;
        this.mfaInitiatedAt = LocalDateTime.now();
        this.mfaMethod = method;
        this.mfaAttempts = 0;
    }

    public void resetMfa() {
        this.mfaInitiated = false;
        this.mfaInitiatedAt = null;
        this.mfaMethod = null;
        this.mfaAttempts = 0;
    }
}