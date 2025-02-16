package consulting.gazman.security.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.idp.model.OAuthFlowData;
import consulting.gazman.security.idp.model.OAuthSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisSessionConfig {

    @Bean(name = "sessionRedisTemplate")
    public RedisTemplate<String, OAuthSession> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, OAuthSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        // Use the constructor directly with the ObjectMapper
        Jackson2JsonRedisSerializer<OAuthSession> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, OAuthSession.class);
        template.setValueSerializer(serializer);

        return template;
    }

    @Bean(name = "flowDataRedisTemplate")
    public RedisTemplate<String, OAuthFlowData> flowDataRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        RedisTemplate<String, OAuthFlowData> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        // Use the constructor directly with the ObjectMapper
        Jackson2JsonRedisSerializer<OAuthFlowData> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, OAuthFlowData.class);
        template.setValueSerializer(serializer);

        return template;
    }
}