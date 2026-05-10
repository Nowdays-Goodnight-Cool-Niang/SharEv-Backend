package sharev.team.application.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.command.UpdateTeamInfoCommand
import sharev.team.application.port.outbound.CheckTeamMemberPort
import sharev.team.application.port.outbound.LoadGatheringSummaryPort
import sharev.team.application.port.outbound.LoadTeamPort
import sharev.team.application.port.outbound.QueryTeamPort
import sharev.team.application.port.outbound.SaveTeamAdminMemberPort
import sharev.team.application.port.outbound.SaveTeamPort
import sharev.team.application.port.outbound.summary.GatheringSummary
import sharev.team.application.port.outbound.summary.TeamMemberSummary
import sharev.team.application.port.outbound.summary.TeamSummary
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import sharev.team.domain.model.Team
import sharev.team.domain.model.TeamCertification
import java.time.LocalDateTime

class TeamServiceTest {
    private val saveTeamPort = mock(SaveTeamPort::class.java)
    private val loadTeamPort = mock(LoadTeamPort::class.java)
    private val queryTeamPort = mock(QueryTeamPort::class.java)
    private val saveTeamAdminMemberPort = mock(SaveTeamAdminMemberPort::class.java)
    private val checkTeamMemberPort = mock(CheckTeamMemberPort::class.java)
    private val loadGatheringSummaryPort = mock(LoadGatheringSummaryPort::class.java)

    private val teamService = TeamService(
        saveTeamPort,
        loadTeamPort,
        queryTeamPort,
        saveTeamAdminMemberPort,
        checkTeamMemberPort,
        loadGatheringSummaryPort,
    )

    // ───────────── create ─────────────

    @Test
    @DisplayName("팀 생성 시 팀을 저장하고 admin 멤버를 자동 생성한다")
    fun create_savesTeamAndCreatesAdminMember() {
        val accountId = 10L
        val command = CreateTeamCommand(accountId = accountId, title = "새 팀")
        val savedTeam = team(id = 1L, title = command.title)

        given(saveTeamPort.save(command.title)).willReturn(savedTeam)

        val result = teamService.create(command)

        assertThat(result.teamId).isEqualTo(savedTeam.id)
        then(saveTeamAdminMemberPort).should().saveTeamAdmin(savedTeam.id, accountId)
    }

    // ───────────── getMyTeams ─────────────

    @Test
    @DisplayName("getMyTeams는 내 팀 목록을 반환한다")
    fun getMyTeams_returnsTeamList() {
        val accountId = 10L
        val command = GetMyTeamsCommand(accountId = accountId)
        val summaries = listOf(
            teamSummary(id = 1L, title = "팀A"),
            teamSummary(id = 2L, title = "팀B"),
        )

        given(queryTeamPort.findMyTeams(accountId)).willReturn(summaries)

        val result = teamService.getMyTeams(command)

        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo(1L)
        assertThat(result[1].id).isEqualTo(2L)
        assertThat(result[0].title).isEqualTo("팀A")
    }

    // ───────────── getTeamDetail ─────────────

    @Test
    @DisplayName("팀 멤버가 아니면 getTeamDetail 시 NOT_TEAM_MEMBER 예외가 발생한다")
    fun getTeamDetail_throwsException_whenNotTeamMember() {
        val accountId = 10L
        val teamId = 1L

        given(checkTeamMemberPort.isMember(accountId, teamId)).willReturn(false)

        assertThatThrownBy { teamService.getTeamDetail(accountId, teamId) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.NOT_TEAM_MEMBER.name)
            })

        then(loadTeamPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("정상 조회 시 getTeamDetail은 gathering summaries, member summaries, headcount를 반환한다")
    fun getTeamDetail_returnsDetailWithGatheringsAndMembers() {
        val accountId = 10L
        val teamId = 1L
        val existingTeam = team(id = teamId, title = "팀A")
        val gatheringSummaries = listOf(
            gatheringSummary(title = "행사1"),
            gatheringSummary(title = "행사2"),
        )
        val memberSummaries = listOf(
            teamMemberSummary(name = "홍길동", email = "hong@test.com"),
            teamMemberSummary(name = "김철수", email = "kim@test.com"),
            teamMemberSummary(name = "이영희", email = "lee@test.com"),
        )

        given(checkTeamMemberPort.isMember(accountId, teamId)).willReturn(true)
        given(loadTeamPort.load(teamId)).willReturn(existingTeam)
        given(loadGatheringSummaryPort.loadByTeam(teamId)).willReturn(gatheringSummaries)
        given(queryTeamPort.findTeamMembers(teamId)).willReturn(memberSummaries)

        val result = teamService.getTeamDetail(accountId, teamId)

        assertThat(result.id).isEqualTo(teamId)
        assertThat(result.title).isEqualTo("팀A")
        assertThat(result.headcount).isEqualTo(3)
        assertThat(result.gatherings).hasSize(2)
        assertThat(result.gatherings[0].title).isEqualTo("행사1")
        assertThat(result.members).hasSize(3)
        assertThat(result.members[0].name).isEqualTo("홍길동")
    }

    // ───────────── updateTeamInfo ─────────────

    @Test
    @DisplayName("admin이 아니면 updateTeamInfo 시 NOT_TEAM_ADMIN_MEMBER 예외가 발생한다")
    fun updateTeamInfo_throwsException_whenNotAdmin() {
        val command = UpdateTeamInfoCommand(accountId = 10L, teamId = 1L, title = "새 제목")

        given(checkTeamMemberPort.isAdminMember(command.accountId, command.teamId)).willReturn(false)

        assertThatThrownBy { teamService.updateTeamInfo(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER.name)
            })

        then(saveTeamPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("admin이면 updateTeamInfo 시 팀 제목을 수정하고 결과를 반환한다")
    fun updateTeamInfo_updatesTitle_whenAdmin() {
        val newTitle = "수정된 팀 이름"
        val command = UpdateTeamInfoCommand(accountId = 10L, teamId = 1L, title = newTitle)
        val updatedTeam = team(id = command.teamId, title = newTitle)

        given(checkTeamMemberPort.isAdminMember(command.accountId, command.teamId)).willReturn(true)
        given(saveTeamPort.updateTitle(command.teamId, newTitle)).willReturn(updatedTeam)

        val result = teamService.updateTeamInfo(command)

        assertThat(result.title).isEqualTo(newTitle)
        then(saveTeamPort).should().updateTitle(command.teamId, newTitle)
    }

    // ───────────── helpers ─────────────

    private fun team(
        id: Long,
        title: String,
    ) = Team(
        id = id,
        teamCertification = TeamCertification.NONE,
        title = title,
        content = null,
        activateFlag = true,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
    )

    private fun teamSummary(
        id: Long,
        title: String,
    ) = TeamSummary(
        id = id,
        title = title,
        content = null,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        memberRole = "ADMIN",
        headcount = 1,
    )

    private fun gatheringSummary(
        title: String,
    ) = GatheringSummary(
        title = title,
        startAt = LocalDateTime.of(2026, 6, 1, 10, 0),
        endAt = LocalDateTime.of(2026, 6, 1, 12, 0),
        place = "서울",
    )

    private fun teamMemberSummary(
        name: String,
        email: String,
    ) = TeamMemberSummary(
        name = name,
        email = email,
    )
}
