package consulting.gazman.security.oauth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

import java.util.Base64;



import java.util.Map;

@Component
public class JwtUtils {





    private Key getKey(String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public static String extractClientId(String token) {
        return (String) parseTokenPayload(token).get("aud");
    }

    public static String extractSubject(String token) {
        return (String) parseTokenPayload(token).get("sub");
    }

    public static String extractType(String token) {return (String) parseTokenPayload(token).get("type");}

    public static Map<String, Object> parseTokenPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return new ObjectMapper().readValue(payload, Map.class);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse token payload", e);
        }
    }

}

