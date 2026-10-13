package sharev.card.adapter.outbound.jooq

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.springframework.stereotype.Component
import sharev.card.application.port.outbound.IssueJoinOrderPort
import java.util.*

@Component
class CardJooqAdapter(
    private val dsl: DSLContext,
) : IssueJoinOrderPort {
    private companion object {
        val JOIN_ORDER_SEQUENCES = DSL.table(DSL.unquotedName("join_order_sequences"))
        val GATHERING_ID = DSL.field(DSL.unquotedName("join_order_sequences", "gathering_id"), SQLDataType.UUID)
        val LAST_JOIN_ORDER =
            DSL.field(DSL.unquotedName("join_order_sequences", "last_join_order"), SQLDataType.INTEGER)
    }

    override fun issue(gatheringId: UUID): Int =
        dsl.insertInto(JOIN_ORDER_SEQUENCES, GATHERING_ID, LAST_JOIN_ORDER)
            .values(gatheringId, 0)
            .onConflict(GATHERING_ID)
            .doUpdate()
            .set(LAST_JOIN_ORDER, LAST_JOIN_ORDER.plus(1))
            .returningResult(LAST_JOIN_ORDER)
            .fetchSingle(LAST_JOIN_ORDER)!!
}
