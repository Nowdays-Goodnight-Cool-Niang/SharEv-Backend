package sharev.gathering.adapter.outbound.jpa.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.application.port.outbound.LoadGatheringFilter
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary

interface GatheringRepositoryCustom {
    fun searchGatheringDetails(filter: LoadGatheringFilter, pageable: Pageable): Page<GatheringDetailSummary>
    fun searchByTeamIds(teamIds: List<Long>, pageable: Pageable): Page<GatheringDetailSummary>
}
