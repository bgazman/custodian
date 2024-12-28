package consulting.gazman.security.service.impl;

import consulting.gazman.security.dto.AuthorizeRequest;
import consulting.gazman.security.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import consulting.gazman.security.exception.AppException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthCodeService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    OAuthClientServiceImpl oAuthClientService;
    private static final long CODE_EXPIRY = 600; // 10 minutes

    public String generateSecureCode() {
        return UUID.randomUUID().toString();
    }
    public String generateCode(String userId, String clientId) {
        String code = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                code,
                userId + ":" + clientId,
                10, // 10 minute expiry
                TimeUnit.MINUTES
        );
        return code;
    }
    public void storeCode(String code, String userId, String clientId) {
        String value = userId + ":" + clientId;
        redisTemplate.opsForValue().set(code, value, CODE_EXPIRY, TimeUnit.SECONDS);
    }

    public void validateCode(String code) {
        String value = redisTemplate.opsForValue().get(code);
        if (value == null) {
            throw AppException.invalidAuthCode("Authorization code expired or invalid");
        }
        redisTemplate.delete(code); // Single use
    }

    public User getUserFromCode(String code) {
        String value = redisTemplate.opsForValue().get(code);
        if (value == null) {
            throw AppException.invalidAuthCode("Code not found");
        }
        String userId = value.split(":")[0];
        return userService.findById(Long.parseLong(userId));
    }

    private void validateRequest(AuthorizeRequest request) {
        if (!"code".equals(request.getResponseType())) {
            throw AppException.invalidAuthCode("Invalid response type");
        }
        if (!oAuthClientService.validateRedirectUri(request.getClientId(), request.getRedirectUri())) {
            throw AppException.invalidRequest("Invalid redirect URI");
        }
    }
}