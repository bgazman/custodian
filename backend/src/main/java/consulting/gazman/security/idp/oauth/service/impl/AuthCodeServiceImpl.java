package consulting.gazman.security.idp.oauth.service.impl;

import consulting.gazman.security.idp.oauth.service.AuthCodeService;
import consulting.gazman.security.client.user.entity.User;
import consulting.gazman.security.idp.oauth.dto.AuthorizeRequest;
import consulting.gazman.security.client.user.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import consulting.gazman.security.common.exception.AppException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthCodeServiceImpl implements AuthCodeService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    OAuthClientServiceImpl oAuthClientService;
    private static final long CODE_EXPIRY = 600; // 10 minutes

    @Override
    public String generateCode(String email, String clientId) {
        String code = UUID.randomUUID().toString(); // Generate the code
        String redisKey = "authCode:" + code; // Key includes the code itself
        String redisValue = email + ":" + clientId; // Value contains userId and clientId

        // Store in Redis
        redisTemplate.opsForValue().set(
                redisKey,
                redisValue,
                CODE_EXPIRY,
                TimeUnit.SECONDS
        );

        System.out.println("Generated authorization code: " + code + " for userId: " + email + ", clientId: " + clientId);
        return code; // Return the generated code to the caller
    }



    @Override
    public String validateCode(String code) {
        String redisKey = "authCode:" + code; // Form the Redis key

        // Retrieve and validate the code
        String value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            System.out.println("Authorization code not found or expired: " + code);
            throw AppException.invalidAuthCode("Authorization code expired or invalid");
        }

        // Delete the key to enforce single-use
        redisTemplate.delete(redisKey);

        System.out.println("Authorization code validated successfully: " + code);
        return value; // Return the stored value (e.g., email:clientId)
    }


    @Override
    public User getUserFromCode(String code) {
        String value = redisTemplate.opsForValue().get(code);
        if (value == null) {
            throw AppException.invalidAuthCode("Code not found");
        }
        String email = value.split(":")[0];
        return userService.findByEmail(email);
    }
    @Override
    public void validateRequest(AuthorizeRequest request) {
        if (!"code".equals(request.getResponseType())) {
            throw AppException.invalidAuthCode("Invalid response type");
        }
        if (!oAuthClientService.validateRedirectUri(request.getClientId(), request.getRedirectUri())) {
            throw AppException.invalidRequest("Invalid redirect URI");
        }
    }
}