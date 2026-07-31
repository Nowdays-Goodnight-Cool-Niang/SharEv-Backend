package sharev.team.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity

interface TeamRepository : JpaRepository<TeamJpaEntity, Long>, TeamRepositoryCustom {

}
