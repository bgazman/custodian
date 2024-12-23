package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.repository.TokenConfigurationRepository;
import consulting.gazman.security.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expirationMinutes * 60L)))
                .signWith(signingKey, SignatureAlgorithm.forName(config.getAlgorithm()))
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
    public boolean validateToken(String token, Key key, String algorithm) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Handle specific exceptions if needed
        }
    }


    @Override
    public Map<String, Object> parseHeader(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJwt(token)
                .getHeader();
    }
}
