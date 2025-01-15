package consulting.gazman.security.auth.service;

public interface MfaService {
    void initiateMfaChallenge(String email, String method);

    String generateMfaSecret(String email);


    boolean validateMfaToken(String email, String token, String method);

    String generateTotpSecret(String email);

    String generateRandomCode();

    boolean resendMfaCode(String email);

    boolean validateBackupCode(String email, String token);
}
