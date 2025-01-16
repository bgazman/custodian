package consulting.gazman.security.idp.oauth.utils;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public class TokenUtils {

    /**
     * Generate a random opaque token.
     *
     * @return A unique opaque token string.
     */
    public static String generateOpaqueToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Hash a token for secure storage or comparison.
     *
     * @param token The raw token to hash.
     * @return The hashed token (Base64-encoded SHA-256).
     */
    public static String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    /**
     * Validate if a token is expired.
     *
     * @param expiresAt The expiration time of the token.
     * @return `true` if the token is still valid; `false` otherwise.
     */
    public boolean isTokenExpired(Instant expiresAt) {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Generate a new expiration time for a token.
     *
     * @param lifetimeSeconds The lifetime of the token in seconds.
     * @return The expiration time as an `Instant`.
     */
    public Instant generateExpirationTime(long lifetimeSeconds) {
        return Instant.now().plusSeconds(lifetimeSeconds);
    }
}
