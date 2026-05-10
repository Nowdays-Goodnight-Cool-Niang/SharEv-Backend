package sharev.gathering.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.gathering.domain.model.IntroduceTemplateContent

@Entity
@Table(name = "introduce_templates")
class IntroduceTemplateJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduce_template_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    val gathering: GatheringJpaEntity,

    @Column(nullable = false)
    val version: Int,

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var content: IntroduceTemplateContent,
) : BaseTimeEntity() {

    fun validateIntroduce(version: Int, introduce: Map<String, String>): Boolean {
        if (this.version != version) {
            return false
        }

        val introduceFields = introduce.keys
        val introduceTemplateFields = content.getFields()

        if (introduceTemplateFields.size != introduceFields.size) {
            return false
        }

        return introduceFields.subtract(introduceTemplateFields).isEmpty()
    }

    fun updateContent(newContent: IntroduceTemplateContent) {
        if (!content.hasSameFields(newContent.fieldPlaceholders)) {
            throw IllegalArgumentException("필드 구조가 다르면 업데이트할 수 없소")
        }

        content = newContent
    }
}
