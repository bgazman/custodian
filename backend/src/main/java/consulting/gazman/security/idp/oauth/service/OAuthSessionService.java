package consulting.gazman.security.idp.oauth.service;

import consulting.gazman.security.idp.model.OAuthSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OAuthSessionService {
    @Autowired
    @Qualifier("sessionRedisTemplate")
    private RedisTemplate<String, OAuthSession> sessionRedisTemplate;

    public void saveSession(String sessionId, OAuthSession session) {
        sessionRedisTemplate.opsForValue().set("oauth:session:" + sessionId, session, 24, TimeUnit.HOURS);
    }

    public OAuthSession getSession(String sessionId) {
        return sessionRedisTemplate.opsForValue().get("oauth:session:" + sessionId);
    }

    public void removeSession(String sessionId) {
        sessionRedisTemplate.delete("oauth:session:" + sessionId);
    }
}