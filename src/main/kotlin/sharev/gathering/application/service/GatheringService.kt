package sharev.gathering.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpsertIntroductionCommand
import sharev.gathering.application.port.inbound.mapper.*
import sharev.gathering.application.port.inbound.result.*
import sharev.gathering.application.port.inbound.usecase.*
import sharev.gathering.application.port.outbound.*
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.Introduction
import sharev.gathering.domain.model.Template
import sharev.team.application.port.outbound.LoadTeamPort
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
    private val loadTeamPort: LoadTeamPort,
) : CheckGatheringParticipantUseCase,
    CreateGatheringUseCase,
    UpdateGatheringUseCase,
    DeleteGatheringUseCase,
    GetIntroductionUseCase,
    GetGatheringsUseCase,
    GetGatheringUseCase,
    UpsertIntroductionUseCase,
    GetManagedGatheringsUseCase {

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

    override fun getGatherings(command: GetGatheringCommand, pageable: Pageable): Page<GatheringResult> {
        val authenticated = command.accountId != null

        val filter = LoadGatheringFilter(
            accountId = command.accountId,
            participated = command.participated.takeIf { authenticated },
            teamId = command.teamId,
            visible = if (authenticated) command.visibility else GatheringVisible.PUBLIC,
            progress = command.progress,
            registration = command.registration,
        )

        return loadGatheringPort.loadAll(filter, pageable)
            .map { it.toResult() }
    }

    override fun getManagedGatherings(
        accountId: Long,
        pageable: Pageable
    ): Page<ManagedGatheringDetailResult> {
        val manageableTeamIds = teamAccessPort.loadManageableTeamIds(accountId)
        val manageableGatherings = loadGatheringPort.loadAllByTeams(manageableTeamIds, pageable)
        val manageableGatheringTeamIds = manageableGatherings.content.map { it.teamId }
            .toSet()

        val manageableTeams = loadTeamPort.loadAll(manageableGatheringTeamIds)
            .associateBy { it.id }
        // TODO: 팀 softDelete 시 에러 발생할 수 있음, 발생하지 않으려면 !! 대신 team이 null일 시 생성되지 않도록 해야 함. 쿼리에서 조정해야 함
        return manageableGatherings
            .map { it.toManagedDetailResult(manageableTeams[it.teamId]!!) }
    }

    override fun getGathering(accountId: Long?, gatheringId: UUID): GatheringDetailResult {
        val gathering = loadGatheringPort.load(gatheringId)

        if (gathering.visible == GatheringVisible.PUBLIC) {
            val team = loadTeamPort.load(gathering.teamId)
            return gathering.toDetailResult(team)
        }

        if (accountId == null) {
            throw GatheringException(GatheringExceptionCode.GATHERING_NOT_FOUND)
        }

        if (!teamAccessPort.hasAccess(accountId, gathering.teamId)) {
            throw GatheringException(GatheringExceptionCode.GATHERING_NOT_FOUND)
        }

        val team = loadTeamPort.load(gathering.teamId)
        return gathering.toDetailResult(team)
    }

    @Transactional
    override fun update(command: UpdateGatheringCommand): ManagedGatheringDetailResult {
        val gathering = loadGatheringPort.load(command.gatheringId)

        validateTeamManage(command.accountId, gathering.teamId)

        val team = loadTeamPort.load(gathering.teamId)

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
        ).toManagedDetailResult(team)
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
    override fun upsertIntroduction(command: UpsertIntroductionCommand): UpsertIntroductionResult {
        val gathering = loadGatheringPort.load(command.gatheringId)
        validateTeamManage(command.accountId, gathering.teamId)

        val latest = loadIntroductionPort.loadLatestIntroductionOrNull(command.gatheringId)
        val introduction = latest?.update(Template(command.source, command.fields))
            ?: Introduction.create(command.gatheringId, Template(command.source, command.fields))

        return saveIntroductionPort.save(introduction)
            .toUpsertResult()
    }
}
