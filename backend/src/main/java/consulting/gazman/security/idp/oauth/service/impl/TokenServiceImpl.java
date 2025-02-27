package consulting.gazman.security.idp.oauth.service.impl;

import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.entity.Token;
import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.idp.oauth.repository.TokenRepository;
import consulting.gazman.security.idp.oauth.service.TokenService;
import consulting.gazman.security.idp.oauth.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final TokenUtils tokenUtils = new TokenUtils();

    @Override
    public Token createToken(String tokenType, String tokenValue, LocalDateTime expiresAt, User user, OAuthClient client) {
        String hashedToken = tokenUtils.hashToken(tokenValue); // Hash the token before saving
        Token token = Token.builder()
                .tokenType(tokenType)
                .token(hashedToken)
                .expiresAt(expiresAt)
                .issuedAt(LocalDateTime.now())
                .user(user)
                .client(client)
                .build();
        return tokenRepository.save(token);
    }

    @Override
    public Optional<Token> findToken(String tokenValue) {
        String hashedToken = tokenUtils.hashToken(tokenValue);
        return tokenRepository.findByToken(hashedToken);
    }

    @Override
    @Transactional
    public void revokeToken(Long tokenId) {
        tokenRepository.revokeToken(tokenId);
    }

    @Override
    @Transactional
    public void revokeAllTokensForUserAndClient(Long userId, Long clientId) {
        tokenRepository.revokeAllTokensForUserAndClient(userId, clientId);
    }

    @Override
    public boolean isTokenValid(String tokenValue) {
        Optional<Token> token = findToken(tokenValue);
        return token.isPresent() && !tokenUtils.isTokenExpired(token.get().getExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    @Transactional
    public void rotateRefreshToken(Token oldToken, String newTokenValue, LocalDateTime newExpiresAt) {
        String hashedNewToken = tokenUtils.hashToken(newTokenValue);

        Token newToken = Token.builder()
                .tokenType(oldToken.getTokenType())
                .token(hashedNewToken)
                .expiresAt(newExpiresAt)
                .issuedAt(LocalDateTime.now())
                .user(oldToken.getUser())
                .client(oldToken.getClient())
                .build();

        tokenRepository.save(newToken);

        oldToken.setRevokedAt(LocalDateTime.now());
        oldToken.setRotatedTo(newToken);
        tokenRepository.save(oldToken);
    }
}
