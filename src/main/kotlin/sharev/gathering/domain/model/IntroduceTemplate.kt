package sharev.gathering.domain.model

import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode
import java.util.*

data class IntroduceTemplate(
    val id: Long,
    val gatheringId: UUID,
    val version: Int,
    val content: String,
    val placeholders: Map<String, String>,
) {
    init {
        validateContentAndPlaceholders(content, placeholders)
    }

    fun update(newContent: String, newPlaceholders: Map<String, String>): IntroduceTemplate {
        validateContentAndPlaceholders(newContent, newPlaceholders)

        val isVersionBumpRequired = needsVersionBump(newPlaceholders.keys)
        val nextVersion = if (isVersionBumpRequired) version + 1 else version

        return copy(
            version = nextVersion,
            content = newContent,
            placeholders = newPlaceholders,
        )
    }

    private fun needsVersionBump(newKeys: Set<String>): Boolean {
        return newKeys.subtract(placeholders.keys)
            .isNotEmpty()
    }

    companion object {
        private val variablePattern = PatternCache.variablePattern

        private fun validateContentAndPlaceholders(content: String, placeholders: Map<String, String>) {
            val matcher = variablePattern.matcher(content)
            val foundKeys = mutableSetOf<String>()

            while (matcher.find()) {
                val key = matcher.group(1)
                foundKeys.add(key)

                if (!placeholders.containsKey(key)) {
                    throw GatheringException(GatheringExceptionCode.WRONG_TEMPLATE)
                }
            }

            if (foundKeys.size != placeholders.size) {
                throw GatheringException(GatheringExceptionCode.WRONG_TEMPLATE)
            }
        }
    }
}

private object PatternCache {
    val variablePattern = "\\$\\{([^}]+)}".toPattern()
}
