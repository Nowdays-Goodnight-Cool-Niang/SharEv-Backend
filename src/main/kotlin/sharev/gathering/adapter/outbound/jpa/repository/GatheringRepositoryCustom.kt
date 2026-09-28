package sharev.gathering.adapter.outbound.jpa.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.adapter.outbound.jpa.entity.GatheringJpaEntity
import sharev.gathering.application.port.outbound.LoadGatheringFilter
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary

interface GatheringRepositoryCustom {
    fun searchGatherings(filter: LoadGatheringFilter, pageable: Pageable): Page<GatheringJpaEntity>
    fun searchByTeamIds(teamIds: List<Long>, pageable: Pageable): Page<GatheringDetailSummary>
}
