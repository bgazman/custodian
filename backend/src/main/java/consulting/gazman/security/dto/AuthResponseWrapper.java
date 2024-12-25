package consulting.gazman.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AuthResponseWrapper {
    private String result;
    private String message;
    private AuthResponse authResponse;

    public static AuthResponseWrapper success(AuthResponse response) {
        return new AuthResponseWrapper("success", null, response);
    }

    public static AuthResponseWrapper message(String result,String message) {
        return new AuthResponseWrapper(result, message, null);
    }

}