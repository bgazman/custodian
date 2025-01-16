package consulting.gazman.security.idp.oauth.repository;

import consulting.gazman.security.idp.oauth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // Find a token by its hashed value
    @Query("SELECT t FROM Token t WHERE t.token = :hashedToken AND t.revokedAt IS NULL")
    Optional<Token> findByToken(String hashedToken);

    // Find active tokens for a specific user and client
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.client.id = :clientId AND t.revokedAt IS NULL AND t.expiresAt > CURRENT_TIMESTAMP")
    Optional<Token> findActiveToken(Long userId, Long clientId);

    // Revoke a token (soft delete by setting revokedAt)
    @Query("UPDATE Token t SET t.revokedAt = CURRENT_TIMESTAMP WHERE t.id = :tokenId")
    void revokeToken(Long tokenId);

    // Revoke all tokens for a user and client (e.g., logout or rotation)
    @Query("UPDATE Token t SET t.revokedAt = CURRENT_TIMESTAMP WHERE t.user.id = :userId AND t.client.id = :clientId")
    void revokeAllTokensForUserAndClient(Long userId, Long clientId);
}
