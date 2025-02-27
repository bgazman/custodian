package consulting.gazman.security.idp.oauth.service.impl;

import consulting.gazman.security.user.entity.UserRole;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.model.OAuthSession;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.entity.Secret;
import consulting.gazman.security.idp.oauth.service.JwtService;
import consulting.gazman.security.user.service.impl.UserServiceImpl;
import consulting.gazman.security.idp.oauth.utils.JwtUtils;
import consulting.gazman.security.user.entity.GroupMembership;
import consulting.gazman.security.user.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;

import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private OAuthClientServiceImpl oAuthClientService;
    @Autowired
    private UserServiceImpl userService;
    // Inject the base URL from application.properties
    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public String generateAccessToken(User user, OAuthClient oAuthClient, List<GroupMembership> groups, Map<Long, List<String>> permissions,List<UserRole> roles) {
        // Validate expiration
        if (oAuthClient.getAccessTokenExpirySeconds() == null) {
            throw new IllegalArgumentException("Access token expiry seconds must not be null");
        }

        // Generate timestamps
        Instant now = Instant.now();
        long issuedAt = now.getEpochSecond();
        long expiration = now.plusSeconds(oAuthClient.getAccessTokenExpirySeconds()).getEpochSecond();

        Set<String> userRoles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName()) // Add "ROLE_" prefix to roles
                .collect(Collectors.toSet());

        List<String> userGroups = groups.stream()
                .map(group -> group.getGroup().getName())
                .collect(Collectors.toList());

// Permissions/Scopes - store without SCOPE_ prefix
        List<String> userPermissions = permissions.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Set<String> scopes = userPermissions.stream()
                .collect(Collectors.toSet());
        // Prepare claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getEmail());
        claims.put("roles", userRoles);
        claims.put("scope", String.join(" ", scopes));
        claims.put("iss", baseUrl); // Replace with production issuer
        claims.put("aud", oAuthClient.getClientId());
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("iat", issuedAt);
        claims.put("exp", expiration);


        // Generate and return the access token
        return generateToken(user.getEmail(), oAuthClient, claims, oAuthClient.getAccessTokenExpirySeconds());
    }



    @Override
    public String generateIdToken(User user, OAuthClient oAuthClient) {
        // Validate that access token expiry is set
        if (oAuthClient.getAccessTokenExpirySeconds() == null) {
            throw new IllegalArgumentException("Access token expiry seconds must not be null");
        }

        // Generate timestamps
        Instant now = Instant.now();
        long issuedAt = now.getEpochSecond();
        long expiration = now.plusSeconds(oAuthClient.getAccessTokenExpirySeconds()).getEpochSecond();

        // Prepare claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getEmail());  // Email as subject
        claims.put("email", user.getEmail());
        claims.put("auth_time", issuedAt);
        claims.put("iat", issuedAt);
        claims.put("exp", expiration);
        claims.put("iss", baseUrl);  // Update to your production issuer
        claims.put("aud", oAuthClient.getClientId());
        claims.put("jti", UUID.randomUUID().toString());

        // Generate and return the token
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

    @Override
    public String generateSessionToken(OAuthSession session) {
        // Retrieve the OAuthClient using the clientId from the session
        OAuthClient client = oAuthClientService.getClientByClientId(session.getClientId())
                .orElseThrow(() -> new AppException("INVALID_CLIENT", "Client not found"));

        // Prepare claims from the session data
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "session"); // Mark token as a session token
        claims.put("clientId", session.getClientId());
        claims.put("oauthSessionId", session.getOauthSessionId());
        claims.put("email", session.getEmail());
        claims.put("mfaInitiated", session.isMfaInitiated());
        claims.put("mfaAttempts", session.getMfaAttempts());
        claims.put("mfaInitiatedAt", session.getMfaInitiatedAt() != null ? session.getMfaInitiatedAt().toString() : null);
        claims.put("mfaMethod", session.getMfaMethod());
        claims.put("valid", session.isValid());
        claims.put("mfaExpired", session.isMfaExpired());

        // Set an expiration (for example, 10 minutes)
        int expirationSeconds = 600;
        return generateToken(session.getClientId(), client, claims, expirationSeconds);
    }

    @Override
    public OAuthSession parseSessionToken(String token) {
        if (token == null || token.isEmpty()) {
            throw AppException.invalidToken("Session token is null or empty");
        }        Claims claims = validateToken(token);
        OAuthSession session = new OAuthSession();
        session.setClientId(claims.get("clientId", String.class));
        session.setOauthSessionId(claims.get("oauthSessionId", String.class));
        session.setEmail(claims.get("email", String.class));
        session.setMfaInitiated(claims.get("mfaInitiated", Boolean.class));
        session.setMfaAttempts(claims.get("mfaAttempts", Integer.class));
        session.setMfaInitiatedAt(claims.get("mfaInitiatedAt", String.class) != null ? LocalDateTime.parse(claims.get("mfaInitiatedAt", String.class)) : null);
        session.setMfaMethod(claims.get("mfaMethod", String.class));
        session.setValid(claims.get("valid", Boolean.class));
        session.setMfaExpired(claims.get("mfaExpired", Boolean.class));
        return session;
    }
    @Override
    public void validateState(String expectedState, String actualState) {
        if (!Objects.equals(expectedState, actualState)) {
            throw new AppException("STATE_MISMATCH", "Invalid state parameter");
        }
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






