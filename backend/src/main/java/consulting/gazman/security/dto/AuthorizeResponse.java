package consulting.gazman.security.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizeResponse {
    String code;
    String state;
    String error;
    String errorDescription;
}