package sharev.card.application.port.outbound

import java.util.*

fun interface IssueJoinOrderPort {
    fun issue(gatheringId: UUID): Int
}
