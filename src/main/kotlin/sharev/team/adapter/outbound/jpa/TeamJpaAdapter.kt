package sharev.team.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.common.adapter.outbound.jpa.exception.onUniqueViolation
import sharev.team.adapter.outbound.jpa.mapper.toDomainModel
import sharev.team.adapter.outbound.jpa.mapper.toJpaEntity
import sharev.team.adapter.outbound.jpa.repository.TeamRepository
import sharev.team.application.port.outbound.LoadTeamPort
import sharev.team.application.port.outbound.SaveTeamPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import sharev.team.domain.model.Team

@Component
class TeamJpaAdapter(
    private val teamRepository: TeamRepository,
) : SaveTeamPort,
    LoadTeamPort {

    override fun save(team: Team): Team {
        return onUniqueViolation({ TeamException(TeamExceptionCode.DUPLICATE_TEAM_NAME) }) {
            teamRepository.save(team.toJpaEntity())
                .toDomainModel()
        }
    }

    override fun updateTitleAndContent(team: Team): Team {
        val entity = teamRepository.findByIdOrNull(team.id)
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)

        entity.update(team.title, team.content)

        return onUniqueViolation({ TeamException(TeamExceptionCode.DUPLICATE_TEAM_NAME) }) {
            teamRepository.saveAndFlush(entity)
                .toDomainModel()
        }
    }

    override fun load(teamId: Long): Team {
        return teamRepository.findByIdOrNull(teamId)
            ?.toDomainModel()
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)
    }

    override fun loadAll(teamIds: Collection<Long>): List<Team> {
        return teamRepository.findAllById(teamIds)
            .map { it.toDomainModel() }
    }

    override fun exists(teamId: Long): Boolean {
        return teamRepository.existsById(teamId)
    }
}
