package consulting.gazman.security.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UserDetailsDTO {
    private UserProfileDTO profile;
    private UserStatusDTO status;
    private UserSecurityDTO security;
    private UserAccessDTO access;

    // Additional metadata
    private LocalDateTime lastPasswordChange;
}