package sharev.config;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {
    private static final String REDISSON_HOST_PREFIX = "redis://";

    private final RedisProperties redisProperties;
    private final Optional<RedisServer> embeddedRedisServer;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();

        final String host;
        final int port;

        if (embeddedRedisServer.isPresent()) {
            host = "localhost";
            port = embeddedRedisServer.get().ports().get(0);
        } else {
            host = redisProperties.getHost();
            port = redisProperties.getPort();
        }

        serverConfig.setAddress(REDISSON_HOST_PREFIX + host + ":" + port);

        String password = redisProperties.getPassword();
        if (Objects.nonNull(password) && !password.isEmpty()) {
            serverConfig.setPassword(password);
        }

        return Redisson.create(config);
    }
}
