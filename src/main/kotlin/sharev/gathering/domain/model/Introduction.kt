package sharev.gathering.domain.model

import java.util.*

data class Introduction(
    val id: Long,
    val gatheringId: UUID,
    val version: Int,
    val template: Template,
) {

    fun update(next: Template): Introduction {
        val isBumpRequired = needsToBump(next.fields.keys)

        return copy(
            id = if (isBumpRequired) NEW_ID else id,
            version = if (isBumpRequired) version + 1 else version,
            template = next,
        )
    }

    private fun needsToBump(keys: Set<String>): Boolean {
        return template.fields.keys != keys
    }

    companion object {
        const val NEW_ID = 0L
        const val FIRST_VERSION = 1
        fun create(gatheringId: UUID, template: Template) =
            Introduction(NEW_ID, gatheringId, FIRST_VERSION, template)
    }
}
