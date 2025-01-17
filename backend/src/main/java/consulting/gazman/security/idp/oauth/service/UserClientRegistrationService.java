package consulting.gazman.security.idp.oauth.service;

import consulting.gazman.security.idp.auth.dto.UserRegistrationResponse;
import consulting.gazman.security.idp.oauth.dto.*;

import java.util.List;

public interface UserClientRegistrationService {
    // Registration and linking
    UserClientRegistrationResponse register(UserClientRegistrationRequest request);
    UserClientRegistrationResponse linkExistingUser(Long userId, String clientId);

    // Verification and status
    void verifyEmail(String clientId, String email, String token);
    void updateMfaSettings(Long userId, String clientId, MfaUpdateRequest request);
    UserClientStatusResponse getRegistrationStatus(Long userId, String clientId);

    // Consent management
    void grantConsent(Long userId, String clientId, List<String> scopes);
    void revokeConsent(Long userId, String clientId);
    ConsentResponse getConsent(Long userId, String clientId);

    // Client management
    List<ClientRegistrationResponse> getUserClients(Long userId);
    List<UserRegistrationResponse> getClientUsers(String clientId);

    // Updates and deletion
    void updateRegistration(Long userId, String clientId, UpdateRegistrationRequest request);
    void deleteRegistration(Long userId, String clientId);

    // Utility methods
    boolean hasUserAuthorizedClient(Long userId, String clientId);
    boolean isEmailVerifiedForClient(Long userId, String clientId);
    boolean isMfaEnabledForClient(Long userId, String clientId);
}









