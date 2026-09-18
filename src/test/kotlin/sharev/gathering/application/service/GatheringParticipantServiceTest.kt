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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.outbound.*
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.exception.GatheringExceptionCode
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.Introduction
import sharev.gathering.domain.model.Template
import sharev.team.application.port.outbound.TeamAccessPort
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.time.LocalDateTime
import java.util.*

class GatheringParticipantServiceTest {
    private val checkGatheringParticipantPort = mock(CheckGatheringParticipantPort::class.java)
    private val saveGatheringPort = mock(SaveGatheringPort::class.java)
    private val loadGatheringPort = mock(LoadGatheringPort::class.java)
    private val loadIntroductionPort = mock(LoadIntroductionPort::class.java)
    private val teamAccessPort = mock(TeamAccessPort::class.java)
    private val saveIntroductionPort = mock(SaveIntroductionPort::class.java)

    private val gatheringParticipantService = GatheringService(
        checkGatheringParticipantPort,
        saveGatheringPort,
        loadGatheringPort,
        loadIntroductionPort,
        teamAccessPort,
        saveIntroductionPort,
    )

    // ───────────── create ─────────────

    @Test
    @DisplayName("admin이 아니면 create 시 UNAUTHORIZED_TEAM_MANAGE 예외가 발생한다")
    fun create_throwsException_whenNotAdmin() {
        val command = createGatheringCommand()

        given(teamAccessPort.canManage(command.accountId, command.teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.create(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE.name)
            })

        then(saveGatheringPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("admin이면 create 시 gathering을 저장하고 결과를 반환한다")
    fun create_savesGathering_whenAdmin() {
        val command = createGatheringCommand()
        val savedGathering = gathering(id = UUID.randomUUID(), command = command)

        given(teamAccessPort.canManage(command.accountId, command.teamId)).willReturn(true)
        given(saveGatheringPort.save(gathering(Gathering.NEW_ID, command))).willReturn(savedGathering)

        val result = gatheringParticipantService.create(command)

        assertThat(result.id).isEqualTo(savedGathering.id)
        assertThat(result.title).isEqualTo(command.title)
        then(saveGatheringPort).should().save(gathering(Gathering.NEW_ID, command))
    }

    // ───────────── getGatherings (all) ─────────────

    @Test
    @DisplayName("getGatherings는 전체 행사 목록을 반환한다")
    fun getGatherings_returnsAllGatherings() {
        val pageable = PageRequest.of(0, 10)
        val command = getGatheringCommand()
        val summaries = listOf(
            gatheringDetailSummary(title = "행사1"),
            gatheringDetailSummary(title = "행사2"),
        )

        given(loadGatheringPort.loadAll(any(), any()))
            .willReturn(PageImpl(summaries, pageable, summaries.size.toLong()))

        val result = gatheringParticipantService.getGatherings(command, pageable)

        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.content[0].title).isEqualTo("행사1")
        assertThat(result.content[1].title).isEqualTo("행사2")
    }

    @Test
    @DisplayName("행사가 없으면 빈 목록을 반환한다")
    fun getGatherings_returnsEmptyList() {
        val pageable = PageRequest.of(0, 10)
        val command = getGatheringCommand()

        given(loadGatheringPort.loadAll(any(), any()))
            .willReturn(PageImpl(emptyList<GatheringDetailSummary>(), pageable, 0))

        val result = gatheringParticipantService.getGatherings(command, pageable)

        assertThat(result.content).isEmpty()
    }

    // ───────────── update ─────────────

    @Test
    @DisplayName("admin이 아니면 update 시 UNAUTHORIZED_TEAM_MANAGE 예외가 발생한다")
    fun update_throwsException_whenNotAdmin() {
        val command = updateGatheringCommand()
        val existing = gathering(command.gatheringId, teamId = 2L)

        given(loadGatheringPort.load(command.gatheringId)).willReturn(existing)
        given(teamAccessPort.canManage(command.accountId, existing.teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.update(command) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE.name)
            })

        then(saveGatheringPort).should(never()).update(any())
    }

