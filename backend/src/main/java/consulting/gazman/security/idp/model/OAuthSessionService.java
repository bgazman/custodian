package consulting.gazman.security.idp.model;

import consulting.gazman.security.common.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OAuthSessionService {
    private final RedisTemplate<String, OAuthSession> redisTemplate;
    private static final long SESSION_TIMEOUT = 600; // 10 minutes

    public OAuthSession getSession(String sessionId) {
        return redisTemplate.opsForValue().get("oauth:session:" + sessionId);
    }

    public void saveSession(String sessionId, OAuthSession session) {
        redisTemplate.opsForValue().set(
                "oauth:session:" + sessionId,
                session,
                SESSION_TIMEOUT,
                TimeUnit.SECONDS
        );
    }

    public void removeSession(String sessionId) {
        redisTemplate.delete("oauth:session:" + sessionId);
    }

    public void validateState(String expectedState, String actualState) {
        if (!Objects.equals(expectedState, actualState)) {
            throw new AppException("STATE_MISMATCH", "Invalid state parameter");
        }
    }
}