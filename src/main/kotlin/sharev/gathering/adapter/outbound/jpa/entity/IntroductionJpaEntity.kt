package sharev.gathering.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.gathering.domain.model.FieldSpec

@Entity
@Table(name = "introductions")
class IntroductionJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduction_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    val gathering: GatheringJpaEntity,

    @Column(nullable = false)
    val version: Int,

    @Column(nullable = false)
    val source: String,

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    val fields: Map<String, FieldSpec>,
) : BaseTimeEntity()
