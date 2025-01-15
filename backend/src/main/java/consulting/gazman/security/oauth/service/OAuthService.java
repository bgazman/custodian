package consulting.gazman.security.oauth.service;

import consulting.gazman.security.oauth.dto.*;

public interface OAuthService {
    LoginResponse login(LoginRequest loginRequest);  // Add this
    AuthorizeResponse generateAuthCode(AuthorizeRequest request);
    TokenResponse exchangeToken(TokenRequest request);
    TokenResponse refreshToken(TokenRequest request);

    IntrospectResponse introspectToken(String token);


    UserInfoResponse getUserInfo(String bearerToken);

    void revokeToken(String token);
}