package sharev.member.application.port.outbound

import sharev.member.domain.model.Member
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus

interface SaveMemberPort {
    fun save(teamId: Long, accountId: Long, status: MemberStatus, role: MemberRole): Member
    fun activate(memberId: Long): Member
    fun updateRole(memberId: Long, role: MemberRole): Member
}
