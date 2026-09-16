package sharev.gathering.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity

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

    @Column(nullable = false)
    val content: String,

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var placeholders: Map<String, String>,
) : BaseTimeEntity() {

}
