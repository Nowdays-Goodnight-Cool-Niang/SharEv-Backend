package sharev.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import sharev.common.adapter.inbound.security.model.AccountPrincipal;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@EnableRedisHttpSession
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJacksonJsonRedisSerializer(new JsonMapper()));
        return redisTemplate;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ClassLoader classLoader = getClass().getClassLoader();

        // Spring Security 타입 외에 세션에 저장되는 principal과, 그 안의 OAuth2 attributes 값(카카오 회원 id)을 허용한다.
        BasicPolymorphicTypeValidator.Builder typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(AccountPrincipal.class)
                .allowIfSubType(Long.class);

        JsonMapper jsonMapper = JsonMapper.builder()
                .findAndAddModules()
                .addModules(SecurityJacksonModules.getModules(classLoader, typeValidator))
                .addMixIn(AccountPrincipal.class, AccountPrincipalMixin.class)
                .build();

        return new JacksonJsonRedisSerializer<>(jsonMapper, Object.class);
    }

    // authorities와 name은 role, handle, id로 다시 계산되므로 세션에 저장하지 않는다.
    @JsonIgnoreProperties({"authorities", "name"})
    private abstract static class AccountPrincipalMixin {
    }
}
