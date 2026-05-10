package sharev.gathering.domain.model

import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode as GatheringCode

data class IntroduceTemplateContent(
    val text: String,
    val fieldPlaceholders: Map<String, String>,
) {
    init {
        validateContentAndFieldPlaceholders(
            text,
            fieldPlaceholders
        )
    }

    fun hasSameFields(fieldPlaceholders: Map<String, String>): Boolean {
        val fields = this.fieldPlaceholders.keys
        val otherFields = fieldPlaceholders.keys
        return fields.size == otherFields.size && fields.containsAll(otherFields)
    }

    fun getFields(): Set<String> = fieldPlaceholders.keys

    companion object {
        private val variablePattern = PatternCache.variablePattern

        private fun validateContentAndFieldPlaceholders(text: String, fieldPlaceholders: Map<String, String>) {
            val matcher = variablePattern.matcher(text)
            val foundKeys = mutableSetOf<String>()

            while (matcher.find()) {
                val key = matcher.group(1)
                foundKeys.add(key)

                if (!fieldPlaceholders.containsKey(key)) {
                    throw GatheringException(GatheringCode.WRONG_TEMPLATE)
                }
            }

            if (foundKeys.size != fieldPlaceholders.size) {
                throw GatheringException(GatheringCode.WRONG_TEMPLATE)
            }
        }
    }
}

private object PatternCache {
    val variablePattern = "\\$\\{([^}]+)}".toPattern()
}
