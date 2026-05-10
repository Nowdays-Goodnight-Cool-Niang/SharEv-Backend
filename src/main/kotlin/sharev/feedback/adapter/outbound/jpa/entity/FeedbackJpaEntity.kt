package sharev.feedback.adapter.outbound.jpa.entity

import jakarta.persistence.*
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity

@Entity
@Table(name = "feedbacks")
class FeedbackJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    val id: Long? = null,

    @Column
    var content: String,
) : BaseTimeEntity() {
}
