package consulting.gazman.security.dto;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken; // The refresh token provided during login
}
