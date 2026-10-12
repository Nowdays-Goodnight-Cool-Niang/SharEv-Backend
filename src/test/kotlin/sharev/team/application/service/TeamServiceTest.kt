package sharev.team.application.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import sharev.member.domain.model.MemberRole
import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.command.UpdateTeamInfoCommand
import sharev.team.application.port.inbound.result.JoinedTeamResult
import sharev.team.application.port.outbound.*
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import sharev.team.domain.model.Team
import sharev.team.domain.model.TeamCertification
import java.time.LocalDateTime

class TeamServiceTest {
    private val saveTeamPort = mock(SaveTeamPort::class.java)
    private val loadTeamPort = mock(LoadTeamPort::class.java)
    private val saveTeamAdminPort = mock(SaveTeamAdminPort::class.java)
    private val teamAccessPort = mock(TeamAccessPort::class.java)
    private val teamGatheringPort = mock(TeamGatheringPort::class.java)
    private val teamMemberPort = mock(TeamMemberPort::class.java)
    private val loadJoinedTeamPort = mock(LoadJoinedTeamPort::class.java)

    private val teamService = TeamService(
        saveTeamPort,
        loadTeamPort,
        saveTeamAdminPort,
        teamAccessPort,
        teamGatheringPort,
        teamMemberPort,
        loadJoinedTeamPort,
    )

    @Test
    @DisplayName("팀 생성 시 팀을 저장하고 admin 멤버를 자동 생성한다")
    fun create_PublicTeam_savesTeamAndCreatesAdminMember() {
        val accountId = 10L
        val command = CreateTeamCommand(accountId, "새 팀", "설명")
        val savedTeam = team(1L, command.title, command.content)

        given(saveTeamPort.save(Team.create(command.title, command.content)))
            .willReturn(savedTeam)

        val result = teamService.create(command)

        assertThat(result.teamId).isEqualTo(savedTeam.id)
        then(saveTeamAdminPort).should().save(savedTeam.id, accountId)
    }

    @Test
    @DisplayName("getJoinedTeams는 내가 참여한 팀 목록을 반환한다")
    fun getMyTeams_returnsTeamList() {
        val accountId = 10L
        val command = GetMyTeamsCommand(accountId = accountId)
        val joinedTeams = PageImpl(
            listOf(
                JoinedTeam(1L, MemberRole.ADMIN.name),
                JoinedTeam(2L, MemberRole.COMMON.name),
            )
        )
        val teams = listOf(
            team(1L, "1팀", "1팀입니다."),
            team(2L, "2팀", "2팀입니다."),
        )
        val headcounts = mapOf(
            1L to 3L,
            2L to 5L,
        )

        given(loadJoinedTeamPort.load(anyLong(), any()))
            .willReturn(joinedTeams)
        given(loadTeamPort.loadAll(any()))
            .willReturn(teams)
        given(loadJoinedTeamPort.headcount(any()))
            .willReturn(headcounts)

        val result = teamService.getJoinedTeams(command, PageRequest.of(0, 20))

        assertThat(result.totalElements).isEqualTo(2)

        assertThat(result.content[0].id).isEqualTo(1L)
        assertThat(result.content[0].title).isEqualTo("1팀")

        assertThat(result.content[1].id).isEqualTo(2L)
        assertThat(result.content[1].title).isEqualTo("2팀")
    }

