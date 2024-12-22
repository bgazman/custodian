package consulting.gazman.security.dto;

import consulting.gazman.security.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class UserRequest {
    private String email; // User's email
    private String password; // User's password
    private String role; // Role of the user (e.g., USER, ADMIN)

    // Convert UserRequest to User entity
//    public User toEntity() {
//        return User.builder()
//                .email(this.email)
//                .password(this.password) // Hash the password in the service layer
//                .role(this.role)
//                .enabled(true) // Default values
//                .emailVerified(false) // Default values
//                .accountNonExpired(true) // Default values
//                .accountNonLocked(true) // Default values
//                .credentialsNonExpired(true) // Default values
//                .build();
//    }
}

