package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.*;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.JwtService;
import consulting.gazman.security.utils.JwtUtils;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
    private OAuthClientServiceImpl oAuthClientService;
    @Autowired
    private UserServiceImpl userService;
    private static final String JWKS_URL = "https://idp.example.com/.well-known/jwks.json";


    @Override
    public String generateAccessToken(User user, OAuthClient oAuthClient, List<GroupMembership> groups, Map<Long, List<String>> permissions) {


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
        claims.put("aud", oAuthClient.getClientId());
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("iat", Instant.now().getEpochSecond());
        claims.put("exp", Instant.now().plusSeconds(oAuthClient.getAccessTokenExpirySeconds()).getEpochSecond());
        claims.put("resource_access", Map.of(
                oAuthClient.getClientId(), Map.of(
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
    public String generateIdToken(User user, OAuthClient oAuthClient) {
        // Fetch the token configuration for the app


        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getEmail());  // Email as subject
        claims.put("email", user.getEmail());
        claims.put("email_verified", user.isEmailVerified());
        claims.put("auth_time", Instant.now().getEpochSecond());
        claims.put("iat", Instant.now().getEpochSecond());
        claims.put("exp", Instant.now().plusSeconds(oAuthClient.getAccessTokenExpirySeconds()).getEpochSecond());
        claims.put("iss", "http://localhost:8080");
        claims.put("aud", oAuthClient.getClientId());
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
                .setHeaderParam("kid", String.valueOf(oAuthClient.getSigningKey().getId()))
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








    @Override
    public Claims validateToken (String token) {
        try {
            String clientId = JwtUtils.extractClientId(token);

            OAuthClient config = oAuthClientService.getClientByClientId(clientId)
                    .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

            Key signingKey = getSigningKey(config);

            // Parse and validate the token, returning the claims if successful
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey) // Use app-specific key
                    .build()
                    .parseClaimsJws(token) // Parses the token and throws an exception if invalid
                    .getBody(); // Returns the claims
            // If successful, return a success response
        } catch (ExpiredJwtException ex) {
            throw AppException.invalidToken("Token expired");
        } catch (MalformedJwtException ex) {
            throw AppException.invalidToken("Malformed token");
        } catch (SignatureException ex) {
            throw AppException.invalidToken("Invalid signature");
        } catch (Exception ex) {
            throw AppException.invalidToken("Token validation error: " + ex.getMessage());
        }


    }

}






