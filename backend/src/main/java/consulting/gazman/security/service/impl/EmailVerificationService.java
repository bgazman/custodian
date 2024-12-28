package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.AuthService;
import consulting.gazman.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    @Autowired
    EmailService emailService;
    @Autowired
    UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long TOKEN_EXPIRY = 24 * 60 * 60; // 24 hours in seconds

    public String generateVerificationToken(String email) {
        // Generate a secure random token
        String token = UUID.randomUUID().toString();

        // Store the token in Redis with an expiry
        redisTemplate.opsForValue().set(
                token,
                email, // Map token to email
                TOKEN_EXPIRY,
                TimeUnit.SECONDS
        );

        return token;
    }

    public void validateVerificationToken(String token) {
        // Retrieve the email associated with the token
        String email = redisTemplate.opsForValue().get(token);

        if (email == null) {
            throw AppException.badRequest("Verification token is invalid or expired");
        }

        // Find the user by email
        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.resourceNotFound("User not found for email: " + email));

        // Mark the user's email as verified
        userService.verifyEmail(user.getId());
        // Delete the token to ensure it's single-use
        redisTemplate.delete(token);


    }

    // Send the verification email
    public void sendVerificationEmail(String email, String token) {
        String verificationLink = "https://example.com/api/auth/verify-email?token=" + token;
        String subject = "Verify Your Email";
        String message = "Click the link below to verify your email:\n\n" + verificationLink;

        emailService.sendEmail(email, subject, message);
    }
}
