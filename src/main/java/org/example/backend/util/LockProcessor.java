package org.example.backend.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockProcessor {
    private static final String LOCK_PREFIX = "redisson-lock";
    private static final int WAIT_TIME = 60;
    private static final int LEASE_TIME = 5;

    private final RedissonClient redissonClient;

    public void lock(String key, Consumer<String> consumer) {
        RLock lock = redissonClient.getLock(String.join("-", LOCK_PREFIX, key));
        boolean lockSuccessFlag = false;

        try {
            lockSuccessFlag = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!lockSuccessFlag) {
                throw new LockOverWaitTimeException();
            }

            consumer.accept(key);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException();
        } finally {
            if (lockSuccessFlag && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