    @Test
    @DisplayName("admin이면 update 시 gathering을 수정하고 결과를 반환한다")
    fun update_updatesGathering_whenAdmin() {
        val command = updateGatheringCommand()
        val existing = gathering(command.gatheringId, teamId = 2L)
        val updatedGathering = gathering(command.gatheringId, teamId = existing.teamId, title = command.title)
        val captor = argumentCaptor<Gathering>()

        given(loadGatheringPort.load(command.gatheringId)).willReturn(existing)
        given(teamAccessPort.canManage(command.accountId, existing.teamId)).willReturn(true)
        given(saveGatheringPort.update(any())).willReturn(updatedGathering)

        val result = gatheringParticipantService.update(command)

        then(saveGatheringPort).should().update(captor.capture())
        val captured = captor.firstValue
        assertThat(captured.id).isEqualTo(command.gatheringId)
        assertThat(captured.teamId).isEqualTo(existing.teamId)
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
    @DisplayName("admin이 아니면 delete 시 UNAUTHORIZED_TEAM_MANAGE 예외가 발생한다")
    fun delete_throwsException_whenNotAdmin() {
        val accountId = 1L
        val gatheringId = UUID.randomUUID()
        val existing = gathering(gatheringId, teamId = 2L)

        given(loadGatheringPort.load(gatheringId)).willReturn(existing)
        given(teamAccessPort.canManage(accountId, existing.teamId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.delete(accountId, gatheringId) }
            .isInstanceOf(TeamException::class.java)
            .satisfies({ ex ->
                val teamEx = ex as TeamException
                assertThat(teamEx.details.code).isEqualTo(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE.name)
            })

        then(saveGatheringPort).should(never()).softDelete(any())
    }

    @Test
    @DisplayName("admin이면 delete 시 softDelete를 호출하고 gatheringId를 반환한다")
    fun delete_softDeletesGathering_whenAdmin() {
        val accountId = 1L
        val gatheringId = UUID.randomUUID()
        val existing = gathering(gatheringId, teamId = 2L)

        given(loadGatheringPort.load(gatheringId)).willReturn(existing)
        given(teamAccessPort.canManage(accountId, existing.teamId)).willReturn(true)

        val result = gatheringParticipantService.delete(accountId, gatheringId)

        assertThat(result.gatheringId).isEqualTo(gatheringId)
        then(saveGatheringPort).should().softDelete(gatheringId)
    }

    // ───────────── getLatestIntroduction ─────────────

    @Test
    @DisplayName("참가자가 아니면 getLatestIntroduction 시 GATHERING_PARTICIPANT_NOT_FOUND 예외가 발생한다")
    fun getLatestIntroduction_throwsException_whenNotParticipant() {
        val gatheringId = UUID.randomUUID()
        val accountId = 1L

        given(checkGatheringParticipantPort.isParticipant(gatheringId, accountId)).willReturn(false)

        assertThatThrownBy { gatheringParticipantService.getLatestIntroduction(gatheringId, accountId) }
            .isInstanceOf(GatheringException::class.java)
            .satisfies({ ex ->
                val gatheringEx = ex as GatheringException
                assertThat(gatheringEx.details.code).isEqualTo(GatheringExceptionCode.GATHERING_PARTICIPANT_NOT_FOUND.name)
            })

        then(loadIntroductionPort).shouldHaveNoInteractions()
    }

    @Test
    @DisplayName("참가자이면 getLatestIntroduction 시 최신 소개 템플릿을 반환한다")
    fun getLatestIntroduction_returnsLatest_whenParticipant() {
        val gatheringId = UUID.randomUUID()
        val accountId = 1L
        val introduction = introduction(gatheringId = gatheringId)

        given(checkGatheringParticipantPort.isParticipant(gatheringId, accountId)).willReturn(true)
        given(loadIntroductionPort.loadLatestIntroduction(gatheringId)).willReturn(introduction)

        val result = gatheringParticipantService.getLatestIntroduction(gatheringId, accountId)

        assertThat(result.version).isEqualTo(introduction.version)
        assertThat(result.source).isEqualTo(introduction.template.source)
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

    private fun getGatheringCommand() = GetGatheringCommand(
        accountId = null,
        participated = null,
        teamId = null,
        visibility = null,
        progress = null,
        registration = null,
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

    private fun gatheringDetailSummary(
        id: UUID = UUID.randomUUID(),
        teamId: Long = 1L,
        title: String = "title",
    ) = GatheringDetailSummary(
        id = id,
        teamId = teamId,
        teamTitle = "team",
        ownerHandle = "owner",
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

    private fun introduction(
        id: Long = 1L,
        gatheringId: UUID = UUID.randomUUID(),
        version: Int = 1,
    ) = Introduction(
        id = id,
        gatheringId = gatheringId,
        version = version,
        template = Template(
            source = "안녕하세요. 저는 홍길동입니다.",
            fields = emptyMap(),
        ),
    )
}
