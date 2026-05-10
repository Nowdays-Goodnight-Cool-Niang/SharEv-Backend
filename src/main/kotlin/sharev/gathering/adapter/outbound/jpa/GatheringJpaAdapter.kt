package sharev.gathering.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.gathering.adapter.outbound.jpa.entity.GatheringJpaEntity
import sharev.gathering.adapter.outbound.jpa.entity.IntroduceTemplateJpaEntity
import sharev.gathering.adapter.outbound.jpa.mapper.toDomainModel
import sharev.gathering.adapter.outbound.jpa.repository.GatheringRepository
import sharev.gathering.adapter.outbound.jpa.repository.IntroduceTemplateRepository
import sharev.gathering.application.port.outbound.LoadGatheringPort
import sharev.gathering.application.port.outbound.LoadIntroduceTemplatePort
import sharev.gathering.application.port.outbound.SaveGatheringPort
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.IntroduceTemplate
import sharev.gathering.domain.model.IntroduceTemplateContent
import sharev.team.adapter.outbound.jpa.repository.TeamRepository
import sharev.team.application.port.outbound.LoadGatheringSummaryPort
import sharev.team.application.port.outbound.summery.GatheringSummary
import sharev.team.domain.exception.TeamException
import java.util.*
import sharev.gathering.domain.exception.GatheringExceptionCode as GatheringCode
import sharev.team.domain.exception.TeamExceptionCode as TeamCode

@Component
class GatheringJpaAdapter(
    private val gatheringRepository: GatheringRepository,
    private val introduceTemplateRepository: IntroduceTemplateRepository,
    private val teamRepository: TeamRepository,
) : SaveGatheringPort, LoadGatheringPort, LoadIntroduceTemplatePort, LoadGatheringSummaryPort {

    override fun save(gathering: Gathering): Gathering {
        val team = teamRepository.findByIdOrNull(gathering.teamId)
            ?: throw TeamException(TeamCode.TEAM_NOT_FOUND)

        val gatheringJpaEntity = gatheringRepository.save(
            GatheringJpaEntity(
                team = team,
                visible = gathering.visible,
                title = gathering.title,
                content = gathering.content,
                startAt = gathering.startAt,
                endAt = gathering.endAt,
                place = gathering.place,
                imageUrl = gathering.imageUrl,
                gatheringUrl = gathering.gatheringUrl,
                contact = gathering.contact,
                registerStartAt = gathering.registerStartAt,
                registerEndAt = gathering.registerEndAt,
            )
        )

        introduceTemplateRepository.save(
            IntroduceTemplateJpaEntity(
                gathering = gatheringJpaEntity,
                version = 0,
                content = IntroduceTemplateContent("", emptyMap()),
            )
        )

        return gatheringJpaEntity.toDomainModel()
    }

    override fun update(gathering: Gathering): Gathering {
        val gatheringJpaEntity = getGatheringWithTeamValidation(gathering.teamId, gathering.id)

        gatheringJpaEntity.update(
            gathering.visible,
            gathering.title,
            gathering.content,
            gathering.startAt,
            gathering.endAt,
            gathering.place,
            gathering.imageUrl,
            gathering.gatheringUrl,
            gathering.contact,
            gathering.registerStartAt,
            gathering.registerEndAt,
        )

        return gatheringJpaEntity.toDomainModel()
    }

    override fun softDelete(gatheringId: UUID) {
        val gathering = gatheringRepository.findByIdOrNull(gatheringId)
            ?: throw GatheringException(GatheringCode.GATHERING_NOT_FOUND)

        gathering.softDelete()
    }

    override fun load(gatheringId: UUID): Gathering {
        return gatheringRepository.findByIdOrNull(gatheringId)
            ?.toDomainModel()
            ?: throw GatheringException(GatheringCode.GATHERING_NOT_FOUND)
    }

    override fun loadAllByTeam(teamId: Long): List<Gathering> {
        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamCode.TEAM_NOT_FOUND)

        return gatheringRepository.findAllByTeam(team)
            .map { it.toDomainModel() }
    }

    override fun loadLatest(gatheringId: UUID): IntroduceTemplate {
        return introduceTemplateRepository.findTopByGatheringIdOrderByVersionDesc(gatheringId)
            .orElseThrow { GatheringException(GatheringCode.INTRODUCE_TEMPLATE_NOT_FOUND) }
            .toDomainModel()
    }

    override fun loadByGatheringAndVersion(gatheringId: UUID, version: Int): IntroduceTemplate {
        return introduceTemplateRepository.findByGatheringIdAndVersion(gatheringId, version)
            .orElseThrow { GatheringException(GatheringCode.INTRODUCE_TEMPLATE_NOT_FOUND) }
            .toDomainModel()
    }

    override fun loadByTeam(teamId: Long): List<GatheringSummary> {
        return loadAllByTeam(teamId).map {
            GatheringSummary(it.title, it.startAt, it.endAt, it.place)
        }
    }

    fun getGatheringWithTeamValidation(teamId: Long, gatheringId: UUID): GatheringJpaEntity {
        val gathering = gatheringRepository.findByIdOrNull(gatheringId)
            ?: throw GatheringException(GatheringCode.GATHERING_NOT_FOUND)

        if (gathering.team.id != teamId) {
            throw GatheringException(GatheringCode.GATHERING_NOT_FOUND)
        }

        return gathering
    }
}
