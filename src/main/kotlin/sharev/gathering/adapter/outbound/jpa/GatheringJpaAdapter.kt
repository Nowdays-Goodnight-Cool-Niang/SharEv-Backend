package sharev.gathering.adapter.outbound.jpa

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.gathering.adapter.outbound.jpa.entity.GatheringJpaEntity
import sharev.gathering.adapter.outbound.jpa.entity.IntroductionJpaEntity
import sharev.gathering.adapter.outbound.jpa.mapper.toDomainModel
import sharev.gathering.adapter.outbound.jpa.repository.GatheringRepository
import sharev.gathering.adapter.outbound.jpa.repository.IntroductionRepository
import sharev.gathering.application.port.outbound.*
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.Introduction
import sharev.team.adapter.outbound.jpa.repository.TeamRepository
import sharev.team.application.port.outbound.QueryGatheringPort
import sharev.team.application.port.outbound.summary.GatheringSummary
import sharev.team.domain.exception.TeamException
import java.util.*
import sharev.gathering.domain.exception.GatheringExceptionCode as GatheringCode
import sharev.team.domain.exception.TeamExceptionCode as TeamCode

@Component
class GatheringJpaAdapter(
    private val gatheringRepository: GatheringRepository,
    private val introductionRepository: IntroductionRepository,
    private val teamRepository: TeamRepository,
) : SaveGatheringPort,
    LoadGatheringPort,
    LoadIntroductionPort,
    QueryGatheringPort,
    SaveIntroductionPort {

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

    override fun loadAll(filter: LoadGatheringFilter, pageable: Pageable): Page<GatheringDetailSummary> {
        return gatheringRepository.searchGatheringDetails(filter, pageable)
    }

    override fun loadAllByTeam(teamId: Long): List<Gathering> {
        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamCode.TEAM_NOT_FOUND)

        return gatheringRepository.findAllByTeam(team)
            .map { it.toDomainModel() }
    }

    override fun loadAllByTeams(teamId: List<Long>, pageable: Pageable): Page<GatheringDetailSummary> {

        if (teamId.isEmpty()) {
            return Page.empty(pageable)
        }

        return gatheringRepository.searchByTeamIds(teamId, pageable)
    }

    override fun loadLatestIntroduction(gatheringId: UUID): Introduction {
        return loadLatestIntroductionOrNull(gatheringId)
            ?: throw GatheringException(GatheringCode.INTRODUCTION_NOT_FOUND)
    }

    override fun loadLatestIntroductionOrNull(gatheringId: UUID): Introduction? {
        return introductionRepository.findTopByGatheringIdOrderByIdDesc(gatheringId)
            ?.toDomainModel()
    }

    override fun loadByGatheringAndVersion(gatheringId: UUID, version: Int): Introduction {
        return introductionRepository.findByGatheringIdAndVersion(gatheringId, version)
            ?.toDomainModel()
            ?: throw GatheringException(GatheringCode.INTRODUCTION_NOT_FOUND)
    }

    override fun findByTeam(teamId: Long): List<GatheringSummary> {
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

    override fun save(introduction: Introduction): Introduction {
        val gathering = gatheringRepository.findByIdOrNull(introduction.gatheringId)
            ?: throw GatheringException(GatheringCode.GATHERING_NOT_FOUND)

        return introductionRepository.save(
            IntroductionJpaEntity(
                if (introduction.id == Introduction.NEW_ID) null else introduction.id,
                gathering,
                introduction.version,
                introduction.template.source,
                introduction.template.fields,
            )
        ).toDomainModel()
    }
}
