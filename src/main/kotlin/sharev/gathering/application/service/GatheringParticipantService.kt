package sharev.gathering.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.mapper.toCreateGatheringResult
import sharev.gathering.application.port.inbound.mapper.toDetailResult
import sharev.gathering.application.port.inbound.mapper.toResult
import sharev.gathering.application.port.inbound.result.*
import sharev.gathering.application.port.inbound.usecase.*
import sharev.gathering.application.port.outbound.CheckGatheringParticipantPort
import sharev.gathering.application.port.outbound.LoadGatheringPort
import sharev.gathering.application.port.outbound.LoadIntroduceTemplatePort
import sharev.gathering.application.port.outbound.SaveGatheringPort
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode
import sharev.gathering.domain.model.Gathering
import sharev.team.application.port.outbound.CheckTeamMemberPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.util.*

@Service
@Transactional(readOnly = true)
class GatheringParticipantService(
    private val checkGatheringParticipantPort: CheckGatheringParticipantPort,
    private val saveGatheringPort: SaveGatheringPort,
    private val loadGatheringPort: LoadGatheringPort,
    private val loadIntroduceTemplatePort: LoadIntroduceTemplatePort,
    private val checkTeamMemberPort: CheckTeamMemberPort,
) : CheckGatheringParticipantUseCase,
    CreateGatheringUseCase,
    GetGatheringUseCase,
    UpdateGatheringUseCase,
    DeleteGatheringUseCase,
    GetIntroduceTemplateUseCase {

    override fun isParticipant(accountId: Long, gatheringId: UUID): ParticipantResult {
        return ParticipantResult(checkGatheringParticipantPort.isParticipant(gatheringId, accountId))
    }

    @Transactional
    override fun create(command: CreateGatheringCommand): CreateGatheringResult {
        validateTeamAdmin(command.accountId, command.teamId)

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

    override fun getGatherings(accountId: Long, teamId: Long): List<GatheringDetailResult> {
        validateTeamMember(accountId, teamId) // TODO: 단순 조회에서 이 로직이 필요할 것 같지는 않음, 공개되지 않은 행사 조회에 영향을 끼쳐야 함

        return loadGatheringPort.loadAllByTeam(teamId)
            .map { it.toDetailResult() }
    }

    override fun getGathering(accountId: Long, teamId: Long, gatheringId: UUID): GatheringDetailResult {
        validateTeamMember(accountId, teamId)

        val gathering = loadGatheringPort.load(gatheringId)

        if (gathering.teamId != teamId) {
            throw GatheringException(GatheringExceptionCode.GATHERING_NOT_FOUND)
        }

        return gathering.toDetailResult() // TODO: 해당 행사 소개 템플릿 마지막 버전 가져와서 detail에 넣어줘야 하는지? 기본적으로 행사 미참여라면 템플릿 내용이 보이지 않음
    }

    @Transactional
    override fun update(command: UpdateGatheringCommand): GatheringDetailResult {
        validateTeamAdmin(command.accountId, command.teamId)

        return saveGatheringPort.update(
            Gathering(
                id = command.gatheringId,
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
            )
        ).toDetailResult()
    }

    @Transactional
    override fun delete(accountId: Long, teamId: Long, gatheringId: UUID): DeleteGatheringResult {
        validateTeamAdmin(accountId, teamId)
        validateGatheringBelongsToTeam(teamId, gatheringId)

        saveGatheringPort.softDelete(gatheringId)
        return DeleteGatheringResult(gatheringId)
    }

    override fun getLatestTemplate(gatheringId: UUID, accountId: Long): IntroduceTemplateResult {
        if (!checkGatheringParticipantPort.isParticipant(gatheringId, accountId)) {
            throw GatheringException(GatheringExceptionCode.GATHERING_PARTICIPANT_NOT_FOUND)
        }

        return loadIntroduceTemplatePort.loadLatest(gatheringId)
            .toResult()
    }

    private fun validateGatheringBelongsToTeam(teamId: Long, gatheringId: UUID) {
        val gathering = loadGatheringPort.load(gatheringId)

        if (gathering.teamId != teamId) {
            throw GatheringException(GatheringExceptionCode.GATHERING_NOT_FOUND)
        }
    }

    private fun validateTeamMember(accountId: Long, teamId: Long) {
        if (!checkTeamMemberPort.isMember(accountId, teamId)) {
            throw TeamException(TeamExceptionCode.NOT_TEAM_MEMBER)
        }
    }

    private fun validateTeamAdmin(accountId: Long, teamId: Long) {
        if (!checkTeamMemberPort.isAdminMember(accountId, teamId)) {
            throw TeamException(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER)
        }
    }
}
