package consulting.gazman.security.idp.auth.service;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface EmailVerificationService {
    String generateVerificationToken(String email);

    void validateVerificationToken(String token);

    // Send the verification email
    void sendVerificationEmail(String email, String token);

    @Async
    CompletableFuture<Void> sendVerificationEmailAsync(String email, String token);
}
