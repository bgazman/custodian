package consulting.gazman.security.utils;

import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.User;

import consulting.gazman.security.repository.TokenConfigurationRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

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

    private String generateToken(User user, String appName, boolean isAccessToken) {
        // Fetch token configuration from the database
        TokenConfiguration config = tokenConfigRepository.findByAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name"));

        String secret = config.getSecretKey();
        Long expiration = isAccessToken
                ? config.getAccessTokenExpirationMinutes() * 60L
                : config.getRefreshTokenExpirationMinutes() * 60L;

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(getKey(secret), SignatureAlgorithm.HS256)
                .compact();
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
            TokenConfiguration config = tokenConfigRepository.findByAppName(appName)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid app name"));

            String secret = config.getSecretKey();

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
        TokenConfiguration config = tokenConfigRepository.findByAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name"));

        String secret = config.getSecretKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }
}

