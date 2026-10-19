package sharev.member.adapter.outbound.jooq

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jooq.test.autoconfigure.JooqTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import sharev.account.domain.model.AccountRole
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.domain.model.TeamCertification

@JooqTest
@Import(MemberJooqAdapter::class)
@Testcontainers
@TestPropertySource(
    properties = [
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:ddl.sql",
    ]
)
class MemberJooqAdapterTest @Autowired constructor(
    private val memberJooqAdapter: MemberJooqAdapter,
    private val dsl: DSLContext,
) {

    @Test
    fun `받은 초대 목록을 가져온다`() {

        // given
        val adminAccountId = createAccount("어드민", "admin@test.test", "test_admin")
        val myAccountId = createAccount("멤버1", "member1@test.test", "test_member_1")
        val activatedAccountId = createAccount("멤버2", "member2@test.test", "test_member_2")
        val invitedAccountId = createAccount("멤버3", "member3@test.test", "test_member_3")
        val deactivatedAccountId = createAccount("멤버4", "member4@test.test", "test_member_4")

        val firstTeamId = createTeam("1팀", "1팀 소개", TeamCertification.CERTIFICATED)
        createMember(firstTeamId, adminAccountId, MemberStatus.ACTIVATE, MemberRole.ADMIN)
        createMember(firstTeamId, activatedAccountId, MemberStatus.ACTIVATE)
        createMember(firstTeamId, invitedAccountId, MemberStatus.INVITE)
        createMember(firstTeamId, deactivatedAccountId, MemberStatus.DEACTIVATE)
        createMember(firstTeamId, myAccountId, MemberStatus.INVITE)

        val secondTeamId = createTeam("2팀")
        createMember(secondTeamId, adminAccountId, MemberStatus.ACTIVATE, MemberRole.ADMIN)
        createMember(secondTeamId, myAccountId, MemberStatus.INVITE)

        // when
        val invitations = memberJooqAdapter.loadAll(myAccountId, PageRequest.of(0, 10))

        // then
        assertThat(invitations.content.map { it.teamId }).containsExactly(secondTeamId, firstTeamId)

        val (secondInvitation, firstInvitation) = invitations.content

        assertThat(firstInvitation.title).isEqualTo("1팀")
        assertThat(firstInvitation.content).isEqualTo("1팀 소개")
        assertThat(firstInvitation.certification).isEqualTo(TeamCertification.CERTIFICATED)
        assertThat(firstInvitation.role).isEqualTo(MemberRole.COMMON)
        assertThat(firstInvitation.headcount).isEqualTo(2)

        assertThat(secondInvitation.title).isEqualTo("2팀")
        assertThat(secondInvitation.content).isNull()
        assertThat(secondInvitation.certification).isEqualTo(TeamCertification.NONE)
        assertThat(secondInvitation.role).isEqualTo(MemberRole.COMMON)
        assertThat(secondInvitation.headcount).isEqualTo(1)
    }

    @Test
    fun `이미 가입했거나 탈퇴했거나 비활성화한 팀은 초대 목록과 전체 수에서 빠진다`() {

        // given
        val myAccountId = createAccount("멤버1", "member1@test.test", "test_member_1")

        val invitedTeamId = createTeam("초대받은 팀")
        createMember(invitedTeamId, myAccountId, MemberStatus.INVITE)

        val activatedTeamId = createTeam("가입한 팀")
        createMember(activatedTeamId, myAccountId, MemberStatus.ACTIVATE)

        val deactivatedTeamId = createTeam("비활성화한 팀")
        createMember(deactivatedTeamId, myAccountId, MemberStatus.DEACTIVATE)

        val deletedTeamId = createTeam("삭제된 팀")
        createMember(deletedTeamId, myAccountId, MemberStatus.INVITE)
        deleteTeam(deletedTeamId)

        // when
        val invitations = memberJooqAdapter.loadAll(myAccountId, PageRequest.of(0, 1))

        // then
        assertThat(invitations.content.map { it.teamId }).containsExactly(invitedTeamId)
        assertThat(invitations.totalElements).isEqualTo(1)
    }

    private fun createAccount(name: String, email: String, handle: String): Long {
        return dsl.fetchValue(
            "INSERT INTO accounts (role, name, email, handle) VALUES (?, ?, ?, ?) RETURNING account_id",
            AccountRole.USER.name,
            name,
            email,
            handle,
        ) as Long
    }

    private fun createTeam(
        title: String,
        content: String? = null,
        certification: TeamCertification = TeamCertification.NONE,
    ): Long {
        return dsl.fetchValue(
            "INSERT INTO teams (certification, title, content) VALUES (?, ?, ?) RETURNING team_id",
            certification.name,
            title,
            content,
        ) as Long
    }

    private fun deleteTeam(teamId: Long) {
        dsl.execute("UPDATE teams SET deleted_at = NOW() WHERE team_id = ?", teamId)
    }

    private fun createMember(
        teamId: Long,
        accountId: Long,
        status: MemberStatus,
        role: MemberRole = MemberRole.COMMON
    ): Long {
        return dsl.fetchValue(
            "INSERT INTO members (team_id, account_id, status, role) VALUES (?, ?, ?, ?) RETURNING member_id",
            teamId,
            accountId,
            status.name,
            role.name,
        ) as Long
    }

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")
    }
}
