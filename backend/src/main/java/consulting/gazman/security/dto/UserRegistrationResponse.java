package consulting.gazman.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationResponse {
    private UserDetails user;   // Minimal user details
    private Tokens tokens;      // Access and refresh tokens

    @Data
    @Builder
    public static class UserDetails {
        private Long id;        // User ID
        private String name;    // User's full name
        private String email;   // User's email address
    }

    @Data
    @Builder
    public static class Tokens {
        private String accessToken;   // Access token
        private String refreshToken;  // Refresh token
    }
}
