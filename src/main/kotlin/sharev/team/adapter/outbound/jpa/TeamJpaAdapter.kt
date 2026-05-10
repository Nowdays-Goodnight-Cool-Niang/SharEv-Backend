package sharev.team.adapter.outbound.jpa

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity
import sharev.team.adapter.outbound.jpa.mapper.toDomainModel
import sharev.team.adapter.outbound.jpa.repository.TeamRepository
import sharev.team.application.port.outbound.LoadTeamPort
import sharev.team.application.port.outbound.QueryTeamPort
import sharev.team.application.port.outbound.SaveTeamPort
import sharev.team.application.port.outbound.summary.TeamMemberSummary
import sharev.team.application.port.outbound.summary.TeamSummary
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import sharev.team.domain.model.Team

@Component
class TeamJpaAdapter(
    private val teamRepository: TeamRepository,
) : SaveTeamPort, LoadTeamPort, QueryTeamPort {

    override fun save(title: String): Team {
        return try {
            teamRepository.save(TeamJpaEntity(title = title))
                .toDomainModel()
        } catch (e: DataIntegrityViolationException) {
            throw TeamException(TeamExceptionCode.DUPLICATE_TEAM_NAME)
        }
    }

    override fun updateTitle(teamId: Long, title: String): Team {
        val teamJpaEntity = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)

        teamJpaEntity.updateTitle(title)

        return teamJpaEntity.toDomainModel()
    }

    override fun load(teamId: Long): Team {
        return teamRepository.findByIdOrNull(teamId)
            ?.toDomainModel()
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)
    }

    override fun exists(teamId: Long): Boolean {
        return teamRepository.existsById(teamId)
    }

    override fun findMyTeams(accountId: Long): List<TeamSummary> {
        return teamRepository.findMyTeams(accountId)
    }

    override fun findTeamMembers(teamId: Long): List<TeamMemberSummary> {
        return teamRepository.findMyTeamMembers(teamId)
    }
}
