package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.*;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.JwtService;
import consulting.gazman.security.utils.JwtUtils;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private OAuthClientServiceImpl oAuthClientService;
    @Autowired
    private UserServiceImpl userService;


    @Override
    public String generateAccessToken(User user, String clientId, List<GroupMembership> groups, Map<Long, List<String>> permissions) {
        // Fetch the token configuration for the app
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));
//        Set<String> roles = user.getUserRoles().stream()
//                .map(userRole -> userRole.getRole().getName())
//                .collect(Collectors.toSet());
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getUserRoles().stream()  // Fix role extraction
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toSet()));
        List<Map<String, Object>> groupsList = groups.stream()
                .map(group -> {
                    Map<String, Object> groupMap = new HashMap<>();
                    groupMap.put("id", group.getGroup().getId().toString());
                    groupMap.put("name", group.getGroup().getName());
                    groupMap.put("role", group.getRole().toString());
                    groupMap.put("permissions", permissions.get(group.getGroup().getId()));
                    return groupMap;
                })
                .collect(Collectors.toList());

        claims.put("groups",groupsList);
        claims.put("sub", user.getEmail());
        claims.put("iss", "http://localhost:8080");
        claims.put("aud", clientId);
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("iat", Instant.now().getEpochSecond());
        claims.put("exp", Instant.now().plusSeconds(oAuthClient.getAccessTokenExpirySeconds()).getEpochSecond());
        claims.put("resource_access", Map.of(
                clientId, Map.of(
                        "roles", user.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getName())
                                .collect(Collectors.toSet()),
                        "permissions", permissions.values().stream()
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                )
        ));
        // Generate and return the access token
        return generateToken(user.getEmail(),oAuthClient, claims, oAuthClient.getAccessTokenExpirySeconds());
    }

    @Override
    public String generateRefreshToken(User user, String clientId) {
        // Fetch the token configuration for the app
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

        // Prepare claims specific to refresh tokens
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("client-id", clientId);
        claims.put("sub", user.getEmail());      // Add subject as string
        claims.put("type", "refresh_token");             // Add token type

        // Generate and return the refresh token
        return generateToken(user.getEmail(), oAuthClient, claims, oAuthClient.getRefreshTokenExpirySeconds());
    }



    @Override
    public String generateIdToken(User user, String clientId) {
        // Fetch the token configuration for the app
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getEmail());  // Email as subject
        claims.put("email", user.getEmail());
        claims.put("email_verified", user.isEmailVerified());
        claims.put("auth_time", Instant.now().getEpochSecond());
        claims.put("iat", Instant.now().getEpochSecond());
        claims.put("exp", Instant.now().plusSeconds(oAuthClient.getAccessTokenExpirySeconds()).getEpochSecond());
        claims.put("iss", "http://localhost:8080");
        claims.put("aud", clientId);
        claims.put("jti", UUID.randomUUID().toString());
        return generateToken(user.getEmail(), oAuthClient, claims, oAuthClient.getAccessTokenExpirySeconds());
    }

    private String generateToken(String subject, OAuthClient oAuthClient, Map<String, Object> claims, int expirationSeconds) {
        Key signingKey = getSigningKey(oAuthClient);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expirationSeconds)))
                .signWith(signingKey, SignatureAlgorithm.forName(oAuthClient.getAlgorithm()))
                .setHeaderParam("kid", oAuthClient.getSigningKey().getId())
                .compact();
    }



private Key getSigningKey(OAuthClient oAuthClient) {
    // Retrieve the list of secrets associated with the OAuth client
    Secret secret = oAuthClient.getSigningKey();

   return getPrivateKeyFromPem(secret.getPrivateKey());


            }
    private PrivateKey getPrivateKeyFromPem(String privateKeyPem) {
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

    // Helper method to generate RSA private key from PEM
    private PrivateKey generatePrivateKeyFromPem(String pemKey) {
        try {
            // Remove PEM headers and decode the Base64 content
            String keyContent = pemKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(keyContent);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error loading RSA private key", e);
        }
    }

    // Helper method to generate RSA public key from PEM
    private PublicKey generatePublicKeyFromPem(String pemKey) {
        try {
            // Remove PEM headers and decode the Base64 content
            String keyContent = pemKey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(keyContent);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error loading RSA public key", e);
        }
    }

    // Optional: Handle symmetric keys (if needed)
    private SecretKey generateSymmetricKeyFromPem(String pemKey) {
        try {
            // Remove PEM headers and decode the Base64 content
            String keyContent = pemKey
                    .replace("-----BEGIN SECRET KEY-----", "")
                    .replace("-----END SECRET KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(keyContent);

            return new SecretKeySpec(keyBytes, "HmacSHA256");
        } catch (Exception e) {
            throw new RuntimeException("Error loading symmetric key", e);
        }
    }
    @Override
    public User validateAccessToken(String token) {
        String result = validateToken(token);
        if (!"success".equals(result)) {
            throw AppException.invalidToken("Access token validation failed: " + result);
        }

        String email = JwtUtils.extractSubject(token);
        return userService.findByEmail(email)
                .orElseThrow(() -> AppException.userNotFound("Unable to find user for subject: " + email));
    }

    @Override
    public User validateRefreshToken(String token) {
        String result = validateToken(token);
        if (!"success".equals(result)) {
            throw AppException.invalidToken("Refresh token validation failed: " + result);
        }

        if (!"refresh_token".equals(JwtUtils.extractType(token))) {
            throw AppException.invalidToken("Invalid token type");
        }

        String email = JwtUtils.extractSubject(token);
        return userService.findByEmail(email)
                .orElseThrow(() -> AppException.userNotFound("Unable to find user for subject: " + email));    }

    @Override
    public String validateToken(String token) {
        try {
            String clientId = JwtUtils.extractClientId(token);


            OAuthClient config = oAuthClientService.getClientByClientId(clientId)
                    .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

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






