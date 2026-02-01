package sharev.config;

import java.io.IOException;
import java.net.ServerSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

@Profile("!prod")
@Configuration
public class EmbeddedRedisConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RedisServer redisServer() throws IOException {
        return new RedisServer(findAvailablePort());
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisServer redisServer) {
        return new LettuceConnectionFactory("localhost", redisServer.ports().get(0));
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
