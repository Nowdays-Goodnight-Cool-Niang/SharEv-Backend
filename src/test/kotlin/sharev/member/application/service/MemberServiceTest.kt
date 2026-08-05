package sharev.member.application.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import sharev.member.application.port.inbound.command.*
import sharev.member.application.port.outbound.*
import sharev.member.domain.exception.MemberException
import sharev.member.domain.exception.MemberExceptionCode
import sharev.member.domain.model.Member
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.application.port.outbound.LoadTeamPort
import sharev.team.application.port.outbound.TeamAccessPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode

class MemberServiceTest {
    private val loadTeamPort = mock(LoadTeamPort::class.java)
    private val loadMemberPort = mock(LoadMemberPort::class.java)
    private val saveMemberPort = mock(SaveMemberPort::class.java)
    private val deleteMemberPort = mock(DeleteMemberPort::class.java)
    private val loadAccountForMemberPort = mock(LoadAccountForMemberPort::class.java)
    private val teamAccessPort = mock(TeamAccessPort::class.java)
    private val checkMemberPort = mock(CheckMemberPort::class.java)

    private val memberService = MemberService(
        loadTeamPort,
        loadMemberPort,
        saveMemberPort,
        deleteMemberPort,
        loadAccountForMemberPort,
        teamAccessPort,
        checkMemberPort,
    )

    // ───────────── isAdmin ─────────────

