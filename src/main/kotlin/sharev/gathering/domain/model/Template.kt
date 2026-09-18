package sharev.gathering.domain.model

import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode

data class Template(
    val source: String,
    val fields: Map<String, FieldSpec>,
) {

    init {
        validateSourceMatchesFields()
    }

    private fun validateSourceMatchesFields() {
        val matcher = FieldPattern.regex.matcher(source)
        val found = mutableSetOf<String>()

        while (matcher.find()) {
            found.add(matcher.group(1))
        }

        if (found != fields.keys) {
            throw GatheringException(GatheringExceptionCode.WRONG_TEMPLATE)
        }
    }
}

private object FieldPattern {
    val regex = "\\$\\{([^}]+)}".toPattern()
}
