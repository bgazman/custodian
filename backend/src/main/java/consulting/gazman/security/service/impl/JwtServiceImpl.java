package consulting.gazman.security.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.ResourceNotFoundException;
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
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private TokenConfigurationRepository tokenConfigurationRepository;



    @Override
    public String generateAccessToken(User user, String appName) {
        // Fetch the token configuration for the app
        TokenConfiguration config = tokenConfigurationRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name: " + appName));

        // Generate the access token
        return generateToken(user, config, config.getAccessTokenExpirationMinutes());
    }

    @Override
    public String generateRefreshToken(User user, String appName) {
        // Fetch the token configuration for the app
        TokenConfiguration config = tokenConfigurationRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name: " + appName));

        // Generate the access token
        return generateToken(user, config, config.getAccessTokenExpirationMinutes());
    }
    @Override
    public String generateToken(User user, TokenConfiguration config, int expirationMinutes) {
        // Fetch the signing key based on the algorithm
        Key signingKey = getSigningKey(config);

        // Generate the token
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRole())
                .claim("appName", config.getTokenId().getAppName()) // Add appName as a claim
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expirationMinutes * 60L)))
                .signWith(signingKey, SignatureAlgorithm.forName(config.getAlgorithm()))
                .setHeaderParam("kid", config.getTokenId().getKeyId()) // Include key ID for JWKS lookup
                .compact();
    }





    public String extractSubject(String token, String appName) {
        // Fetch the app configuration to get the signing key
        TokenConfiguration config = tokenConfigurationRepository.findByTokenIdAppName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid app name: " + appName));

        try {
            // Parse the token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(config)) // Use the correct signing key
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extract and return the subject
            return claims.getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
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
                    .orElseThrow(() -> new ResourceNotFoundException("App configuration not found for: " + appName));
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






