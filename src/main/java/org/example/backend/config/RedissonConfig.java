package org.example.backend.config;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {
    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress(REDISSON_HOST_PREFIX + host + ":" + port);

        String password = redisProperties.getPassword();
        if (Objects.nonNull(password) && !password.isEmpty()) {
            serverConfig.setPassword(password);
        }

        return Redisson.create(config);
    }
}
