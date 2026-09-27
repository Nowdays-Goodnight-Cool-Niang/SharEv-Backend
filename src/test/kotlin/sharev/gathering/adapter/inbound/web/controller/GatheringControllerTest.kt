package sharev.gathering.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.then
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
import sharev.gathering.adapter.inbound.web.dto.request.FieldSpecRequest
import sharev.gathering.adapter.inbound.web.dto.request.UpdateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.request.UpsertIntroductionRequest
import sharev.gathering.adapter.inbound.web.dto.response.*
import sharev.gathering.adapter.inbound.web.mapper.toModel
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.result.*
import sharev.gathering.application.port.inbound.usecase.*
import sharev.gathering.domain.model.FieldSpec
import sharev.gathering.domain.model.GatheringVisible
import java.time.LocalDateTime
import java.util.*

class GatheringControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("전체 행사 목록 조회")
    fun allGatherings() {
        val pageable = PageRequest.of(0, 20)
        val response = PageImpl(listOf(gatheringDetailResult(UUID.randomUUID())), pageable, 1)

        given(mockBean<GetGatheringsUseCase>().getGatherings(any(), any()))
            .willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/gatherings")
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "allGatherings",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("전체 행사 목록 조회")
                            .description("모든 행사 목록을 페이지네이션으로 조회합니다.")
                            .queryParameters(*pageableQueryParameters())
                            .responseFields(*gatheringPageFields())
                            .responseSchema(schema(GatheringDetailResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("관리 가능한 행사 목록 조회")
    fun getManagedGatherings() {
        val pageable = PageRequest.of(0, 20)
        val response = PageImpl(listOf(gatheringDetailResult(UUID.randomUUID())), pageable, 1)

        given(mockBean<GetGatheringsUseCase>().getManagedGatherings(anyLong(), any<Pageable>()))
            .willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/gatherings/managed")
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "allManagedGatherings",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("관리 가능한 전체 행사 목록 조회")
                            .description("모든 관리 가능한 행사 목록을 페이지네이션으로 조회합니다.")
                            .queryParameters(*pageableQueryParameters())
                            .responseFields(*gatheringPageFields())
                            .responseSchema(schema(GatheringDetailResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("관리 가능한 행사 목록 조회 실패 - 비로그인")
    fun getManagedGatheringsFail() {
        val request = RestDocumentationRequestBuilders.get("/gatherings/managed")
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnauthorized())

        then(mockBean<GetGatheringsUseCase>()).shouldHaveNoInteractions()
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 참여 유무 확인")
    fun isParticipant() {
        val gatheringId = UUID.randomUUID()

        given(mockBean<CheckGatheringParticipantUseCase>().isParticipant(1L, gatheringId))
            .willReturn(ParticipantResult(false))

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}/participant", gatheringId)
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
    @DisplayName("행사 참여 유무 확인 실패 - 비로그인")
    fun isParticipantFail() {
        val gatheringId = UUID.randomUUID()

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}/participant", gatheringId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnauthorized())

        then(mockBean<CheckGatheringParticipantUseCase>()).shouldHaveNoInteractions()
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 생성")
    fun createGathering() {
        val teamId = 1L
        val gatheringId = UUID.randomUUID()
        val dto = CreateGatheringRequest(
            teamId,
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
            mockBean<CreateGatheringUseCase>().create(
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

        val request = RestDocumentationRequestBuilders.post("/gatherings")
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
                            .requestFields(
                                fieldWithPath("teamId").type(NUMBER).description("행사를 생성할 팀 ID"),
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
    @DisplayName("행사 상세 조회")
    fun getGathering() {
        val gatheringId = UUID.randomUUID()

        given(mockBean<GetGatheringUseCase>().getGathering(1L, gatheringId))
            .willReturn(gatheringDetailResult(gatheringId))

        val request =
            RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}", gatheringId)
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
                            .description("특정 행사의 상세 정보를 조회합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID (UUID)"))
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
            mockBean<UpdateGatheringUseCase>().update(
                UpdateGatheringCommand(
                    1L,
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
        ).willReturn(gatheringDetailResult(gatheringId))

        val request = RestDocumentationRequestBuilders.patch(
            "/gatherings/{gatheringId}",
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
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID (UUID)"))
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
    @DisplayName("행사 삭제")
    fun deleteGathering() {
        val gatheringId = UUID.randomUUID()

        given(mockBean<DeleteGatheringUseCase>().delete(1L, gatheringId))
            .willReturn(DeleteGatheringResult(gatheringId))

        val request = RestDocumentationRequestBuilders.delete(
            "/gatherings/{gatheringId}",
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
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID (UUID)"))
                            .responseFields(fieldWithPath("gatheringId").type(STRING).description("삭제된 행사 ID"))
                            .responseSchema(schema(DeleteGatheringResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 소개 템플릿 조회")
    fun getIntroduction() {
        val gatheringId = UUID.randomUUID()
        val response = IntroductionResult(
            1,
            $$"안녕하세요. 저는 ${introduce} 개발자입니다. 가장 뿌듯했던 경험은 ${proudestExperience} 입니다.",
            mapOf(
                "introduce" to FieldSpec("직무를 입력하세요"),
                "proudestExperience" to FieldSpec("경험을 입력하세요"),
            ),
        )

        given(mockBean<GetIntroductionUseCase>().getLatestIntroduction(gatheringId, 1L))
            .willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}/introduction", gatheringId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getIntroduction",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 소개 템플릿 조회")
                            .description("행사의 최신 자기소개 템플릿을 조회합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                            .responseFields(
                                fieldWithPath("version").type(NUMBER)
                                    .description("템플릿 버전. 카드 수정 시 version 필드에 전달합니다."),
                                fieldWithPath("source").type(STRING)
                                    .description($$"템플릿 원문. ${변수명} 패턴이 입력 필드가 됩니다."),
                                subsectionWithPath("fields").type("OBJECT").description("필드별 명세 (placeholder 포함)"),
                            )
                            .responseSchema(schema(IntroductionResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 소개 템플릿 upsert")
    fun upsertIntroduction() {
        val gatheringId = UUID.randomUUID()
        val source = $$"안녕하세요. ${name}입니다."
        val fields = mapOf("name" to FieldSpecRequest("이름을 입력해 주세요"))
        val dto = UpsertIntroductionRequest(source, fields)
        val result = IntroductionResult(1, source, fields.mapValues { (_, field) -> field.toModel() })

        given(mockBean<UpsertIntroductionUseCase>().upsertIntroduction(any()))
            .willReturn(result)

        val request = RestDocumentationRequestBuilders.put("/gatherings/{gatheringId}/introduction", gatheringId)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                documentResource(
                    "upsertIntroduction",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 소개 템플릿 upsert")
                            .description("행사의 자기소개 템플릿을 추가하거나 업데이트됩니다. 기존 필드 형식과 같다면 업데이트, 다르다면 추가되며 버전이 상승합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                            .responseFields(
                                fieldWithPath("source").type(STRING)
                                    .description($$"템플릿 원문. ${변수명} 패턴이 입력 필드가 됩니다."),
                                fieldWithPath("version").type(NUMBER)
                                    .description("템플릿 버전"),
                                subsectionWithPath("fields").type("OBJECT")
                                    .description("필드별 명세 (placeholder 포함)"),
                            )
                            .responseSchema(schema(IntroductionResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    private fun gatheringDetailResult(gatheringId: UUID): GatheringDetailResult = GatheringDetailResult(
        gatheringId,
        1L,
        "공유대학",
        "owner_handle",
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
        fieldWithPath("teamId").type(NUMBER).description("팀 ID"),
        fieldWithPath("teamTitle").type(STRING).description("팀 제목").optional(),
        fieldWithPath("ownerHandle").type(STRING).description("행사 소유자 핸들").optional(),
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

    private fun pageableQueryParameters() = arrayOf(
        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
        parameterWithName("size").description("페이지 크기").optional(),
        parameterWithName("sort").description("정렬 기준 (예: startAt,desc)").optional(),
    )

    private fun gatheringPageFields(): Array<FieldDescriptor> = arrayOf(
        fieldWithPath("content[].id").type(STRING).description("행사 ID (UUID)"),
        fieldWithPath("content[].teamId").type(NUMBER).description("팀 ID"),
        fieldWithPath("content[].teamTitle").type(STRING).description("팀 제목").optional(),
        fieldWithPath("content[].ownerHandle").type(STRING).description("행사 소유자 핸들").optional(),
        fieldWithPath("content[].visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
        fieldWithPath("content[].title").type(STRING).description("행사 제목"),
        fieldWithPath("content[].content").type(STRING).description("행사 설명"),
        fieldWithPath("content[].startAt").type(STRING).description("행사 시작일시"),
        fieldWithPath("content[].endAt").type(STRING).description("행사 종료일시"),
        fieldWithPath("content[].place").type(STRING).description("행사 장소"),
        fieldWithPath("content[].imageUrl").type(STRING).description("행사 이미지 URL").optional(),
        fieldWithPath("content[].gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
        fieldWithPath("content[].contact").type(STRING).description("연락처").optional(),
        fieldWithPath("content[].registerStartAt").type(STRING).description("참가 등록 시작일시"),
        fieldWithPath("content[].registerEndAt").type(STRING).description("참가 등록 종료일시"),
        fieldWithPath("page").type("OBJECT").description("페이지 정보"),
        fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
        fieldWithPath("page.number").type(NUMBER).description("현재 페이지"),
        fieldWithPath("page.totalElements").type(NUMBER).description("총 요소 수"),
        fieldWithPath("page.totalPages").type(NUMBER).description("총 페이지 수"),
    )
}
