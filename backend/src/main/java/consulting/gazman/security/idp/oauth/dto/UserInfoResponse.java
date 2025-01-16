package consulting.gazman.security.idp.oauth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class UserInfoResponse {
    String sub;
    String name;
    String email;
    Boolean emailVerified;
}