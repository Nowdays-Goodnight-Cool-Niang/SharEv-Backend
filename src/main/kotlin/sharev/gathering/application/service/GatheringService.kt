package sharev.gathering.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpsertIntroductionCommand
import sharev.gathering.application.port.inbound.mapper.toCreateGatheringResult
import sharev.gathering.application.port.inbound.mapper.toDetailResult
import sharev.gathering.application.port.inbound.mapper.toFilter
import sharev.gathering.application.port.inbound.mapper.toResult
import sharev.gathering.application.port.inbound.result.*
import sharev.gathering.application.port.inbound.usecase.*
import sharev.gathering.application.port.outbound.*
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.Introduction
import sharev.gathering.domain.model.Template
import sharev.team.application.port.outbound.TeamAccessPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.util.*

@Service
@Transactional(readOnly = true)
class GatheringService(
    private val checkGatheringParticipantPort: CheckGatheringParticipantPort,
    private val saveGatheringPort: SaveGatheringPort,
    private val loadGatheringPort: LoadGatheringPort,
    private val loadIntroductionPort: LoadIntroductionPort,
    private val teamAccessPort: TeamAccessPort,
    private val saveIntroductionPort: SaveIntroductionPort,
) : CheckGatheringParticipantUseCase,
    CreateGatheringUseCase,
    UpdateGatheringUseCase,
    DeleteGatheringUseCase,
    GetIntroductionUseCase,
    GetGatheringsUseCase,
    GetGatheringUseCase,
    UpsertIntroductionUseCase {

    override fun isParticipant(accountId: Long, gatheringId: UUID): ParticipantResult {
        return ParticipantResult(checkGatheringParticipantPort.isParticipant(gatheringId, accountId))
    }

    @Transactional
    override fun create(command: CreateGatheringCommand): CreateGatheringResult {
        validateTeamManage(command.accountId, command.teamId)

        return saveGatheringPort.save(
            Gathering(
                id = Gathering.NEW_ID,
                teamId = command.teamId,
                visible = command.visible,
                title = command.title,
                content = command.content,
                startAt = command.startAt,
                endAt = command.endAt,
                place = command.place,
                imageUrl = command.imageUrl,
                gatheringUrl = command.gatheringUrl,
                contact = command.contact,
                registerStartAt = command.registerStartAt,
                registerEndAt = command.registerEndAt,
            ),
        ).toCreateGatheringResult()
    }

    override fun getGatherings(
        getGatheringCommand: GetGatheringCommand,
        pageable: Pageable
    ): Page<GatheringDetailResult> {
        return loadGatheringPort.loadAll(getGatheringCommand.toFilter(), pageable)
            .map { it.toDetailResult() }
    }

    override fun getManagedGatherings(
        accountId: Long,
        pageable: Pageable
    ): Page<GatheringDetailResult> {
        val teamIds = teamAccessPort.loadManageableTeamIds(accountId)
        return loadGatheringPort.loadAllByTeams(teamIds, pageable)
            .map { it.toDetailResult() }
    }

    override fun getGathering(accountId: Long?, gatheringId: UUID): GatheringDetailResult {
        val gathering = loadGatheringPort.load(gatheringId)

        if (gathering.visible == GatheringVisible.PUBLIC) {
            return gathering.toDetailResult()
        }

        if (accountId == null) {
            throw GatheringException(GatheringExceptionCode.GATHERING_NOT_FOUND)
        }

        if (!teamAccessPort.hasAccess(accountId, gathering.teamId)) {
            throw GatheringException(GatheringExceptionCode.GATHERING_NOT_FOUND)
        }

        return gathering.toDetailResult()
    }

    @Transactional
    override fun update(command: UpdateGatheringCommand): GatheringDetailResult {
        val gathering = loadGatheringPort.load(command.gatheringId)

        validateTeamManage(command.accountId, gathering.teamId)

        return saveGatheringPort.update(
            gathering.update(
                visible = command.visible,
                title = command.title,
                content = command.content,
                startAt = command.startAt,
                endAt = command.endAt,
                place = command.place,
                imageUrl = command.imageUrl,
                gatheringUrl = command.gatheringUrl,
                contact = command.contact,
                registerStartAt = command.registerStartAt,
                registerEndAt = command.registerEndAt,
            )
        ).toDetailResult()
    }

    @Transactional
    override fun delete(accountId: Long, gatheringId: UUID): DeleteGatheringResult {
        val gathering = loadGatheringPort.load(gatheringId)

        validateTeamManage(accountId, gathering.teamId)

        saveGatheringPort.softDelete(gatheringId)
        return DeleteGatheringResult(gatheringId)
    }

    override fun getLatestIntroduction(gatheringId: UUID, accountId: Long): IntroductionResult {
        if (!checkGatheringParticipantPort.isParticipant(gatheringId, accountId)) {
            throw GatheringException(GatheringExceptionCode.GATHERING_PARTICIPANT_NOT_FOUND)
        }

        return loadIntroductionPort.loadLatestIntroduction(gatheringId)
            .toResult()
    }

    private fun validateTeamManage(accountId: Long, teamId: Long) {
        if (!teamAccessPort.canManage(accountId, teamId)) {
            throw TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE)
        }
    }

    @Transactional
    override fun upsertIntroduction(command: UpsertIntroductionCommand): IntroductionResult {
        val gathering = loadGatheringPort.load(command.gatheringId)
        validateTeamManage(command.accountId, gathering.teamId)

        val latest = loadIntroductionPort.loadLatestIntroductionOrNull(command.gatheringId)
        val introduction = latest?.update(Template(command.source, command.fields))
            ?: Introduction.create(command.gatheringId, Template(command.source, command.fields))

        return saveIntroductionPort.save(introduction)
            .toResult()
    }
}
