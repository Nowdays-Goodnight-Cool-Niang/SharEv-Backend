package sharev.team.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.command.UpdateTeamInfoCommand
import sharev.team.application.port.inbound.mapper.toCreateTeamResult
import sharev.team.application.port.inbound.mapper.toResult
import sharev.team.application.port.inbound.result.*
import sharev.team.application.port.inbound.usecase.CreateTeamUseCase
import sharev.team.application.port.inbound.usecase.GetMyTeamsUseCase
import sharev.team.application.port.inbound.usecase.GetTeamDetailUseCase
import sharev.team.application.port.inbound.usecase.UpdateTeamInfoUseCase
import sharev.team.application.port.outbound.*
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import sharev.team.domain.model.Team

@Service
@Transactional(readOnly = true)
class TeamService(
    private val saveTeamPort: SaveTeamPort,
    private val loadTeamPort: LoadTeamPort,
    private val saveTeamAdminPort: SaveTeamAdminPort,
    private val teamAccessPort: TeamAccessPort,
    private val teamGatheringPort: TeamGatheringPort,
    private val teamMemberPort: TeamMemberPort,
    private val loadJoinedTeamPort: LoadJoinedTeamPort,
) : CreateTeamUseCase,
    GetMyTeamsUseCase,
    GetTeamDetailUseCase,
    UpdateTeamInfoUseCase {

    @Transactional
    override fun create(command: CreateTeamCommand): CreateTeamResult {
        val team = saveTeamPort.save(Team.create(command.title, command.content))
        saveTeamAdminPort.save(team.id, command.accountId)
        return team.toCreateTeamResult()
    }

    override fun getJoinedTeams(command: GetMyTeamsCommand, pageable: Pageable): Page<JoinedTeamResult> {
        val joinedTeams = loadJoinedTeamPort.load(command.accountId, pageable)
        val teamIds = joinedTeams.content.map { it.teamId }
        val teams = loadTeamPort.loadAll(teamIds)
            .associateBy { it.id }
        val teamHeadcounts = loadJoinedTeamPort.headcount(teamIds)
        // TODO: 멤버 -> 팀으로 조회했을 때 팀이 softDelete 상태라면 에러 발생. 페이지네이션이기에 쿼리에서 해결해야 함
        return joinedTeams.map {
            val team = teams.getValue(it.teamId)
            val headcount = teamHeadcounts.getValue(it.teamId)
            return@map it.toResult(team, headcount)
        }
    }

    @Transactional
    override fun updateTeamInfo(command: UpdateTeamInfoCommand): TeamUpdateInfoResult {
        if (!teamAccessPort.canManage(command.accountId, command.teamId)) {
            throw TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE)
        }

        val team = loadTeamPort.load(command.teamId)

        val updatedTeam = saveTeamPort.updateTitleAndContent(team.updateInfo(command.title, command.content))

        return TeamUpdateInfoResult(updatedTeam.title, updatedTeam.content)
    }

    override fun getTeamDetail(accountId: Long, teamId: Long): TeamDetailResult {
        if (!teamAccessPort.hasAccess(accountId, teamId)) {
            throw TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_ACCESS)
        }

        val team = loadTeamPort.load(teamId)
        val gatherings = teamGatheringPort.findGatheringsByTeamId(teamId)
        val members = teamMemberPort.findMembersByTeamId(teamId)

        return TeamDetailResult(
            id = team.id,
            title = team.title,
            content = team.content,
            createdAt = team.createdAt,
            headcount = members.size,
            certification = team.certification,
            gatherings = gatherings.map {
                GatheringInfoResult(it.title, it.startAt, it.endAt, it.place)
            },
            members = members.map {
                TeamMemberInfoResult(it.name, it.email, it.role)
            },
        )
    }
}
