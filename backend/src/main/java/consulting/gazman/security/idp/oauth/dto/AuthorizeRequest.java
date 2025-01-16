package consulting.gazman.security.idp.oauth.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeRequest {
    String responseType;  // "code"
    String clientId;
    String redirectUri;
    String scope;
    String state;
    String email;
    String password;
}