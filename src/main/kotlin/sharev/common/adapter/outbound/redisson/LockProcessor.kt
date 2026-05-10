package sharev.common.adapter.outbound.redisson

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import sharev.common.domain.exception.LockException
import sharev.common.domain.exception.LockExceptionCode
import java.util.concurrent.TimeUnit

@Component
class LockProcessor(
    private val redissonClient: RedissonClient,
) {
    fun lock(key: String, consumer: (String) -> Unit) {
        val lock = redissonClient.getLock("$LOCK_PREFIX-$key")
        var lockSuccessFlag = false

        try {
            lockSuccessFlag = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)
            if (!lockSuccessFlag) {
                throw LockException(LockExceptionCode.LOCK_OVER_WAIT_TIME)
            }

            consumer(key)

        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw LockException(LockExceptionCode.LOCK_INTERRUPTED)
        } finally {
            if (lockSuccessFlag && lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    fun lock(key: String, consumer: (String, () -> Boolean) -> Unit) {
        val lock = redissonClient.getLock("$LOCK_PREFIX-$key")
        var lockSuccessFlag = false

        try {
            lockSuccessFlag = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)
            if (!lockSuccessFlag) {
                throw LockException(LockExceptionCode.LOCK_OVER_WAIT_TIME)
            }

            consumer(key, lock::isHeldByCurrentThread)

        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw LockException(LockExceptionCode.LOCK_INTERRUPTED)
        } finally {
            if (lockSuccessFlag && lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    private companion object {
        const val LOCK_PREFIX = "redisson-lock"
        const val WAIT_TIME = 60L
        const val LEASE_TIME = 5L
    }
}
