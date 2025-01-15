package consulting.gazman.security.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ResetPasswordRequest {
    private String email;
    private String token;
    private String newPassword;

    // Getters and Setters
}

