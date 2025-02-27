package consulting.gazman.security.idp.auth.service;

import consulting.gazman.security.idp.auth.dto.*;
import consulting.gazman.security.idp.oauth.model.OAuthSession;

public interface MfaService {
    /**
     * Initiates an MFA challenge for a user
     * @return MfaInitiationResult containing challenge details and status
     */
    MfaInitiationResult initiateMfaChallenge(OAuthSession session, String preferredMethod);

    /**
     * Generates a new MFA secret for setup
     * @return Generated secret and associated configuration
     */
    MfaSetupResult generateMfaSecret(OAuthSession session, String method);

    /**
     * Validates an MFA token/code
     * @return Validation result with details about success/failure
     */
    MfaValidationResult validateMfaToken(
            OAuthSession session,
            MfaRequest mfaRequest
    );

    /**
     * Generates TOTP secret for initial setup
     * @return TOTP configuration including QR code URI
     */
    TotpSetupResult generateTotpSecret(OAuthSession session);

    /**
     * Generates a random verification code for SMS/Email
     * @return Generated code and metadata
     */
    MfaCode generateRandomCode(String method);

    /**
     * Resends MFA code using the current method
     * @return Result indicating success/failure and any rate limiting info
     */
    MfaResendResult resendMfaCode(OAuthSession session, MfaRequest request);

    /**
     * Validates a backup code
     * @return Validation result including remaining backup codes
     */
    RecoveryCodeValidationResult validateRecoveryCode(
            OAuthSession session,
            MfaRequest request
    );
}

