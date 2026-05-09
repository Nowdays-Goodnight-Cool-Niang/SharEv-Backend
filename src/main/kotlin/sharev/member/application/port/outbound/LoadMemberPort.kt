package sharev.member.application.port.outbound

import sharev.member.domain.model.Member
import sharev.member.domain.model.MemberRole

interface LoadMemberPort {
    fun load(memberId: Long): Member
    fun loadByTeamAndAccount(teamId: Long, accountId: Long): Member
    fun loadAllByTeam(teamId: Long): List<Member>
    fun countByTeamAndRole(teamId: Long, role: MemberRole): Long
}