    @Test
    @DisplayName("팀 멤버가 아니면 getTeamDetail 시 UNAUTHORIZED_TEAM_ACCESS 예외가 발생한다")
    fun getTeamDetail_throwsException_whenNotTeamMember() {
        val accountId = 10L
        val teamId = 1L

        given(teamAccessPort.hasAccess(accountId, teamId)).willReturn(false)

        assertThatThrownBy { teamService.getTeamDetail(accountId, teamId) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_ACCESS.name)
            })

        then(loadTeamPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("정상 조회 시 getTeamDetail은 gathering summaries, member summaries, headcount를 반환한다")
    fun getTeamDetail_returnsDetailWithGatheringsAndMembers() {
        val accountId = 10L
        val teamId = 1L
        val existingTeam = team(teamId, "팀A", "내용")
        val gatheringSummaries = listOf(
            gatheringSummary(title = "행사1"),
            gatheringSummary(title = "행사2"),
        )
        val memberSummaries = listOf(
            teamMemberSummary(name = "홍길동", email = "hong@test.com", role = MemberRole.ADMIN),
            teamMemberSummary(name = "김철수", email = "kim@test.com", role = MemberRole.COMMON),
            teamMemberSummary(name = "이영희", email = "lee@test.com", role = MemberRole.COMMON),
        )

        given(teamAccessPort.hasAccess(accountId, teamId)).willReturn(true)
        given(loadTeamPort.load(teamId)).willReturn(existingTeam)
        given(teamGatheringPort.findGatheringsByTeamId(teamId)).willReturn(gatheringSummaries)
        given(teamMemberPort.findMembersByTeamId(teamId)).willReturn(memberSummaries)

        val result = teamService.getTeamDetail(accountId, teamId)

        assertThat(result.id).isEqualTo(teamId)
        assertThat(result.title).isEqualTo("팀A")
        assertThat(result.headcount).isEqualTo(3)
        assertThat(result.gatherings).hasSize(2)
        assertThat(result.gatherings[0].title).isEqualTo("행사1")
        assertThat(result.members).hasSize(3)
        assertThat(result.members[0].name).isEqualTo("홍길동")
        assertThat(result.members[0].role).isEqualTo(MemberRole.ADMIN.name)
        assertThat(result.members[1].role).isEqualTo(MemberRole.COMMON.name)
        assertThat(result.members[2].role).isEqualTo(MemberRole.COMMON.name)
    }

    @Test
    @DisplayName("admin이 아니면 updateTeamInfo 시 UNAUTHORIZED_TEAM_MANAGE 예외가 발생한다")
    fun updateInfo_throwsException_whenNotAdmin() {
        val command = UpdateTeamInfoCommand(10L, 1L, "새 제목", "내용")

        given(teamAccessPort.canManage(command.accountId, command.teamId))
            .willReturn(false)

        assertThatThrownBy { teamService.updateTeamInfo(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE.name)
            })

        then(saveTeamPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("admin이면 updateTeamInfo 시 팀 제목을 수정하고 결과를 반환한다")
    fun updateInfo_updatesTitle_whenAdmin() {
        val newTitle = "수정된 팀 이름"
        val newContent = "수정된 내용"
        val command = UpdateTeamInfoCommand(10L, 1L, newTitle, newContent)
        val beforeTeam = team(command.teamId, "수정 전 이름", "수정 전 내용")
        val updatedTeam = team(command.teamId, newTitle, newContent)

        given(teamAccessPort.canManage(command.accountId, command.teamId))
            .willReturn(true)
        given(loadTeamPort.load(command.teamId))
            .willReturn(beforeTeam)
        given(saveTeamPort.updateTitleAndContent(team(beforeTeam.id, newTitle, newContent)))
            .willReturn(updatedTeam)

        val result = teamService.updateTeamInfo(command)

        assertThat(result.title).isEqualTo(newTitle)
        then(saveTeamPort).should().updateTitleAndContent(any())
    }

    private fun team(
        id: Long,
        title: String,
        content: String?,
    ) = Team(
        id = id,
        title = title,
        content = content,
        certification = TeamCertification.NONE,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
    )

    private fun joinedTeamResult(
        id: Long,
        title: String,
    ) = JoinedTeamResult(
        id = id,
        title = title,
        content = null,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        memberRole = "ADMIN",
        headcount = 1,
    )

    private fun gatheringSummary(
        title: String,
    ) = TeamGatherings(
        title = title,
        startAt = LocalDateTime.of(2026, 6, 1, 10, 0),
        endAt = LocalDateTime.of(2026, 6, 1, 12, 0),
        place = "서울",
    )

    private fun teamMemberSummary(
        name: String,
        email: String,
        role: MemberRole = MemberRole.COMMON,
    ) = TeamMembers(
        name = name,
        role = role.name,
        email = email,
    )
}
