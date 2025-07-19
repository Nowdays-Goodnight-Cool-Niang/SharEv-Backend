package org.example.backend.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.example.backend.exception.LockInterruptedException;
import org.example.backend.exception.LockOverWaitTimeException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockProcessor {
    private static final int WAIT_TIME = 60;
    private static final int LEASE_TIME = 5;

    private final RedissonClient redissonClient;

    public void lock(String key, Consumer<String> consumer) {
        RLock lock = redissonClient.getLock(key);

        try {
            boolean lockSuccess = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!lockSuccess) {
                throw new LockOverWaitTimeException();
            }

            consumer.accept(key);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException();
        } finally {
            lock.unlock();
        }
    }
}
