package org.example.backend.config;

import static redis.embedded.Redis.DEFAULT_REDIS_PORT;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Profile("!prod")
@Configuration
public class EmbeddedRedisConfig {
    private static final String REDIS_SERVER_MAX_MEMORY = "maxmemory 512M";
    private static final String CHECK_PORT_IS_AVAILABLE_WIN = "netstat -nao | find \"LISTEN\" | find \"%d\"";
    private static final String CHECK_PORT_IS_AVAILABLE_ANOTHER = "netstat -nat | grep LISTEN|grep %d";

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        int port = isRedisRunning() ? findAvailablePort() : DEFAULT_REDIS_PORT;
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

    private boolean isRedisRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(DEFAULT_REDIS_PORT));
    }

    private boolean isRunning(Process process) throws IOException {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }

        } catch (Exception e) {
            throw e;
        }

        return StringUtils.hasText(pidInfo.toString());
    }

    private Process executeGrepProcessCommand(int port) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        String[] shell;
        if (os.contains("win")) {
            String command = String.format(CHECK_PORT_IS_AVAILABLE_WIN, port);
            shell = new String[]{"cmd.exe", "/y", "/c", command};
        } else {
            String command = String.format(CHECK_PORT_IS_AVAILABLE_ANOTHER, port);
            shell = new String[]{"/bin/sh", "-c", command};
        }
        return Runtime.getRuntime().exec(shell);
    }

    private int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if (!isRunning(process)) {
                return port;
            }
        }

        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }
}
