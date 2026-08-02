package sharev.team.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.command.UpdateTeamInfoCommand
import sharev.team.application.port.inbound.mapper.toCreateTeamResult
import sharev.team.application.port.inbound.result.*
import sharev.team.application.port.inbound.usecase.CreateTeamUseCase
import sharev.team.application.port.inbound.usecase.GetMyTeamsUseCase
import sharev.team.application.port.inbound.usecase.GetTeamDetailUseCase
import sharev.team.application.port.inbound.usecase.UpdateTeamInfoUseCase
import sharev.team.application.port.outbound.*
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode

@Service
@Transactional(readOnly = true)
class TeamService(
    private val saveTeamPort: SaveTeamPort,
    private val loadTeamPort: LoadTeamPort,
    private val queryTeamPort: QueryTeamPort,
    private val saveTeamAdminPort: SaveTeamAdminPort,
    private val teamAccessPort: TeamAccessPort,
    private val loadGatheringSummaryPort: LoadGatheringSummaryPort,
) : CreateTeamUseCase,
    GetMyTeamsUseCase,
    GetTeamDetailUseCase,
    UpdateTeamInfoUseCase {

    @Transactional
    override fun create(command: CreateTeamCommand): CreateTeamResult {
        val team = saveTeamPort.save(command.title, command.content, command.type)
        saveTeamAdminPort.saveTeamAdmin(team.id, command.accountId)

        return team.toCreateTeamResult()
    }

    override fun getMyTeams(command: GetMyTeamsCommand): List<TeamInfoResult> {
        return queryTeamPort.findMyTeams(command.accountId)
            .map {
                TeamInfoResult(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    createdAt = it.createdAt,
                    memberRole = it.memberRole,
                    headcount = it.headcount,
                )
            }
    }

    @Transactional
    override fun updateTeamInfo(command: UpdateTeamInfoCommand): TeamUpdateInfoResult {
        if (!teamAccessPort.canManage(command.accountId, command.teamId)) {
            throw TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE)
        }

        val team = saveTeamPort.update(command.teamId, command.title, command.content)
        return TeamUpdateInfoResult(team.title, team.content)
    }

    override fun getTeamDetail(accountId: Long, teamId: Long): TeamDetailResult {
        if (!teamAccessPort.hasAccess(accountId, teamId)) {
            throw TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_ACCESS)
        }

        val team = loadTeamPort.load(teamId)
        val gatherings = loadGatheringSummaryPort.loadByTeam(teamId)
        val members = queryTeamPort.findTeamMembers(teamId)

        return TeamDetailResult(
            id = team.id,
            title = team.title,
            content = team.content,
            createdAt = team.createdAt,
            headcount = members.size,
            certification = team.teamCertification,
            gatherings = gatherings.map {
                GatheringInfoResult(it.title, it.startAt, it.endAt, it.place)
            },
            members = members.map {
                TeamMemberInfoResult(it.name, it.email, it.role)
            },
        )
    }
}
