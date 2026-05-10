package sharev.gathering.application.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.outbound.CheckGatheringParticipantPort
import sharev.gathering.application.port.outbound.LoadGatheringPort
import sharev.gathering.application.port.outbound.LoadIntroduceTemplatePort
import sharev.gathering.application.port.outbound.SaveGatheringPort
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.IntroduceTemplate
import sharev.gathering.domain.model.IntroduceTemplateContent
import sharev.team.application.port.outbound.CheckTeamMemberPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.time.LocalDateTime
import java.util.UUID

class GatheringParticipantServiceTest {
    private val checkGatheringParticipantPort = mock(CheckGatheringParticipantPort::class.java)
    private val saveGatheringPort = mock(SaveGatheringPort::class.java)
    private val loadGatheringPort = mock(LoadGatheringPort::class.java)
    private val loadIntroduceTemplatePort = mock(LoadIntroduceTemplatePort::class.java)
    private val checkTeamMemberPort = mock(CheckTeamMemberPort::class.java)

    private val gatheringParticipantService = GatheringParticipantService(
        checkGatheringParticipantPort,
        saveGatheringPort,
        loadGatheringPort,
        loadIntroduceTemplatePort,
        checkTeamMemberPort,
    )

    // ───────────── create ─────────────

    @Test
    @DisplayName("admin이 아니면 create 시 NOT_TEAM_ADMIN_MEMBER 예외가 발생한다")
    fun create_throwsException_whenNotAdmin() {
        val command = createGatheringCommand()

        given(checkTeamMemberPort.isAdminMember(command.accountId, command.teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.create(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER.name)
            })

        then(saveGatheringPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("admin이면 create 시 gathering을 저장하고 결과를 반환한다")
    fun create_savesGathering_whenAdmin() {
        val command = createGatheringCommand()
        val savedGathering = gathering(id = UUID.randomUUID(), command = command)

        given(checkTeamMemberPort.isAdminMember(command.accountId, command.teamId)).willReturn(true)
        given(saveGatheringPort.save(gathering(Gathering.NEW_ID, command))).willReturn(savedGathering)

        val result = gatheringParticipantService.create(command)

        assertThat(result.id).isEqualTo(savedGathering.id)
        assertThat(result.title).isEqualTo(command.title)
        then(saveGatheringPort).should().save(gathering(Gathering.NEW_ID, command))
    }

    // ───────────── getGatherings ─────────────

    @Test
    @DisplayName("팀 멤버가 아니면 getGatherings 시 NOT_TEAM_MEMBER 예외가 발생한다")
    fun getGatherings_throwsException_whenNotTeamMember() {
        val accountId = 1L
        val teamId = 2L

        given(checkTeamMemberPort.isMember(accountId, teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.getGatherings(accountId, teamId) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.NOT_TEAM_MEMBER.name)
            })

        then(loadGatheringPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("팀 멤버이면 getGatherings 시 행사 목록을 반환한다")
    fun getGatherings_returnsGatheringList_whenTeamMember() {
        val accountId = 1L
        val teamId = 2L
        val gatheringList = listOf(
            gathering(id = UUID.randomUUID(), teamId = teamId, title = "행사1"),
            gathering(id = UUID.randomUUID(), teamId = teamId, title = "행사2"),
        )

        given(checkTeamMemberPort.isMember(accountId, teamId)).willReturn(true)
        given(loadGatheringPort.loadAllByTeam(teamId)).willReturn(gatheringList)

        val result = gatheringParticipantService.getGatherings(accountId, teamId)

        assertThat(result).hasSize(2)
        assertThat(result[0].title).isEqualTo("행사1")
        assertThat(result[1].title).isEqualTo("행사2")
    }

    // ───────────── getGathering (getDetail) ─────────────

    @Test
    @DisplayName("팀 멤버가 아니면 getGathering 시 NOT_TEAM_MEMBER 예외가 발생한다")
    fun getGathering_throwsException_whenNotTeamMember() {
        val accountId = 1L
        val teamId = 2L
        val gatheringId = UUID.randomUUID()

        given(checkTeamMemberPort.isMember(accountId, teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.getGathering(accountId, teamId, gatheringId) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.NOT_TEAM_MEMBER.name)
            })

        then(loadGatheringPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("gathering이 다른 팀에 속하면 getGathering 시 GATHERING_NOT_FOUND 예외가 발생한다")
    fun getGathering_throwsException_whenGatheringInDifferentTeam() {
        val accountId = 1L
        val teamId = 2L
        val otherTeamId = 99L
        val gatheringId = UUID.randomUUID()
        val gatheringInOtherTeam = gathering(gatheringId, teamId = otherTeamId)

        given(checkTeamMemberPort.isMember(accountId, teamId)).willReturn(true)
        given(loadGatheringPort.load(gatheringId)).willReturn(gatheringInOtherTeam)

        assertThatThrownBy { gatheringParticipantService.getGathering(accountId, teamId, gatheringId) }
            .isInstanceOf(GatheringException::class.java)
            .satisfies({ ex ->
                val gatheringEx = ex as GatheringException
                assertThat(gatheringEx.details.code).isEqualTo(GatheringExceptionCode.GATHERING_NOT_FOUND.name)
            })
    }

    @Test
    @DisplayName("정상 조회 시 getGathering은 gathering 상세 정보를 반환한다")
    fun getGathering_returnsDetail() {
        val accountId = 1L
        val teamId = 2L
        val gatheringId = UUID.randomUUID()
        val existingGathering = gathering(gatheringId, teamId = teamId)

        given(checkTeamMemberPort.isMember(accountId, teamId)).willReturn(true)
        given(loadGatheringPort.load(gatheringId)).willReturn(existingGathering)

        val result = gatheringParticipantService.getGathering(accountId, teamId, gatheringId)

        assertThat(result.id).isEqualTo(gatheringId)
        assertThat(result.title).isEqualTo(existingGathering.title)
    }

    // ───────────── update ─────────────

    @Test
    @DisplayName("admin이 아니면 update 시 NOT_TEAM_ADMIN_MEMBER 예외가 발생한다")
    fun update_throwsException_whenNotAdmin() {
        val command = updateGatheringCommand()

        given(checkTeamMemberPort.isAdminMember(command.accountId, command.teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.update(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER.name)
            })

        then(saveGatheringPort).should(never()).update(any())
    }

    @Test
    @DisplayName("admin이면 update 시 gathering을 수정하고 결과를 반환한다")
    fun update_updatesGathering_whenAdmin() {
        val command = updateGatheringCommand()
        val updatedGathering = gathering(command.gatheringId, teamId = command.teamId, title = command.title)
        val captor = argumentCaptor<Gathering>()

        given(checkTeamMemberPort.isAdminMember(command.accountId, command.teamId)).willReturn(true)
        given(saveGatheringPort.update(any())).willReturn(updatedGathering)

        val result = gatheringParticipantService.update(command)

        then(saveGatheringPort).should().update(captor.capture())
        val captured = captor.firstValue
        assertThat(captured.id).isEqualTo(command.gatheringId)
        assertThat(captured.teamId).isEqualTo(command.teamId)
        assertThat(captured.visible).isEqualTo(command.visible)
        assertThat(captured.title).isEqualTo(command.title)
        assertThat(captured.content).isEqualTo(command.content)
        assertThat(captured.startAt).isEqualTo(command.startAt)
        assertThat(captured.endAt).isEqualTo(command.endAt)
        assertThat(captured.place).isEqualTo(command.place)
        assertThat(captured.imageUrl).isEqualTo(command.imageUrl)
        assertThat(captured.gatheringUrl).isEqualTo(command.gatheringUrl)
        assertThat(captured.contact).isEqualTo(command.contact)
        assertThat(captured.registerStartAt).isEqualTo(command.registerStartAt)
        assertThat(captured.registerEndAt).isEqualTo(command.registerEndAt)
        assertThat(result.id).isEqualTo(command.gatheringId)
        assertThat(result.title).isEqualTo(command.title)
    }

    // ───────────── delete ─────────────

    @Test
    @DisplayName("admin이 아니면 delete 시 NOT_TEAM_ADMIN_MEMBER 예외가 발생한다")
    fun delete_throwsException_whenNotAdmin() {
        val accountId = 1L
        val teamId = 2L
        val gatheringId = UUID.randomUUID()

        given(checkTeamMemberPort.isAdminMember(accountId, teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.delete(accountId, teamId, gatheringId) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER.name)
            })

        then(saveGatheringPort).should(never()).softDelete(any())
    }

    @Test
    @DisplayName("다른 팀의 행사를 삭제하려 하면 GATHERING_NOT_FOUND 예외가 발생하고 softDelete를 호출하지 않는다")
    fun delete_throwsException_whenGatheringInDifferentTeam() {
        val accountId = 1L
        val teamId = 2L
        val otherTeamId = 99L
        val gatheringId = UUID.randomUUID()
        val gatheringInOtherTeam = gathering(gatheringId, teamId = otherTeamId)

        given(checkTeamMemberPort.isAdminMember(accountId, teamId)).willReturn(true)
        given(loadGatheringPort.load(gatheringId)).willReturn(gatheringInOtherTeam)

        assertThatThrownBy { gatheringParticipantService.delete(accountId, teamId, gatheringId) }
            .isInstanceOf(GatheringException::class.java)
            .satisfies({ ex ->
                val gatheringEx = ex as GatheringException
                assertThat(gatheringEx.details.code).isEqualTo(GatheringExceptionCode.GATHERING_NOT_FOUND.name)
            })

        then(saveGatheringPort).should(never()).softDelete(any())
    }

    @Test
    @DisplayName("admin이면 delete 시 softDelete를 호출하고 gatheringId를 반환한다")
    fun delete_softDeletesGathering_whenAdmin() {
        val accountId = 1L
        val teamId = 2L
        val gatheringId = UUID.randomUUID()
        val existingGathering = gathering(gatheringId, teamId = teamId)

        given(checkTeamMemberPort.isAdminMember(accountId, teamId)).willReturn(true)
        given(loadGatheringPort.load(gatheringId)).willReturn(existingGathering)

        val result = gatheringParticipantService.delete(accountId, teamId, gatheringId)

        assertThat(result.gatheringId).isEqualTo(gatheringId)
        then(saveGatheringPort).should().softDelete(gatheringId)
    }

    // ───────────── getLatestTemplate (getIntroduceTemplate) ─────────────

    @Test
    @DisplayName("참가자가 아니면 getLatestTemplate 시 GATHERING_PARTICIPANT_NOT_FOUND 예외가 발생한다")
    fun getLatestTemplate_throwsException_whenNotParticipant() {
        val gatheringId = UUID.randomUUID()
        val accountId = 1L

        given(checkGatheringParticipantPort.isParticipant(gatheringId, accountId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.getLatestTemplate(gatheringId, accountId) }
            .isInstanceOf(GatheringException::class.java)
            .satisfies({ ex ->
                val gatheringEx = ex as GatheringException
                assertThat(gatheringEx.details.code).isEqualTo(GatheringExceptionCode.GATHERING_PARTICIPANT_NOT_FOUND.name)
            })

        then(loadIntroduceTemplatePort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("참가자이면 getLatestTemplate 시 최신 템플릿을 반환한다")
    fun getLatestTemplate_returnsLatestTemplate_whenParticipant() {
        val gatheringId = UUID.randomUUID()
        val accountId = 1L
        val template = introduceTemplate(gatheringId = gatheringId)

        given(checkGatheringParticipantPort.isParticipant(gatheringId, accountId)).willReturn(true)
        given(loadIntroduceTemplatePort.loadLatest(gatheringId)).willReturn(template)

        val result = gatheringParticipantService.getLatestTemplate(gatheringId, accountId)

        assertThat(result.version).isEqualTo(template.version)
        assertThat(result.text).isEqualTo(template.content.text)
    }

    // ───────────── isParticipant ─────────────

    @Test
    @DisplayName("isParticipant는 참가 여부를 반환한다")
    fun isParticipant_returnsTrue_whenParticipant() {
        val accountId = 1L
        val gatheringId = UUID.randomUUID()

        given(checkGatheringParticipantPort.isParticipant(gatheringId, accountId)).willReturn(true)

        val result = gatheringParticipantService.isParticipant(accountId, gatheringId)

        assertThat(result.isParticipant).isTrue()
    }

    @Test
    @DisplayName("isParticipant는 비참가 시 false를 반환한다")
    fun isParticipant_returnsFalse_whenNotParticipant() {
        val accountId = 1L
        val gatheringId = UUID.randomUUID()

        given(checkGatheringParticipantPort.isParticipant(gatheringId, accountId)).willReturn(false)

        val result = gatheringParticipantService.isParticipant(accountId, gatheringId)

        assertThat(result.isParticipant).isFalse()
    }

    // ───────────── helpers ─────────────

    private fun createGatheringCommand() = CreateGatheringCommand(
        accountId = 1L,
        teamId = 2L,
        visible = GatheringVisible.PUBLIC,
        title = "title",
        content = "content",
        startAt = LocalDateTime.of(2026, 5, 10, 10, 0),
        endAt = LocalDateTime.of(2026, 5, 10, 12, 0),
        place = "place",
        imageUrl = null,
        gatheringUrl = "https://sharev.test/gathering",
        contact = "contact",
        registerStartAt = LocalDateTime.of(2026, 5, 1, 10, 0),
        registerEndAt = LocalDateTime.of(2026, 5, 9, 18, 0),
    )

    private fun updateGatheringCommand(
        gatheringId: UUID = UUID.randomUUID(),
    ) = UpdateGatheringCommand(
        accountId = 1L,
        teamId = 2L,
        gatheringId = gatheringId,
        visible = GatheringVisible.PUBLIC,
        title = "updated-title",
        content = "updated-content",
        startAt = LocalDateTime.of(2026, 6, 1, 10, 0),
        endAt = LocalDateTime.of(2026, 6, 1, 12, 0),
        place = "updated-place",
        imageUrl = null,
        gatheringUrl = "https://sharev.test/gathering/updated",
        contact = "updated-contact",
        registerStartAt = LocalDateTime.of(2026, 5, 20, 10, 0),
        registerEndAt = LocalDateTime.of(2026, 5, 31, 18, 0),
    )

    private fun gathering(
        id: UUID,
        command: CreateGatheringCommand,
    ) = Gathering(
        id = id,
        teamId = command.teamId,
        visible = command.visible,
        title = command.title,
        content = command.content,
        startAt = command.startAt,
        endAt = command.endAt,
        place = command.place,
        imageUrl = command.imageUrl,
        gatheringUrl = command.gatheringUrl,
        contact = command.contact,
        registerStartAt = command.registerStartAt,
        registerEndAt = command.registerEndAt,
    )

    private fun gathering(
        id: UUID,
        teamId: Long = 2L,
        title: String = "title",
    ) = Gathering(
        id = id,
        teamId = teamId,
        visible = GatheringVisible.PUBLIC,
        title = title,
        content = "content",
        startAt = LocalDateTime.of(2026, 5, 10, 10, 0),
        endAt = LocalDateTime.of(2026, 5, 10, 12, 0),
        place = "place",
        imageUrl = null,
        gatheringUrl = null,
        contact = null,
        registerStartAt = LocalDateTime.of(2026, 5, 1, 10, 0),
        registerEndAt = LocalDateTime.of(2026, 5, 9, 18, 0),
    )

    private fun introduceTemplate(
        id: Long = 1L,
        gatheringId: UUID = UUID.randomUUID(),
        version: Int = 1,
    ) = IntroduceTemplate(
        id = id,
        gatheringId = gatheringId,
        version = version,
        content = IntroduceTemplateContent(
            text = "안녕하세요. 저는 홍길동입니다.",
            fieldPlaceholders = emptyMap(),
        ),
    )
}
