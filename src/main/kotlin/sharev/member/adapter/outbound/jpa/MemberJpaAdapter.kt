package sharev.member.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.account.adapter.outbound.jpa.repository.AccountRepository
import sharev.account.domain.exception.AccountException
import sharev.account.domain.exception.AccountExceptionCode
import sharev.member.adapter.outbound.jpa.entity.MemberJpaEntity
import sharev.member.adapter.outbound.jpa.mapper.toDomainModel
import sharev.member.adapter.outbound.jpa.repository.MemberRepository
import sharev.member.application.port.outbound.DeleteMemberPort
import sharev.member.application.port.outbound.LoadAccountForMemberPort
import sharev.member.application.port.outbound.LoadMemberPort
import sharev.member.application.port.outbound.SaveMemberPort
import sharev.member.domain.exception.MemberException
import sharev.member.domain.exception.MemberExceptionCode
import sharev.member.domain.model.Member
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.adapter.outbound.jpa.repository.TeamRepository
import sharev.team.application.port.outbound.CheckTeamMemberPort
import sharev.team.application.port.outbound.SaveTeamAdminMemberPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode

@Component
class MemberJpaAdapter(
    private val memberRepository: MemberRepository,
    private val teamRepository: TeamRepository,
    private val accountRepository: AccountRepository,
) : SaveMemberPort,
    SaveTeamAdminMemberPort,
    LoadMemberPort,
    DeleteMemberPort,
    LoadAccountForMemberPort,
    CheckTeamMemberPort {

    override fun save(
        teamId: Long,
        accountId: Long,
        status: MemberStatus,
        role: MemberRole,
    ): Member {
        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND)

        return memberRepository.save(
            MemberJpaEntity(
                team = team,
                account = account,
                status = status,
                role = role,
            )
        ).toDomainModel()
    }

    override fun saveTeamAdmin(teamId: Long, accountId: Long) {
        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND)

        memberRepository.save(
            MemberJpaEntity(
                team = team,
                account = account,
                status = MemberStatus.ACTIVATE,
                role = MemberRole.ADMIN,
            )
        )
    }

    override fun activate(memberId: Long): Member {
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberException(MemberExceptionCode.MEMBER_NOT_FOUND)

        member.activate()

        return member.toDomainModel()
    }

    override fun updateRole(memberId: Long, role: MemberRole): Member {
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberException(MemberExceptionCode.MEMBER_NOT_FOUND)

        member.updateRole(role)

        return member.toDomainModel()
    }

    override fun load(memberId: Long): Member {
        return memberRepository.findByIdOrNull(memberId)
            ?.toDomainModel()
            ?: throw MemberException(MemberExceptionCode.MEMBER_NOT_FOUND)
    }

    override fun loadByTeamAndAccount(teamId: Long, accountId: Long): Member {
        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)

        return memberRepository.findByTeamAndAccountId(team, accountId)
            ?.toDomainModel()
            ?: throw MemberException(MemberExceptionCode.MEMBER_NOT_FOUND)
    }

    override fun loadAllByTeam(teamId: Long): List<Member> {
        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)

        return memberRepository.findAllByTeam(team)
            .map { it.toDomainModel() }
    }

    override fun isMember(accountId: Long, teamId: Long): Boolean {
        val team = teamRepository.findByIdOrNull(teamId) ?: return false
        return memberRepository.findByTeamAndAccountId(team, accountId) != null
    }

    override fun isAdminMember(accountId: Long, teamId: Long): Boolean {
        val team = teamRepository.findByIdOrNull(teamId) ?: return false
        return memberRepository.findByTeamAndAccountId(team, accountId)?.role == MemberRole.ADMIN
    }

    override fun countByTeamAndRole(teamId: Long, role: MemberRole): Long {
        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamException(TeamExceptionCode.TEAM_NOT_FOUND)

        return memberRepository.countByTeamAndRole(team, role)
    }

    override fun delete(memberId: Long) {
        memberRepository.deleteById(memberId)
    }

    override fun loadAccountIdByEmail(email: String): Long {
        return accountRepository.findByEmail(email)
            .orElseThrow { AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND) }
            .id!!
    }
}
