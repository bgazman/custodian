package consulting.gazman.security.service;

import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.Token;
import consulting.gazman.security.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenService {

    Token createToken(String tokenType, String tokenValue, LocalDateTime expiresAt, User user, OAuthClient client);

    Optional<Token> findToken(String tokenValue);

    void revokeToken(Long tokenId);

    void revokeAllTokensForUserAndClient(Long userId, Long clientId);

    boolean isTokenValid(String tokenValue);

    void rotateRefreshToken(Token oldToken, String newTokenValue, LocalDateTime newExpiresAt);
}
