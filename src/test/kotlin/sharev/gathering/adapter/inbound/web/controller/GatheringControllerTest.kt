package sharev.gathering.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willThrow
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import sharev.ControllerTestSupport
import sharev.WithCustomMockUser
import sharev.gathering.adapter.inbound.web.dto.request.CreateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.request.UpdateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.response.CreateGatheringResponse
import sharev.gathering.adapter.inbound.web.dto.response.DeleteGatheringResponse
import sharev.gathering.adapter.inbound.web.dto.response.GatheringDetailResponse
import sharev.gathering.adapter.inbound.web.dto.response.IntroduceTemplateResponse
import sharev.gathering.adapter.inbound.web.dto.response.ParticipantResponse
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.result.CreateGatheringResult
import sharev.gathering.application.port.inbound.result.DeleteGatheringResult
import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import sharev.gathering.application.port.inbound.result.IntroduceTemplateResult
import sharev.gathering.application.port.inbound.result.ParticipantResult
import sharev.gathering.domain.model.GatheringVisible
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.time.LocalDateTime
import java.util.*

class GatheringControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("행사 참여 유무 확인")
    fun isParticipant() {
        val gatheringId = UUID.randomUUID()

        given(checkGatheringParticipantUseCase.isParticipant(1L, gatheringId)).willReturn(ParticipantResult(false))

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}", gatheringId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "isParticipant",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 참여 유무 확인")
                            .description("사용자가 특정 행사에 참여했는지 확인합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("확인할 행사의 ID (UUID 형식)"))
                            .responseFields(fieldWithPath("isParticipant").type(BOOLEAN).description("행사 참여 유무"))
                            .responseSchema(schema(ParticipantResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 생성")
    fun createGathering() {
        val teamId = 1L
        val gatheringId = UUID.randomUUID()
        val dto = CreateGatheringRequest(
            GatheringVisible.PUBLIC,
            "Spring 밋업",
            "Spring Boot 관련 밋업입니다.",
            LocalDateTime.of(2025, 3, 20, 14, 0),
            LocalDateTime.of(2025, 3, 20, 17, 0),
            "서울 강남구",
            "https://example.com/image.png",
            "https://example.com/gathering",
            "010-1234-5678",
            LocalDateTime.of(2025, 3, 1, 0, 0),
            LocalDateTime.of(2025, 3, 19, 23, 59),
        )

        given(
            createGatheringUseCase.create(
                CreateGatheringCommand(
                    1L,
                    teamId,
                    requireNotNull(dto.visible),
                    requireNotNull(dto.title),
                    requireNotNull(dto.content),
                    requireNotNull(dto.startAt),
                    requireNotNull(dto.endAt),
                    requireNotNull(dto.place),
                    dto.imageUrl,
                    dto.gatheringUrl,
                    dto.contact,
                    requireNotNull(dto.registerStartAt),
                    requireNotNull(dto.registerEndAt),
                )
            )
        ).willReturn(
            CreateGatheringResult(
                gatheringId,
                teamId,
                requireNotNull(dto.visible),
                requireNotNull(dto.title),
                requireNotNull(dto.content),
                requireNotNull(dto.startAt),
                requireNotNull(dto.endAt),
                requireNotNull(dto.place),
                dto.imageUrl,
                dto.gatheringUrl,
                dto.contact,
                requireNotNull(dto.registerStartAt),
                requireNotNull(dto.registerEndAt),
            )
        )

        val request = RestDocumentationRequestBuilders.post("/teams/{teamId}/gatherings", teamId)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(
                documentResource(
                    "createGathering",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 생성")
                            .description("새로운 행사를 생성합니다. 팀 관리자만 생성할 수 있습니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .requestFields(
                                fieldWithPath("visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
                                fieldWithPath("title").type(STRING).description("행사 제목"),
                                fieldWithPath("content").type(STRING).description("행사 설명"),
                                fieldWithPath("startAt").type(STRING).description("행사 시작일시"),
                                fieldWithPath("endAt").type(STRING).description("행사 종료일시"),
                                fieldWithPath("place").type(STRING).description("행사 장소"),
                                fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
                                fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
                                fieldWithPath("contact").type(STRING).description("연락처").optional(),
                                fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시"),
                                fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시"),
                            )
                            .requestSchema(schema(CreateGatheringRequest::class.java.simpleName))
                            .responseFields(
                                fieldWithPath("id").type(STRING).description("생성된 행사 ID"),
                                fieldWithPath("teamId").type(NUMBER).description("팀 ID"),
                                fieldWithPath("visible").type(STRING).description("공개 범위"),
                                fieldWithPath("title").type(STRING).description("행사 제목"),
                                fieldWithPath("content").type(STRING).description("행사 설명"),
                                fieldWithPath("startAt").type(STRING).description("행사 시작일시"),
                                fieldWithPath("endAt").type(STRING).description("행사 종료일시"),
                                fieldWithPath("place").type(STRING).description("행사 장소"),
                                fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
                                fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
                                fieldWithPath("contact").type(STRING).description("연락처").optional(),
                                fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시"),
                                fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시"),
                            )
                            .responseSchema(schema(CreateGatheringResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 생성 실패 - 권한 없음")
    fun createGatheringFail() {
        val teamId = 1L
        val dto = CreateGatheringRequest(
            GatheringVisible.PUBLIC,
            "Spring 밋업",
            "설명",
            LocalDateTime.of(2025, 3, 20, 14, 0),
            LocalDateTime.of(2025, 3, 20, 17, 0),
            "서울",
            null,
            null,
            null,
            LocalDateTime.of(2025, 3, 1, 0, 0),
            LocalDateTime.of(2025, 3, 19, 23, 59),
        )

        willThrow(TeamException(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER))
            .given(createGatheringUseCase)
            .create(
                CreateGatheringCommand(
                    1L,
                    teamId,
                    requireNotNull(dto.visible),
                    requireNotNull(dto.title),
                    requireNotNull(dto.content),
                    requireNotNull(dto.startAt),
                    requireNotNull(dto.endAt),
                    requireNotNull(dto.place),
                    dto.imageUrl,
                    dto.gatheringUrl,
                    dto.contact,
                    requireNotNull(dto.registerStartAt),
                    requireNotNull(dto.registerEndAt),
                )
            )

        val request = RestDocumentationRequestBuilders.post("/teams/{teamId}/gatherings", teamId)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀별 행사 목록 조회")
    fun getGatherings() {
        val teamId = 1L
        val response = listOf(gatheringResult(UUID.randomUUID()))

        given(getGatheringUseCase.getGatherings(1L, teamId)).willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/teams/{teamId}/gatherings", teamId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getGatherings",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("팀별 행사 목록 조회")
                            .description("특정 팀에 속한 행사 목록을 조회합니다. 팀 멤버만 조회할 수 있습니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .responseFields(*gatheringArrayFields())
                            .responseSchema(schema(GatheringDetailResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀별 행사 목록 조회 실패 - 팀 미소속")
    fun getGatheringsFail() {
        val teamId = 1L

        given(getGatheringUseCase.getGatherings(1L, teamId))
            .willThrow(TeamException(TeamExceptionCode.NOT_TEAM_MEMBER))

        val request = RestDocumentationRequestBuilders.get("/teams/{teamId}/gatherings", teamId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 상세 조회")
    fun getGathering() {
        val teamId = 1L
        val gatheringId = UUID.randomUUID()

        given(getGatheringUseCase.getGathering(1L, teamId, gatheringId)).willReturn(gatheringResult(gatheringId))

        val request =
            RestDocumentationRequestBuilders.get("/teams/{teamId}/gatherings/{gatheringId}", teamId, gatheringId)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getGathering",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 상세 조회")
                            .description("특정 행사의 상세 정보를 조회합니다. 팀 멤버만 조회할 수 있습니다.")
                            .pathParameters(
                                parameterWithName("teamId").description("팀 ID"),
                                parameterWithName("gatheringId").description("행사 ID (UUID)"),
                            )
                            .responseFields(*gatheringFields())
                            .responseSchema(schema(GatheringDetailResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 수정")
    fun updateGathering() {
        val teamId = 1L
        val gatheringId = UUID.randomUUID()
        val dto = UpdateGatheringRequest(
            GatheringVisible.PRIVATE,
            "수정된 행사 제목",
            "수정된 행사 설명",
            LocalDateTime.of(2025, 4, 1, 10, 0),
            LocalDateTime.of(2025, 4, 1, 18, 0),
            "서울 서초구",
            "https://example.com/new-image.png",
            "https://example.com/new-gathering",
            "010-9876-5432",
            LocalDateTime.of(2025, 3, 15, 0, 0),
            LocalDateTime.of(2025, 3, 31, 23, 59),
        )

        given(
            updateGatheringUseCase.update(
                UpdateGatheringCommand(
                    1L,
                    teamId,
                    gatheringId,
                    requireNotNull(dto.visible),
                    requireNotNull(dto.title),
                    requireNotNull(dto.content),
                    requireNotNull(dto.startAt),
                    requireNotNull(dto.endAt),
                    requireNotNull(dto.place),
                    dto.imageUrl,
                    dto.gatheringUrl,
                    dto.contact,
                    requireNotNull(dto.registerStartAt),
                    requireNotNull(dto.registerEndAt),
                )
            )
        ).willReturn(gatheringResult(gatheringId))

        val request = RestDocumentationRequestBuilders.patch(
            "/teams/{teamId}/gatherings/{gatheringId}",
            teamId,
            gatheringId,
        )
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "updateGathering",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 수정")
                            .description("행사 정보를 수정합니다. 팀 관리자만 수정할 수 있습니다.")
                            .pathParameters(
                                parameterWithName("teamId").description("팀 ID"),
                                parameterWithName("gatheringId").description("행사 ID (UUID)"),
                            )
                            .requestFields(*updateGatheringFields())
                            .responseFields(*gatheringFields())
                            .requestSchema(schema(UpdateGatheringRequest::class.java.simpleName))
                            .responseSchema(schema(GatheringDetailResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 수정 실패 - 권한 없음")
    fun updateGatheringFail() {
        val teamId = 1L
        val gatheringId = UUID.randomUUID()
        val dto = UpdateGatheringRequest(
            GatheringVisible.PRIVATE,
            "수정된 행사",
            "설명",
            LocalDateTime.of(2025, 4, 1, 10, 0),
            LocalDateTime.of(2025, 4, 1, 18, 0),
            "서울",
            null,
            null,
            null,
            LocalDateTime.of(2025, 3, 15, 0, 0),
            LocalDateTime.of(2025, 3, 31, 23, 59),
        )

        given(
            updateGatheringUseCase.update(
                UpdateGatheringCommand(
                    1L,
                    teamId,
                    gatheringId,
                    requireNotNull(dto.visible),
                    requireNotNull(dto.title),
                    requireNotNull(dto.content),
                    requireNotNull(dto.startAt),
                    requireNotNull(dto.endAt),
                    requireNotNull(dto.place),
                    dto.imageUrl,
                    dto.gatheringUrl,
                    dto.contact,
                    requireNotNull(dto.registerStartAt),
                    requireNotNull(dto.registerEndAt),
                )
            )
        ).willThrow(TeamException(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER))

        val request = RestDocumentationRequestBuilders.patch(
            "/teams/{teamId}/gatherings/{gatheringId}",
            teamId,
            gatheringId,
        )
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 삭제")
    fun deleteGathering() {
        val teamId = 1L
        val gatheringId = UUID.randomUUID()

        given(deleteGatheringUseCase.delete(1L, teamId, gatheringId)).willReturn(DeleteGatheringResult(gatheringId))

        val request = RestDocumentationRequestBuilders.delete(
            "/teams/{teamId}/gatherings/{gatheringId}",
            teamId,
            gatheringId,
        )
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "deleteGathering",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 삭제")
                            .description("행사를 삭제합니다. 팀 관리자만 삭제할 수 있습니다.")
                            .pathParameters(
                                parameterWithName("teamId").description("팀 ID"),
                                parameterWithName("gatheringId").description("행사 ID (UUID)"),
                            )
                            .responseFields(fieldWithPath("gatheringId").type(STRING).description("삭제된 행사 ID"))
                            .responseSchema(schema(DeleteGatheringResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 삭제 실패 - 권한 없음")
    fun deleteGatheringFail() {
        val teamId = 1L
        val gatheringId = UUID.randomUUID()

        willThrow(TeamException(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER))
            .given(deleteGatheringUseCase)
            .delete(1L, teamId, gatheringId)

        val request = RestDocumentationRequestBuilders.delete(
            "/teams/{teamId}/gatherings/{gatheringId}",
            teamId,
            gatheringId,
        )
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 템플릿 조회")
    fun getTemplate() {
        val gatheringId = UUID.randomUUID()
        val response = IntroduceTemplateResult(
            1,
            "안녕하세요. 저는 \${introduce} 개발자입니다. 가장 뿌듯했던 경험은 \${proudestExperience} 입니다.",
            mapOf("introduce" to "직무를 입력하세요", "proudestExperience" to "경험을 입력하세요"),
        )

        given(getIntroduceTemplateUseCase.getLatestTemplate(gatheringId, 1L)).willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}/template", gatheringId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getTemplate",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 템플릿 조회")
                            .description("행사의 최신 자기소개 템플릿을 조회합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                            .responseFields(
                                fieldWithPath("version").type(NUMBER)
                                    .description("템플릿 버전. 카드 수정 시 version 필드에 전달합니다."),
                                fieldWithPath("text").type(STRING)
                                    .description("템플릿 원문. \${변수명} 패턴이 입력 필드가 됩니다."),
                                subsectionWithPath("fieldPlaceholders").type("OBJECT").description("필드별 placeholder"),
                            )
                            .responseSchema(schema(IntroduceTemplateResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    private fun gatheringResult(gatheringId: UUID): GatheringDetailResult = GatheringDetailResult(
        gatheringId,
        GatheringVisible.PUBLIC,
        "Spring 밋업",
        "Spring Boot 관련 밋업입니다.",
        LocalDateTime.of(2025, 3, 20, 14, 0),
        LocalDateTime.of(2025, 3, 20, 17, 0),
        "서울 강남구",
        "https://example.com/image.png",
        "https://example.com/gathering",
        "010-1234-5678",
        LocalDateTime.of(2025, 3, 1, 0, 0),
        LocalDateTime.of(2025, 3, 19, 23, 59),
    )

    private fun updateGatheringFields(): Array<FieldDescriptor> = arrayOf(
        fieldWithPath("visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
        fieldWithPath("title").type(STRING).description("행사 제목"),
        fieldWithPath("content").type(STRING).description("행사 설명"),
        fieldWithPath("startAt").type(STRING).description("행사 시작일시"),
        fieldWithPath("endAt").type(STRING).description("행사 종료일시"),
        fieldWithPath("place").type(STRING).description("행사 장소"),
        fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
        fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
        fieldWithPath("contact").type(STRING).description("연락처").optional(),
        fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시"),
        fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시"),
    )

    private fun gatheringFields(): Array<FieldDescriptor> = arrayOf(
        fieldWithPath("id").type(STRING).description("행사 ID (UUID)"),
        fieldWithPath("visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
        fieldWithPath("title").type(STRING).description("행사 제목"),
        fieldWithPath("content").type(STRING).description("행사 설명"),
        fieldWithPath("startAt").type(STRING).description("행사 시작일시"),
        fieldWithPath("endAt").type(STRING).description("행사 종료일시"),
        fieldWithPath("place").type(STRING).description("행사 장소"),
        fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
        fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
        fieldWithPath("contact").type(STRING).description("연락처").optional(),
        fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시"),
        fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시"),
    )

    private fun gatheringArrayFields(): Array<FieldDescriptor> = arrayOf(
        fieldWithPath("[].id").type(STRING).description("행사 ID (UUID)"),
        fieldWithPath("[].visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
        fieldWithPath("[].title").type(STRING).description("행사 제목"),
        fieldWithPath("[].content").type(STRING).description("행사 설명"),
        fieldWithPath("[].startAt").type(STRING).description("행사 시작일시"),
        fieldWithPath("[].endAt").type(STRING).description("행사 종료일시"),
        fieldWithPath("[].place").type(STRING).description("행사 장소"),
        fieldWithPath("[].imageUrl").type(STRING).description("행사 이미지 URL").optional(),
        fieldWithPath("[].gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
        fieldWithPath("[].contact").type(STRING).description("연락처").optional(),
        fieldWithPath("[].registerStartAt").type(STRING).description("참가 등록 시작일시"),
        fieldWithPath("[].registerEndAt").type(STRING).description("참가 등록 종료일시"),
    )
}
