package consulting.gazman.security.utils;

import consulting.gazman.security.entity.Secret;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.TokenId;
import consulting.gazman.security.entity.User;

import consulting.gazman.security.repository.TokenConfigurationRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;


import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.util.function.Function;

@Component
public class JwtUtils {

    private final TokenConfigurationRepository tokenConfigRepository;

    public JwtUtils(TokenConfigurationRepository tokenConfigRepository) {
        this.tokenConfigRepository = tokenConfigRepository;
    }

    public String generateAccessToken(User user, String appName) {
        return generateToken(user, appName, true); // true for access token
    }

    public String generateRefreshToken(User user, String appName) {
        return generateToken(user, appName, false); // false for refresh token
    }

    private Key getKey(String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
//    private Key generateKey(String secret, SignatureAlgorithm algorithm) {
//        switch (algorithm) {
//            case HS256:
//            case HS384:
//            case HS512:
//                return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//            case RS256:
//            case RS384:
//            case RS512:
//                return ""; // Example for RSA
//            default:
//                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
//        }
//    }


    private String generateToken(User user, String appName, boolean isAccessToken) {
        // Fetch token configuration from the database
        TokenConfiguration config = tokenConfigRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name"));

        // Retrieve the actual secret from the `Secret` entity
        String secret = getSigningKey(config);

        // Calculate token expiration based on the type
        long expirationSeconds = (isAccessToken
                ? config.getAccessTokenExpirationMinutes()
                : config.getRefreshTokenExpirationMinutes()) * 60L;

        // Build and return the JWT
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRole())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(expirationSeconds)))
                .signWith(getKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }
    private String getSigningKey(TokenConfiguration config) {
        // Fetch the related Secret entity
        Secret secretEntity = config.getSecretKey();

        // Ensure the secret value exists
        if (secretEntity == null || secretEntity.getValue() == null || secretEntity.getValue().isBlank()) {
            throw new IllegalArgumentException("Missing or invalid secret for app: " + config.getTokenId().getAppName());
        }

        return secretEntity.getValue();
    }

    public boolean validateAccessToken(String token, String appName) {
        return validateToken(token, appName, true); // true for access token
    }

    public boolean validateRefreshToken(String token, String appName) {
        return validateToken(token, appName, false); // false for refresh token
    }

    private boolean validateToken(String token, String appName, boolean isAccessToken) {
        try {
            // Fetch the correct secret for the token
            TokenConfiguration config = tokenConfigRepository.findByTokenIdAppName(appName)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid app name"));

            String secret = getSigningKey(config);

            Jwts.parserBuilder()
                    .setSigningKey(getKey(secret))
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            // Token has expired
            return false;
        } catch (JwtException e) {
            // Other JWT validation errors (malformed, invalid signature, etc)
            return false;
        }
    }

    public String extractSubject(String token, String appName) {
        return extractClaim(token, appName, Claims::getSubject);
    }

    public String extractEmail(String token, String appName) {
        return extractClaim(token, appName, claims -> claims.get("email", String.class));
    }

    public String extractRoles(String token, String appName) {
        return extractClaim(token, appName, claims -> claims.get("roles", String.class));
    }

    private <T> T extractClaim(String token, String appName, Function<Claims, T> claimsResolver) {
        TokenConfiguration config = tokenConfigRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name"));

        String secret = getSigningKey(config);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }
}

