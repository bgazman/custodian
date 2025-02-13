package consulting.gazman.security.idp.oauth.service;

import consulting.gazman.security.idp.auth.dto.LoginRequest;
import consulting.gazman.security.idp.auth.dto.LoginResponse;
import consulting.gazman.security.idp.oauth.dto.*;

public interface OAuthService {
    AuthorizeResponse generateAuthCode(AuthorizeRequest request);
    TokenResponse exchangeToken(TokenRequest request);
    TokenResponse refreshToken(TokenRequest request);

    IntrospectResponse introspectToken(String token);


    UserInfoResponse getUserInfo(String bearerToken);

    void revokeToken(String token);
}