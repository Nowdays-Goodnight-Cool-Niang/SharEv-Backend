package sharev.member.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.member.application.port.inbound.command.*
import sharev.member.application.port.inbound.mapper.*
import sharev.member.application.port.inbound.result.*
import sharev.member.application.port.inbound.usecase.*
import sharev.member.application.port.outbound.DeleteMemberPort
import sharev.member.application.port.outbound.LoadAccountForMemberPort
import sharev.member.application.port.outbound.LoadMemberPort
import sharev.member.application.port.outbound.SaveMemberPort
import sharev.member.domain.exception.MemberException
import sharev.member.domain.exception.MemberExceptionCode
import sharev.member.domain.model.Member
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.application.port.outbound.CheckTeamMemberPort
import sharev.team.application.port.outbound.LoadTeamPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode

@Service
@Transactional(readOnly = true)
class MemberService(
    private val loadTeamPort: LoadTeamPort,
    private val loadMemberPort: LoadMemberPort,
    private val saveMemberPort: SaveMemberPort,
    private val deleteMemberPort: DeleteMemberPort,
    private val loadAccountForMemberPort: LoadAccountForMemberPort,
    private val checkTeamMemberPort: CheckTeamMemberPort,
) : GetMembersUseCase,
    InviteMemberUseCase,
    AcceptInvitationUseCase,
    LeaveTeamUseCase,
    UpdateMemberRoleUseCase,
    RemoveMemberUseCase,
    CheckTeamAdminUseCase {

    override fun isAdmin(teamId: Long, accountId: Long): Boolean {
        if (!loadTeamPort.exists(teamId)) {
            return false
        }

        return checkTeamMemberPort.isAdminMember(accountId, teamId)
    }

    override fun getMembers(command: GetMembersCommand): List<MemberResult> {
        validateTeamMember(command.accountId, command.teamId)

        return loadMemberPort.loadAllByTeam(command.teamId)
            .map { it.toResult() }
    }

    @Transactional
    override fun invite(command: InviteMemberCommand): InviteMemberResult {
        validateTeamAdmin(command.accountId, command.teamId)

        val targetAccountId = loadAccountForMemberPort.loadAccountIdByEmail(command.email)

        if (checkTeamMemberPort.isMember(targetAccountId, command.teamId)) {
            throw MemberException(MemberExceptionCode.MEMBER_ALREADY_EXISTS)
        }

        return saveMemberPort.save(command.teamId, targetAccountId, MemberStatus.INVITE, MemberRole.COMMON)
            .toInviteMemberResult()
    }

    @Transactional
    override fun acceptInvitation(command: AcceptInvitationCommand): AcceptInvitationResult {
        val member = loadMemberPort.loadByTeamAndAccount(command.teamId, command.accountId)

        if (member.status != MemberStatus.INVITE) {
            throw MemberException(MemberExceptionCode.MEMBER_NOT_INVITED)
        }

        return saveMemberPort.activate(member.id)
            .toAcceptInvitationResult()
    }

    @Transactional
    override fun leave(command: LeaveTeamCommand): LeaveTeamResult {
        val member = loadMemberPort.loadByTeamAndAccount(command.teamId, command.accountId)
        validateNotLastAdmin(member)
        deleteMemberPort.delete(member.id)

        return member.toLeaveTeamResult()
    }

    @Transactional
    override fun updateRole(command: UpdateMemberRoleCommand): UpdateMemberRoleResult {
        validateTeamAdmin(command.accountId, command.teamId)

        val member = findMemberInTeam(command.memberId, command.teamId)

        if (command.role != MemberRole.ADMIN) {
            validateNotLastAdmin(member)
        }

        return saveMemberPort.updateRole(member.id, command.role)
            .toUpdateMemberRoleResult()
    }

    @Transactional
    override fun removeMember(command: RemoveMemberCommand): RemoveMemberResult {
        validateTeamAdmin(command.accountId, command.teamId)

        val member = findMemberInTeam(command.memberId, command.teamId)

        if (member.accountId == command.accountId) {
            throw MemberException(MemberExceptionCode.CANNOT_REMOVE_SELF)
        }

        validateNotLastAdmin(member)
        deleteMemberPort.delete(member.id)

        return member.toRemoveMemberResult()
    }

    private fun findMemberInTeam(memberId: Long, teamId: Long): Member {
        val member = loadMemberPort.load(memberId)

        if (member.teamId != teamId) {
            throw MemberException(MemberExceptionCode.MEMBER_NOT_FOUND)
        }

        return member
    }

    private fun validateTeamMember(accountId: Long, teamId: Long) {
        if (!checkTeamMemberPort.isMember(accountId, teamId)) {
            throw TeamException(TeamExceptionCode.NOT_TEAM_MEMBER)
        }
    }

    private fun validateTeamAdmin(accountId: Long, teamId: Long) {
        if (!checkTeamMemberPort.isAdminMember(accountId, teamId)) {
            throw TeamException(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER)
        }
    }

    private fun validateNotLastAdmin(member: Member) {
        if (member.role == MemberRole.ADMIN &&
            loadMemberPort.countByTeamAndRole(member.teamId, MemberRole.ADMIN) <= 1
        ) {
            throw MemberException(MemberExceptionCode.CANNOT_REMOVE_LAST_ADMIN)
        }
    }
}
