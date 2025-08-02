package org.example.backend.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Profile("!prod")
@Configuration
public class EmbeddedRedisConfig {
    private static final String REDIS_SERVER_MAX_MEMORY = "maxmemory 512M";
    private RedisServer redisServer;

    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    @PostConstruct
    public void startRedis() throws IOException {
        int port = findAvailablePort();
        redisServer = RedisServer.newRedisServer()
                .port(port)
                .setting(REDIS_SERVER_MAX_MEMORY)
                .build();
        redisServer.start();
        System.setProperty("spring.data.redis.port", String.valueOf(port));
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
