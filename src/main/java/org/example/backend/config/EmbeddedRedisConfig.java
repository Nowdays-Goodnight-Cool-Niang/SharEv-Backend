package org.example.backend.config;

import static redis.embedded.Redis.DEFAULT_REDIS_PORT;

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

    private boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private int findAvailablePort() {
        for (int port = 10000; port <= 65535; port++) {
            if (!isPortInUse(port)) {
                return port;
            }
        }

        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }

    @PostConstruct
    public void startRedis() throws IOException {
        int port = isPortInUse(DEFAULT_REDIS_PORT) ? findAvailablePort() : DEFAULT_REDIS_PORT;
        redisServer = RedisServer.newRedisServer()
                .port(port)
                .setting(REDIS_SERVER_MAX_MEMORY)
                .build();

        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
