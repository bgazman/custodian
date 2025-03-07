package consulting.gazman.security.idp.auth.service;

public interface PasswordResetService {
    void initiatePasswordReset(String email);

    boolean validateResetToken(String email, String token);

    void resetPassword(String email, String newPassword);
}