    @Test
    @DisplayName("팀이 존재하지 않으면 isAdmin은 false를 반환한다")
    fun isAdmin_returnsFalse_whenTeamNotExists() {
        val teamId = 1L
        val accountId = 10L

        given(loadTeamPort.exists(teamId)).willReturn(false)

        val result = memberService.isAdmin(teamId, accountId)

        assertThat(result).isFalse()
        then(teamAccessPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("admin 멤버이면 isAdmin은 true를 반환한다")
    fun isAdmin_returnsTrue_whenAdminMember() {
        val teamId = 1L
        val accountId = 10L

        given(loadTeamPort.exists(teamId)).willReturn(true)
        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)

        val result = memberService.isAdmin(teamId, accountId)

        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("admin이 아니면 isAdmin은 false를 반환한다")
    fun isAdmin_returnsFalse_whenNotAdmin() {
        val teamId = 1L
        val accountId = 10L

        given(loadTeamPort.exists(teamId)).willReturn(true)
        given(teamAccessPort.canManage(accountId, teamId)).willReturn(false)

        val result = memberService.isAdmin(teamId, accountId)

        assertThat(result).isFalse()
    }

    // ───────────── getMembers ─────────────

    @Test
    @DisplayName("팀 멤버가 아니면 getMembers 시 NOT_TEAM_MEMBER 예외가 발생한다")
    fun getMembers_throwsException_whenNotTeamMember() {
        val accountId = 10L
        val teamId = 1L
        val command = GetMembersCommand(accountId = accountId, teamId = teamId)

        given(teamAccessPort.hasAccess(accountId, teamId)).willReturn(false)

        assertThatThrownBy { memberService.getMembers(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_ACCESS.name)
            })

        then(loadMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("정상 조회 시 getMembers는 멤버 목록을 반환한다")
    fun getMembers_returnsMemberList() {
        val accountId = 10L
        val teamId = 1L
        val command = GetMembersCommand(accountId = accountId, teamId = teamId)
        val members = listOf(
            member(id = 1L, teamId = teamId, accountId = accountId),
            member(id = 2L, teamId = teamId, accountId = 20L),
        )

        given(teamAccessPort.hasAccess(accountId, teamId)).willReturn(true)
        given(loadMemberPort.loadAllByTeam(teamId)).willReturn(members)

        val result = memberService.getMembers(command)

        assertThat(result).hasSize(2)
        assertThat(result[0].memberId).isEqualTo(1L)
        assertThat(result[1].memberId).isEqualTo(2L)
    }

    // ───────────── invite ─────────────

    @Test
    @DisplayName("admin이 아니면 invite 시 NOT_TEAM_ADMIN_MEMBER 예외가 발생한다")
    fun invite_throwsException_whenNotAdmin() {
        val accountId = 10L
        val teamId = 1L
        val command = InviteMemberCommand(accountId = accountId, teamId = teamId, handle = "target")

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(false)

        assertThatThrownBy { memberService.invite(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE.name)
            })

        then(saveMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("이미 팀 멤버이면 invite 시 MEMBER_ALREADY_EXISTS 예외가 발생한다")
    fun invite_throwsException_whenMemberAlreadyExists() {
        val accountId = 10L
        val teamId = 1L
        val targetAccountId = 20L
        val handle = "target"
        val command = InviteMemberCommand(accountId = accountId, teamId = teamId, handle = handle)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadAccountForMemberPort.loadAccountIdByHandle(handle)).willReturn(targetAccountId)
        given(checkMemberPort.isMember(targetAccountId, teamId)).willReturn(true)

        assertThatThrownBy { memberService.invite(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.MEMBER_ALREADY_EXISTS.name)
            })

        then(saveMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("정상 초대 시 INVITE 상태로 멤버를 저장한다")
    fun invite_savesMemberWithInviteStatus() {
        val accountId = 10L
        val teamId = 1L
        val targetAccountId = 20L
        val handle = "target"
        val command = InviteMemberCommand(accountId = accountId, teamId = teamId, handle = handle)
        val savedMember = member(
            id = 99L,
            teamId = teamId,
            accountId = targetAccountId,
            status = MemberStatus.INVITE,
            role = MemberRole.COMMON
        )

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadAccountForMemberPort.loadAccountIdByHandle(handle)).willReturn(targetAccountId)
        given(teamAccessPort.hasAccess(targetAccountId, teamId)).willReturn(false)
        given(saveMemberPort.save(teamId, targetAccountId, MemberStatus.INVITE, MemberRole.COMMON)).willReturn(
            savedMember
        )

        val result = memberService.invite(command)

        assertThat(result.memberId).isEqualTo(99L)
        assertThat(result.status).isEqualTo(MemberStatus.INVITE)
        assertThat(result.role).isEqualTo(MemberRole.COMMON)
    }

    // ───────────── acceptInvitation ─────────────

    @Test
    @DisplayName("초대 상태가 아니면 acceptInvitation 시 MEMBER_NOT_INVITED 예외가 발생한다")
    fun acceptInvitation_throwsException_whenNotInvited() {
        val accountId = 10L
        val teamId = 1L
        val command = AcceptInvitationCommand(accountId = accountId, teamId = teamId)
        val activeMember = member(id = 1L, teamId = teamId, accountId = accountId, status = MemberStatus.ACTIVATE)

        given(loadMemberPort.loadByTeamAndAccount(teamId, accountId)).willReturn(activeMember)

        assertThatThrownBy { memberService.acceptInvitation(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.MEMBER_NOT_INVITED.name)
            })

        then(saveMemberPort).should(never()).activate(org.mockito.ArgumentMatchers.anyLong())
    }

    @Test
    @DisplayName("정상 수락 시 acceptInvitation은 activate를 호출한다")
    fun acceptInvitation_activatesMember() {
        val accountId = 10L
        val teamId = 1L
        val memberId = 1L
        val command = AcceptInvitationCommand(accountId = accountId, teamId = teamId)
        val invitedMember = member(id = memberId, teamId = teamId, accountId = accountId, status = MemberStatus.INVITE)
        val activatedMember =
            member(id = memberId, teamId = teamId, accountId = accountId, status = MemberStatus.ACTIVATE)

        given(loadMemberPort.loadByTeamAndAccount(teamId, accountId)).willReturn(invitedMember)
        given(saveMemberPort.activate(memberId)).willReturn(activatedMember)

        val result = memberService.acceptInvitation(command)

        assertThat(result.memberId).isEqualTo(memberId)
        assertThat(result.status).isEqualTo(MemberStatus.ACTIVATE)
        then(saveMemberPort).should().activate(memberId)
    }

    // ───────────── leave ─────────────

    @Test
    @DisplayName("마지막 admin이면 leave 시 CANNOT_REMOVE_LAST_ADMIN 예외가 발생한다")
    fun leave_throwsException_whenLastAdmin() {
        val accountId = 10L
        val teamId = 1L
        val memberId = 1L
        val command = LeaveTeamCommand(accountId = accountId, teamId = teamId)
        val adminMember = member(id = memberId, teamId = teamId, accountId = accountId, role = MemberRole.ADMIN)

        given(loadMemberPort.loadByTeamAndAccount(teamId, accountId)).willReturn(adminMember)
        given(loadMemberPort.countByTeamAndRole(teamId, MemberRole.ADMIN)).willReturn(1L)

        assertThatThrownBy { memberService.leave(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.CANNOT_REMOVE_LAST_ADMIN.name)
            })

        then(deleteMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("정상 탈퇴 시 leave는 delete를 호출한다")
    fun leave_deletesMember() {
        val accountId = 10L
        val teamId = 1L
        val memberId = 1L
        val command = LeaveTeamCommand(accountId = accountId, teamId = teamId)
        val commonMember = member(id = memberId, teamId = teamId, accountId = accountId, role = MemberRole.COMMON)

        given(loadMemberPort.loadByTeamAndAccount(teamId, accountId)).willReturn(commonMember)

        val result = memberService.leave(command)

        assertThat(result.memberId).isEqualTo(memberId)
        then(deleteMemberPort).should().delete(memberId)
    }

    @Test
    @DisplayName("admin이 여러 명이면 leave 시 정상 탈퇴된다")
    fun leave_succeeds_whenMultipleAdminsExist() {
        val accountId = 10L
        val teamId = 1L
        val memberId = 1L
        val command = LeaveTeamCommand(accountId = accountId, teamId = teamId)
        val adminMember = member(id = memberId, teamId = teamId, accountId = accountId, role = MemberRole.ADMIN)

        given(loadMemberPort.loadByTeamAndAccount(teamId, accountId)).willReturn(adminMember)
        given(loadMemberPort.countByTeamAndRole(teamId, MemberRole.ADMIN)).willReturn(2L)

        val result = memberService.leave(command)

        assertThat(result.memberId).isEqualTo(memberId)
        then(deleteMemberPort).should().delete(memberId)
    }

    // ───────────── updateRole ─────────────

    @Test
    @DisplayName("admin이 아니면 updateRole 시 NOT_TEAM_ADMIN_MEMBER 예외가 발생한다")
    fun updateRole_throwsException_whenNotAdmin() {
        val accountId = 10L
        val teamId = 1L
        val command =
            UpdateMemberRoleCommand(accountId = accountId, teamId = teamId, memberId = 2L, role = MemberRole.ADMIN)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(false)

        assertThatThrownBy { memberService.updateRole(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE.name)
            })

        then(saveMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("다른 팀의 멤버를 변경하려 하면 updateRole 시 MEMBER_NOT_FOUND 예외가 발생한다")
    fun updateRole_throwsException_whenMemberInDifferentTeam() {
        val accountId = 10L
        val teamId = 1L
        val otherTeamId = 99L
        val targetMemberId = 2L
        val command = UpdateMemberRoleCommand(
            accountId = accountId,
            teamId = teamId,
            memberId = targetMemberId,
            role = MemberRole.COMMON
        )
        val memberInOtherTeam =
            member(id = targetMemberId, teamId = otherTeamId, accountId = 20L, role = MemberRole.ADMIN)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(targetMemberId)).willReturn(memberInOtherTeam)

        assertThatThrownBy { memberService.updateRole(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.MEMBER_NOT_FOUND.name)
            })

        then(saveMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("마지막 admin을 COMMON으로 강등하면 updateRole 시 CANNOT_REMOVE_LAST_ADMIN 예외가 발생한다")
    fun updateRole_throwsException_whenDemotingLastAdmin() {
        val accountId = 10L
        val teamId = 1L
        val targetMemberId = 2L
        val command = UpdateMemberRoleCommand(
            accountId = accountId,
            teamId = teamId,
            memberId = targetMemberId,
            role = MemberRole.COMMON
        )
        val lastAdminMember = member(id = targetMemberId, teamId = teamId, accountId = 20L, role = MemberRole.ADMIN)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(targetMemberId)).willReturn(lastAdminMember)
        given(loadMemberPort.countByTeamAndRole(teamId, MemberRole.ADMIN)).willReturn(1L)

        assertThatThrownBy { memberService.updateRole(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.CANNOT_REMOVE_LAST_ADMIN.name)
            })

        then(saveMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("admin이 여러 명이면 COMMON으로 강등할 수 있다")
    fun updateRole_demotesToCommon_whenMultipleAdminsExist() {
        val accountId = 10L
        val teamId = 1L
        val targetMemberId = 2L
        val newRole = MemberRole.COMMON
        val command =
            UpdateMemberRoleCommand(accountId = accountId, teamId = teamId, memberId = targetMemberId, role = newRole)
        val targetMember = member(id = targetMemberId, teamId = teamId, accountId = 20L, role = MemberRole.ADMIN)
        val updatedMember = member(id = targetMemberId, teamId = teamId, accountId = 20L, role = newRole)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(targetMemberId)).willReturn(targetMember)
        given(loadMemberPort.countByTeamAndRole(teamId, MemberRole.ADMIN)).willReturn(2L)
        given(saveMemberPort.updateRole(targetMemberId, newRole)).willReturn(updatedMember)

        val result = memberService.updateRole(command)

        assertThat(result.memberId).isEqualTo(targetMemberId)
        assertThat(result.role).isEqualTo(newRole)
        then(saveMemberPort).should().updateRole(targetMemberId, newRole)
    }

    @Test
    @DisplayName("정상 역할 변경 시 updateRole은 updateRole을 호출한다")
    fun updateRole_updatesRole() {
        val accountId = 10L
        val teamId = 1L
        val targetMemberId = 2L
        val newRole = MemberRole.ADMIN
        val command =
            UpdateMemberRoleCommand(accountId = accountId, teamId = teamId, memberId = targetMemberId, role = newRole)
        val targetMember = member(id = targetMemberId, teamId = teamId, accountId = 20L, role = MemberRole.COMMON)
        val updatedMember = member(id = targetMemberId, teamId = teamId, accountId = 20L, role = newRole)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(targetMemberId)).willReturn(targetMember)
        given(saveMemberPort.updateRole(targetMemberId, newRole)).willReturn(updatedMember)

        val result = memberService.updateRole(command)

        assertThat(result.memberId).isEqualTo(targetMemberId)
        assertThat(result.role).isEqualTo(newRole)
        then(saveMemberPort).should().updateRole(targetMemberId, newRole)
    }

    // ───────────── removeMember ─────────────

    @Test
    @DisplayName("admin이 아니면 removeMember 시 NOT_TEAM_ADMIN_MEMBER 예외가 발생한다")
    fun removeMember_throwsException_whenNotAdmin() {
        val accountId = 10L
        val teamId = 1L
        val command = RemoveMemberCommand(accountId = accountId, teamId = teamId, memberId = 2L)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(false)

        assertThatThrownBy { memberService.removeMember(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE.name)
            })

        then(deleteMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("자기 자신을 제거하려 하면 removeMember 시 CANNOT_REMOVE_SELF 예외가 발생한다")
    fun removeMember_throwsException_whenRemovingSelf() {
        val accountId = 10L
        val teamId = 1L
        val memberId = 1L
        val command = RemoveMemberCommand(accountId = accountId, teamId = teamId, memberId = memberId)
        val selfMember = member(id = memberId, teamId = teamId, accountId = accountId)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(memberId)).willReturn(selfMember)

        assertThatThrownBy { memberService.removeMember(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.CANNOT_REMOVE_SELF.name)
            })

        then(deleteMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("마지막 admin을 제거하려 하면 removeMember 시 CANNOT_REMOVE_LAST_ADMIN 예외가 발생한다")
    fun removeMember_throwsException_whenRemovingLastAdmin() {
        val accountId = 10L
        val teamId = 1L
        val targetMemberId = 2L
        val command = RemoveMemberCommand(accountId = accountId, teamId = teamId, memberId = targetMemberId)
        val lastAdminMember = member(id = targetMemberId, teamId = teamId, accountId = 20L, role = MemberRole.ADMIN)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(targetMemberId)).willReturn(lastAdminMember)
        given(loadMemberPort.countByTeamAndRole(teamId, MemberRole.ADMIN)).willReturn(1L)

        assertThatThrownBy { memberService.removeMember(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.CANNOT_REMOVE_LAST_ADMIN.name)
            })

        then(deleteMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("다른 팀의 멤버를 제거하려 하면 removeMember 시 MEMBER_NOT_FOUND 예외가 발생한다")
    fun removeMember_throwsException_whenMemberInDifferentTeam() {
        val accountId = 10L
        val teamId = 1L
        val otherTeamId = 99L
        val targetMemberId = 2L
        val command = RemoveMemberCommand(accountId = accountId, teamId = teamId, memberId = targetMemberId)
        val memberInOtherTeam = member(id = targetMemberId, teamId = otherTeamId, accountId = 20L)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(targetMemberId)).willReturn(memberInOtherTeam)

        assertThatThrownBy { memberService.removeMember(command) }
            .isInstanceOf(MemberException::class.java)
            .satisfies({ ex ->
                val memberEx = ex as MemberException
                assertThat(memberEx.details.code).isEqualTo(MemberExceptionCode.MEMBER_NOT_FOUND.name)
            })

        then(deleteMemberPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("정상 제거 시 removeMember는 delete를 호출한다")
    fun removeMember_deletesMember() {
        val accountId = 10L
        val teamId = 1L
        val targetMemberId = 2L
        val command = RemoveMemberCommand(accountId = accountId, teamId = teamId, memberId = targetMemberId)
        val targetMember = member(id = targetMemberId, teamId = teamId, accountId = 20L, role = MemberRole.COMMON)

        given(teamAccessPort.canManage(accountId, teamId)).willReturn(true)
        given(loadMemberPort.load(targetMemberId)).willReturn(targetMember)

        val result = memberService.removeMember(command)

        assertThat(result.memberId).isEqualTo(targetMemberId)
        then(deleteMemberPort).should().delete(targetMemberId)
    }

    // ───────────── helpers ─────────────

    private fun member(
        id: Long,
        teamId: Long,
        accountId: Long,
        status: MemberStatus = MemberStatus.ACTIVATE,
        role: MemberRole = MemberRole.COMMON,
    ) = Member(
        id = id,
        teamId = teamId,
        accountId = accountId,
        accountName = "name-$accountId",
        accountEmail = "account$accountId@test.com",
        status = status,
        role = role,
    )
}
