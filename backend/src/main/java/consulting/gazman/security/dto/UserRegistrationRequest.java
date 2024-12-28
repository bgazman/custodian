package consulting.gazman.security.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class UserRegistrationRequest {
    private String name;         // User's full name
    private String email;        // User's email address
    private String password;     // User's password
    private String clientId;     // User's password

}
