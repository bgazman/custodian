package consulting.gazman.security.idp.auth.service;

public interface EmailVerificationService {
    String generateVerificationToken(String email);

    void validateVerificationToken(String token);

    // Send the verification email
    void sendVerificationEmail(String email, String token);
}
