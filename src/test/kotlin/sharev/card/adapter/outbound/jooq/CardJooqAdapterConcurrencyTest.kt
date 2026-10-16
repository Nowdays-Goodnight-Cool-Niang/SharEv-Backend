package sharev.card.adapter.outbound.jooq

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import sharev.card.application.port.outbound.IssueJoinOrderPort
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val THREAD_COUNT = 20

@SpringBootTest
@Testcontainers
@TestPropertySource(
    properties = [
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:ddl.sql",
        "spring.sql.init.data-locations=classpath:dml.sql",
        "spring.datasource.hikari.maximum-pool-size=$THREAD_COUNT",
    ]
)
class CardJooqAdapterConcurrencyTest @Autowired constructor(
    private val issueJoinOrderPort: IssueJoinOrderPort,
    private val transactionTemplate: TransactionTemplate,
) {

    @Test
    @DisplayName("카운터가 없는 행사에 동시에 발급해도 순번이 겹치거나 빠지지 않는다")
    fun issue_concurrentlyOnGatheringWithoutSequence() {
        val gatheringId = UUID.fromString("45b2e9d2-5a21-4d1a-8c9e-5f8e5b4e3f17") // 시드상 카드가 없는 행사

        val issued = issueConcurrently(gatheringId)

        assertThat(issued.sorted()).isEqualTo((0 until THREAD_COUNT).toList())
    }

    @Test
    @DisplayName("카운터가 있는 행사에 동시에 발급해도 이어지는 순번이 겹치거나 빠지지 않는다")
    fun issue_concurrentlyOnGatheringWithSequence() {
        val gatheringId = UUID.fromString("a81bc81b-dead-4e5d-abff-90865d1e13b1") // 시드상 last_join_order = 2

        val issued = issueConcurrently(gatheringId)

        assertThat(issued.sorted()).isEqualTo((3 until 3 + THREAD_COUNT).toList())
    }

    private fun issueConcurrently(gatheringId: UUID): List<Int> {
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(THREAD_COUNT)

        try {
            val futures = (1..THREAD_COUNT).map {
                executor.submit(Callable {
                    start.await()
                    transactionTemplate.execute { issueJoinOrderPort.issue(gatheringId) }
                })
            }
            start.countDown()

            return futures.map { it.get(10, TimeUnit.SECONDS) }
        } finally {
            executor.shutdownNow()
        }
    }

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")
    }
}
