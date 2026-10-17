package sharev.card.adapter.outbound.jooq

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jooq.test.autoconfigure.JooqTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import sharev.gathering.domain.model.GatheringVisible
import sharev.team.domain.model.TeamCertification
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val THREAD_COUNT = 20

@JooqTest
@Testcontainers
@Import(CardJooqAdapter::class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestPropertySource(
    properties = [
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:ddl.sql",
        "spring.datasource.hikari.maximum-pool-size=$THREAD_COUNT",
    ]
)
class CardJooqAdapterConcurrencyTest @Autowired constructor(
    private val cardJooqAdapter: CardJooqAdapter,
    private val transactionTemplate: TransactionTemplate,
    private val dsl: DSLContext,
) {

    @AfterEach
    fun clear() {
        dsl.execute("TRUNCATE join_order_sequences, gatherings, teams RESTART IDENTITY CASCADE")
    }

    @Test
    @DisplayName("카운터가 없는 행사에 동시에 발급해도 순번이 겹치거나 빠지지 않는다")
    fun issue_concurrentlyOnGatheringWithoutSequence() {

        // given
        val teamId = createTeam()
        val gatheringId = createGathering(teamId)
        
        // when
        val issued = issueConcurrently(gatheringId)

        // then
        assertThat(issued.sorted()).isEqualTo((0 until THREAD_COUNT).toList())
    }

    @Test
    @DisplayName("카운터가 있는 행사에 동시에 발급해도 이어지는 순번이 겹치거나 빠지지 않는다")
    fun issue_concurrentlyOnGatheringWithSequence() {

        // given
        val teamId = createTeam()
        val gatheringId = createGathering(teamId)
        val lastJoinOrder = 2
        createJoinOrderSequence(gatheringId, lastJoinOrder)

        // when
        val issued = issueConcurrently(gatheringId)

        // then
        assertThat(issued.sorted()).isEqualTo((lastJoinOrder + 1 until lastJoinOrder + 1 + THREAD_COUNT).toList())
    }

    private fun issueConcurrently(gatheringId: UUID): List<Int> {
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(THREAD_COUNT)

        try {
            val futures = (1..THREAD_COUNT).map {
                executor.submit(Callable {
                    start.await()
                    transactionTemplate.execute { cardJooqAdapter.issue(gatheringId) }
                })
            }
            start.countDown()

            return futures.map { it.get(10, TimeUnit.SECONDS) }
        } finally {
            executor.shutdownNow()
        }
    }

    private fun createTeam(): Long {
        return dsl.fetchValue(
            "INSERT INTO teams (certification, title) VALUES (?, ?) RETURNING team_id",
            TeamCertification.NONE.name,
            "테스트 팀",
        ) as Long
    }

    private fun createGathering(teamId: Long): UUID {
        val now = LocalDateTime.now()
        val gatheringId = UUID.randomUUID()

        dsl.execute(
            "INSERT INTO gatherings (gathering_id, visible, team_id, title, content, start_at, end_at, place, register_start_at, register_end_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            gatheringId,
            GatheringVisible.PUBLIC.name,
            teamId,
            "테스트 행사 제목",
            "테스트 행사 내용",
            now,
            now.plusDays(1),
            "강남",
            now.minusDays(1),
            now,
        )

        return gatheringId
    }

    private fun createJoinOrderSequence(gatheringId: UUID, lastJoinOrder: Int) {
        dsl.execute(
            "INSERT INTO join_order_sequences (gathering_id, last_join_order) VALUES (?, ?)",
            gatheringId, lastJoinOrder,
        )
    }

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")
    }
}
