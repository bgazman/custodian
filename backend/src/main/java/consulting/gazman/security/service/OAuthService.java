package consulting.gazman.security.service;

import consulting.gazman.security.dto.*;
import jakarta.transaction.Transactional;

public interface OAuthService {
    LoginResponse login(LoginRequest loginRequest);  // Add this
    AuthorizeResponse generateAuthCode(AuthorizeRequest request);
    TokenResponse exchangeToken(TokenRequest request);
    TokenResponse refreshToken(TokenRequest request);

    @Transactional
    IntrospectResponse introspectToken(String token);

    UserInfoResponse getUserInfo(String bearerToken);
}