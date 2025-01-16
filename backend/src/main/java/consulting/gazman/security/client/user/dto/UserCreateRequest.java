package consulting.gazman.security.client.user.dto;

import lombok.*;

import java.util.Map;
import java.util.Set;

@Data
public class UserCreateRequest {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Set<Long> roleIds;
    private Set<Long> groupIds;
    private Map<String, String> attributes;
}
