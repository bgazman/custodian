package consulting.gazman.security.idp.auth.service;

public interface MfaService {
    void initiateMfaChallenge(String clientId,String email);


    String generateMfaSecret(String email);


    boolean validateMfaToken(String email, String token, String method);

    String generateTotpSecret(String email);

    String generateRandomCode();



    boolean resendMfaCode(String email, String clientId);

    boolean validateBackupCode(String email, String token);
}
