package consulting.gazman.security.service;

import consulting.gazman.security.dto.*;

public interface OAuthService {
    LoginResponse login(LoginRequest loginRequest);  // Add this
    AuthorizeResponse generateAuthCode(AuthorizeRequest request);
    TokenResponse exchangeToken(TokenRequest request);
    TokenResponse refreshToken(TokenRequest request);
    UserInfoResponse getUserInfo(String bearerToken);
}