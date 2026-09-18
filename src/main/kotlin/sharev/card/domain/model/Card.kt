package sharev.card.domain.model

import sharev.card.domain.exception.CardException
import sharev.card.domain.exception.CardExceptionCode
import java.util.*

data class Card(
    val id: Long,
    val gatheringId: UUID,
    val accountId: Long,
    val accountName: String,
    val accountEmail: String,
    val pinNumber: Int?,
    val introductionVersion: Int?,
    val fieldValues: Map<String, String>?,
) {
    fun validateFieldValues(
        currentIntroductionVersion: Int,
        fieldNames: Set<String>,
        introductionVersion: Int,
        fieldValues: Map<String, String>,
    ) {
        if (currentIntroductionVersion != introductionVersion) {
            throw CardException(CardExceptionCode.INVALID_FIELD_VALUES)
        }

        if (fieldNames != fieldValues) {
            throw CardException(CardExceptionCode.INVALID_FIELD_VALUES)
        }
    }

    fun isCompleted(): Boolean = pinNumber != null && fieldValues != null
}
