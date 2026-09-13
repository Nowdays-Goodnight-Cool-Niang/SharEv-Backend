package sharev.gathering.domain.model

import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode
import java.time.LocalDateTime
import java.util.*

data class Gathering(
    val id: UUID,
    val teamId: Long,
    val visible: GatheringVisible,
    val title: String,
    val content: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val place: String,
    val imageUrl: String?,
    val gatheringUrl: String?,
    val contact: String?,
    val registerStartAt: LocalDateTime,
    val registerEndAt: LocalDateTime,
) {
    companion object {
        val NEW_ID: UUID = UUID(0L, 0L)
    }

    fun update(
        visible: GatheringVisible,
        title: String,
        content: String,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
        place: String,
        imageUrl: String?,
        gatheringUrl: String?,
        contact: String?,
        registerStartAt: LocalDateTime,
        registerEndAt: LocalDateTime,
    ): Gathering {
        validatePeriod(startAt, endAt)
        validateRegisterPeriod(startAt, endAt, registerStartAt, registerEndAt)

        return copy(
            visible = visible,
            title = title,
            content = content,
            startAt = startAt,
            endAt = endAt,
            place = place,
            imageUrl = imageUrl,
            gatheringUrl = gatheringUrl,
            contact = contact,
            registerStartAt = registerStartAt,
            registerEndAt = registerEndAt,
        )
    }

    private fun validatePeriod(
        startAt: LocalDateTime,
        endAt: LocalDateTime,
    ) {
        if (!startAt.isBefore(endAt)) {
            throw GatheringException(GatheringExceptionCode.INVALID_GATHERING_PERIOD_EXCEPTION)
        }
    }

    private fun validateRegisterPeriod(
        startAt: LocalDateTime,
        endAt: LocalDateTime,
        registerStartAt: LocalDateTime,
        registerEndAt: LocalDateTime,
    ) {
        if (!registerStartAt.isBefore(startAt) || !registerStartAt.isBefore(endAt)) {
            throw GatheringException(GatheringExceptionCode.INVALID_GATHERING_REGISTER_START_PERIOD_EXCEPTION)
        }

        if (!registerEndAt.isBefore(endAt)) {
            throw GatheringException(GatheringExceptionCode.INVALID_GATHERING_REGISTER_END_PERIOD_EXCEPTION)
        }
    }

    fun progressStatus(now: LocalDateTime): PeriodStatus = when {
        now < startAt -> PeriodStatus.UPCOMING
        endAt < now -> PeriodStatus.ENDED
        else -> PeriodStatus.ONGOING
    }

    fun registrationStatus(now: LocalDateTime): PeriodStatus = when {
        now < registerStartAt -> PeriodStatus.UPCOMING
        registerEndAt < now -> PeriodStatus.ENDED
        else -> PeriodStatus.ONGOING
    }
}
