package consulting.gazman.security.service;

import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Map;

public interface JwtService {

    String generateAccessToken(User user, String appName);
    String generateRefreshToken(User user, String appName);

    String generateToken(User user, TokenConfiguration config, int expirationMinutes);

    boolean validateToken(String token, Key key, String algorithm);

    Map<String, Object> parseHeader(String token);
}
