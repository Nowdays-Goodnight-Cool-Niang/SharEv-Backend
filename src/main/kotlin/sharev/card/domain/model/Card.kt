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
    val templateVersion: Int?,
    val introductionText: Map<String, String>?,
) {
    fun validateIntroductionText(
        currentTemplateVersion: Int,
        templateFields: Set<String>,
        templateVersion: Int,
        introductionText: Map<String, String>,
    ) {
        if (currentTemplateVersion != templateVersion) {
            throw CardException(CardExceptionCode.INVALID_INTRODUCE_TEMPLATE)
        }

        val introduceFields = introductionText.keys

        if (templateFields.size != introduceFields.size || introduceFields.subtract(templateFields).isNotEmpty()) {
            throw CardException(CardExceptionCode.INVALID_INTRODUCE_TEMPLATE)
        }
    }

    fun isCompleted(): Boolean = pinNumber != null && introductionText != null
}
