package consulting.gazman.security.idp.auth.service.impl;

import consulting.gazman.security.idp.auth.service.EmailVerificationService;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.user.service.UserService;
import consulting.gazman.security.common.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Autowired
    EmailService emailService;
    @Autowired
    UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long TOKEN_EXPIRY = 24 * 60 * 60; // 24 hours in seconds
    // Inject the base URL from application.properties
    @Value("${app.base-url}")
    private String baseUrl;
    private  final String VERIFY_EMAIL_URL = baseUrl + "/api/auth/verify-email?token=";
    @Override
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

    @Override
    public void validateVerificationToken(String token) {
        // Retrieve the email associated with the token
        String email = redisTemplate.opsForValue().get(token);

        if (email == null) {
            throw AppException.badRequest("Verification token is invalid or expired");
        }

        // Find the user by email
        User user = userService.findByEmail(email);

        // Mark the user's email as verified
        userService.verifyEmail(user.getId());
        // Delete the token to ensure it's single-use
        redisTemplate.delete(token);


    }

    // Send the verification email
    @Override
    public void sendVerificationEmail(String email, String token) {
        String verificationLink = VERIFY_EMAIL_URL + token;
        String subject = "Verify Your Email";
        String message = "Click the link below to verify your email:\n\n" + verificationLink;

        emailService.sendEmail(email, subject, message);
    }

@Async
@Override
public CompletableFuture<Void> sendVerificationEmailAsync(String email, String token) {
        try {
            sendVerificationEmail(email, token);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
