package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.TokenConfigurationRepository;
import consulting.gazman.security.service.JwtService;
import consulting.gazman.security.utils.JwtUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private TokenConfigurationRepository tokenConfigurationRepository;



    @Override
    public String generateAccessToken(User user, String appName, List<GroupMembership> groups, Map<Long, List<String>> permissions) {
        // Fetch the token configuration for the app
        TokenConfiguration config = tokenConfigurationRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name: " + appName));

        // Prepare the claims specific to access tokens
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRole());
        claims.put("groups", groups.stream().map(group -> Map.of(
                "id", group.getGroup().getId(),
                "name", group.getGroup().getName(),
                "role", group.getRole(),
                "permissions", permissions.get(group.getGroup().getId())
        )).collect(Collectors.toList()));
        claims.put("appName", appName);

        // Generate and return the access token
        return generateToken(user, config, claims, config.getAccessTokenExpirationMinutes());
    }

    @Override
    public String generateRefreshToken(User user, String appName) {
        // Fetch the token configuration for the app
        TokenConfiguration config = tokenConfigurationRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name: " + appName));

        // Prepare claims specific to refresh tokens
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("appName", appName);

        // Generate and return the refresh token
        return generateToken(user, config, claims, config.getRefreshTokenExpirationMinutes());
    }

    private String generateToken(User user, TokenConfiguration config, Map<String, Object> claims, int expirationMinutes) {
        // Fetch the signing key based on the algorithm
        Key signingKey = getSigningKey(config);

        // Generate the token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail()) // Use email as the subject
                .setIssuedAt(new Date()) // Issue time
                .setExpiration(Date.from(Instant.now().plusSeconds(expirationMinutes * 60L))) // Expiry
                .signWith(signingKey, SignatureAlgorithm.forName(config.getAlgorithm())) // Sign with key and algorithm
                .setHeaderParam("kid", config.getTokenId().getKeyId()) // Include key ID for JWKS lookup
                .compact();
    }






    private Key getSigningKey(TokenConfiguration config) {
        String algorithm = config.getAlgorithm();

        switch (algorithm) {
            case "HS256":
            case "HS384":
            case "HS512":
                // Use symmetric key (secret)
                String secret = config.getSecretKey().getValue(); // From the `Secret` entity
                return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            case "RS256":
            case "RS384":
            case "RS512":
                // Use asymmetric private key
                return getPrivateKey(config.getPrivateKey().getValue());
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }
    private PrivateKey getPrivateKey(String privateKeyPem) {
        try {
            // Remove PEM headers and decode Base64
            String privateKeyBase64 = privateKeyPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);

            // Generate PrivateKey object
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse private key", e);
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            String appName = JwtUtils.extractAppName(token);


            TokenConfiguration config = tokenConfigurationRepository.findByTokenIdAppName(appName)
                    .orElseThrow(() -> AppException.resourceNotFound("App configuration not found for: " + appName));
            Key signingKey = getSigningKey(config);


            // Fetch configuration for the app

            // Validate the token
            Jwts.parserBuilder()
                    .setSigningKey(signingKey) // Use app-specific key
                    .build()
                    .parseClaimsJws(token); // Throws exception if invalid

            // If successful, return a success response
            return "success";
        } catch (ExpiredJwtException ex) {
            return "token_expired";
        } catch (MalformedJwtException ex) {
            return "malformed_token";
        } catch (SignatureException ex) {
            return "invalid_signature";
        }
        catch (Exception ex){
            return "token_validation_error";
        }


    }

    @Override
    public Map<String, Object> parseHeader(String token) {
        return Map.of();
    }
}






