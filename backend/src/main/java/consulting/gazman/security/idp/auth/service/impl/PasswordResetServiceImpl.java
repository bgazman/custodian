package consulting.gazman.security.idp.auth.service.impl;

import consulting.gazman.security.client.user.entity.User;
import consulting.gazman.security.common.service.NotificationService;
import consulting.gazman.security.idp.auth.service.PasswordResetService;
import consulting.gazman.security.client.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private NotificationService notificationService; // Handles email delivery

    private static final long TOKEN_EXPIRY = 300; // 5 minutes
    // Inject the base URL from application.properties
    @Value("${app.base-url}")
    private String baseUrl;
    private  final String RESET_PASSWORD_URL = baseUrl + "/forgot-password";
    @Override
    public void initiatePasswordReset(String email) {


        // Check if the email exists without exposing errors
        Optional<User> userOptional = userService.findByEmailOptional(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Generate a unique token
            String resetToken = UUID.randomUUID().toString();

            // Store the token in Redis with a short expiration
            redisTemplate.opsForValue().set("reset:token:" + email, resetToken, TOKEN_EXPIRY, TimeUnit.SECONDS);

            // Send reset link via email
            String resetLink = RESET_PASSWORD_URL + "?email=" + email + "&token=" + resetToken;
            notificationService.sendEmail(
                    email,
                    "Password Reset Request",
                    "Click the link to reset your password: " + resetLink
            );

            log.info("Password reset link sent to {}", email);
        }else{
            log.info("User {} doesn't exist, password reset link not sent!", email);

        }

        // Log and return success, even if email is not registered
        log.info("Password reset process initiated for {}", email);
    }


    @Override
    public boolean validateResetToken(String email, String token) {
        // Retrieve the token from Redis
        String storedToken = redisTemplate.opsForValue().get("reset:token:" + email);

        if (storedToken == null) {
            log.error("No reset token found for email {}", email);
            return false;
        }

        if (!storedToken.equals(token)) {
            log.error("Invalid token for email {}", email);
            return false;
        }

        return true;
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        // Validate user
        User user = userService.findByEmailOptional(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not registered"));

        // Update the password (hashed)
        String hashedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(hashedPassword);
        userService.save(user);

        // Remove the token from Redis after successful password reset
        redisTemplate.delete("reset:token:" + email);

        log.info("Password successfully reset for email {}", email);
    }
}
