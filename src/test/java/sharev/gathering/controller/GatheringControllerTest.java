package sharev.gathering.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.BOOLEAN;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.domain.account.entity.Account;
import sharev.domain.card.dto.response.ResponseParticipantFlagDto;
import sharev.domain.gathering.dto.request.RequestCreateGatheringDto;
import sharev.domain.gathering.dto.request.RequestUpdateGatheringDto;
import sharev.domain.gathering.dto.response.ResponseGatheringDetailDto;
import sharev.domain.gathering.dto.response.ResponseIntroduceTemplateDto;
import sharev.domain.gathering.entity.GatheringVisibleType;

class GatheringControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("행사 참여 유무 확인")
    void isJoined() throws Exception {

        // given
        doReturn(new ResponseParticipantFlagDto(false))
                .when(cardService)
                .isJoined(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gatherings/{gatheringId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("isParticipant",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 참여 유무 확인")
                                .description(
                                        "사용자가 특정 행사에 참여했는지 확인합니다. " +
                                                "참여한 경우 true, 참여하지 않은 경우 false를 반환합니다. " +
                                                "이 API는 행사 참여 여부를 확인하여 카드 작성 가능 여부를 판단하는 데 사용됩니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("확인할 행사의 ID (UUID 형식)")
                                )
                                .responseFields(
                                        fieldWithPath("isParticipant").type(BOOLEAN)
                                                .description("행사 참여 유무 (true: 참여함, false: 참여하지 않음)")
                                )
                                .responseSchema(schema(ResponseParticipantFlagDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 생성")
    void createGathering() throws Exception {
        Long teamId = 1L;
        RequestCreateGatheringDto dto = new RequestCreateGatheringDto(
                GatheringVisibleType.PUBLIC,
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
                null
        );

        doReturn(true).when(memberService).isAdmin(any(Account.class), anyLong());
        doNothing().when(gatheringService).create(anyLong(), any(RequestCreateGatheringDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/teams/{teamId}/gatherings", teamId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("createGathering",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 생성")
                                .description("새로운 행사를 생성합니다. 팀 관리자만 생성할 수 있습니다. "
                                        + "자기소개 템플릿을 함께 전달하면 해당 템플릿으로, 전달하지 않으면 기본 빈 템플릿으로 생성됩니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"))
                                .requestFields(
                                        fieldWithPath("visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
                                        fieldWithPath("title").type(STRING).description("행사 제목"),
                                        fieldWithPath("content").type(STRING).description("행사 설명"),
                                        fieldWithPath("startAt").type(STRING).description("행사 시작일시 (예: 2025-03-20T14:00:00)"),
                                        fieldWithPath("endAt").type(STRING).description("행사 종료일시 (예: 2025-03-20T17:00:00)"),
                                        fieldWithPath("place").type(STRING).description("행사 장소"),
                                        fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
                                        fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
                                        fieldWithPath("contact").type(STRING).description("연락처").optional(),
                                        fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시 (예: 2025-03-01T00:00:00)"),
                                        fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시 (예: 2025-03-19T23:59:00)"),
                                        fieldWithPath("introduceTemplate").type("OBJECT").description("자기소개 템플릿 (선택, {text, fieldPlaceholders})").optional())
                                .requestSchema(schema(RequestCreateGatheringDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 생성 실패 - 권한 없음")
    void createGatheringFail() throws Exception {
        Long teamId = 1L;
        RequestCreateGatheringDto dto = new RequestCreateGatheringDto(
                GatheringVisibleType.PUBLIC, "Spring 밋업", "설명",
                LocalDateTime.of(2025, 3, 20, 14, 0),
                LocalDateTime.of(2025, 3, 20, 17, 0),
                "서울", null, null, null,
                LocalDateTime.of(2025, 3, 1, 0, 0),
                LocalDateTime.of(2025, 3, 19, 23, 59),
                null
        );

        doReturn(false).when(memberService).isAdmin(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/teams/{teamId}/gatherings", teamId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀별 행사 목록 조회")
    void getGatherings() throws Exception {
        Long teamId = 1L;

        List<ResponseGatheringDetailDto> response = List.of(
                new ResponseGatheringDetailDto(
                        UUID.randomUUID(), GatheringVisibleType.PUBLIC,
                        "Spring 밋업", "Spring Boot 관련 밋업입니다.",
                        LocalDateTime.of(2025, 3, 20, 14, 0),
                        LocalDateTime.of(2025, 3, 20, 17, 0),
                        "서울 강남구",
                        "https://example.com/image.png",
                        "https://example.com/gathering",
                        "010-1234-5678",
                        LocalDateTime.of(2025, 3, 1, 0, 0),
                        LocalDateTime.of(2025, 3, 19, 23, 59))
        );

        doReturn(true).when(teamService).isMember(any(Account.class), anyLong());
        doReturn(response).when(gatheringService).getGatherings(anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/teams/{teamId}/gatherings", teamId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getGatherings",
                        resource(ResourceSnippetParameters.builder()
                                .summary("팀별 행사 목록 조회")
                                .description("특정 팀에 속한 행사 목록을 조회합니다. 팀 멤버만 조회할 수 있습니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"))
                                .responseFields(
                                        fieldWithPath("[].id").type(STRING).description("행사 ID (UUID)"),
                                        fieldWithPath("[].visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
                                        fieldWithPath("[].title").type(STRING).description("행사 제목"),
                                        fieldWithPath("[].content").type(STRING).description("행사 설명"),
                                        fieldWithPath("[].startAt").type(STRING).description("행사 시작일시 (예: 2025-03-20T14:00:00)"),
                                        fieldWithPath("[].endAt").type(STRING).description("행사 종료일시 (예: 2025-03-20T17:00:00)"),
                                        fieldWithPath("[].place").type(STRING).description("행사 장소"),
                                        fieldWithPath("[].imageUrl").type(STRING).description("행사 이미지 URL").optional(),
                                        fieldWithPath("[].gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
                                        fieldWithPath("[].contact").type(STRING).description("연락처").optional(),
                                        fieldWithPath("[].registerStartAt").type(STRING).description("참가 등록 시작일시 (예: 2025-03-01T00:00:00)"),
                                        fieldWithPath("[].registerEndAt").type(STRING).description("참가 등록 종료일시 (예: 2025-03-19T23:59:00)"))
                                .responseSchema(schema(ResponseGatheringDetailDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀별 행사 목록 조회 실패 - 팀 미소속")
    void getGatheringsFail() throws Exception {
        Long teamId = 1L;

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/teams/{teamId}/gatherings", teamId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 상세 조회")
    void getGathering() throws Exception {
        Long teamId = 1L;
        UUID gatheringId = UUID.randomUUID();

        ResponseGatheringDetailDto response = new ResponseGatheringDetailDto(
                gatheringId, GatheringVisibleType.PUBLIC,
                "Spring 밋업", "Spring Boot 관련 밋업입니다.",
                LocalDateTime.of(2025, 3, 20, 14, 0),
                LocalDateTime.of(2025, 3, 20, 17, 0),
                "서울 강남구",
                "https://example.com/image.png",
                "https://example.com/gathering",
                "010-1234-5678",
                LocalDateTime.of(2025, 3, 1, 0, 0),
                LocalDateTime.of(2025, 3, 19, 23, 59));

        doReturn(true).when(teamService).isMember(any(Account.class), anyLong());
        doReturn(response).when(gatheringService).getGathering(anyLong(), any(UUID.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/teams/{teamId}/gatherings/{gatheringId}", teamId, gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getGathering",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 상세 조회")
                                .description("특정 행사의 상세 정보를 조회합니다. 팀 멤버만 조회할 수 있습니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"),
                                        parameterWithName("gatheringId").description("행사 ID (UUID)"))
                                .responseFields(
                                        fieldWithPath("id").type(STRING).description("행사 ID (UUID)"),
                                        fieldWithPath("visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
                                        fieldWithPath("title").type(STRING).description("행사 제목"),
                                        fieldWithPath("content").type(STRING).description("행사 설명"),
                                        fieldWithPath("startAt").type(STRING).description("행사 시작일시 (예: 2025-03-20T14:00:00)"),
                                        fieldWithPath("endAt").type(STRING).description("행사 종료일시 (예: 2025-03-20T17:00:00)"),
                                        fieldWithPath("place").type(STRING).description("행사 장소"),
                                        fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
                                        fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
                                        fieldWithPath("contact").type(STRING).description("연락처").optional(),
                                        fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시 (예: 2025-03-01T00:00:00)"),
                                        fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시 (예: 2025-03-19T23:59:00)"))
                                .responseSchema(schema(ResponseGatheringDetailDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 수정")
    void updateGathering() throws Exception {
        Long teamId = 1L;
        UUID gatheringId = UUID.randomUUID();

        RequestUpdateGatheringDto dto = new RequestUpdateGatheringDto(
                GatheringVisibleType.PRIVATE,
                "수정된 행사 제목",
                "수정된 행사 설명",
                LocalDateTime.of(2025, 4, 1, 10, 0),
                LocalDateTime.of(2025, 4, 1, 18, 0),
                "서울 서초구",
                "https://example.com/new-image.png",
                "https://example.com/new-gathering",
                "010-9876-5432",
                LocalDateTime.of(2025, 3, 15, 0, 0),
                LocalDateTime.of(2025, 3, 31, 23, 59)
        );

        ResponseGatheringDetailDto response = new ResponseGatheringDetailDto(
                gatheringId, dto.visible(), dto.title(), dto.content(),
                dto.startAt(), dto.endAt(), dto.place(),
                dto.imageUrl(), dto.gatheringUrl(), dto.contact(),
                dto.registerStartAt(), dto.registerEndAt());

        doReturn(true).when(memberService).isAdmin(any(Account.class), anyLong());
        doReturn(response).when(gatheringService).update(anyLong(), any(UUID.class), any(RequestUpdateGatheringDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/teams/{teamId}/gatherings/{gatheringId}", teamId, gatheringId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("updateGathering",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 수정")
                                .description("행사 정보를 수정합니다. 팀 관리자만 수정할 수 있습니다. "
                                        + "현재 모든 필드가 필수이며, 추후 부분 수정을 지원할 예정입니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"),
                                        parameterWithName("gatheringId").description("행사 ID (UUID)"))
                                .requestFields(
                                        fieldWithPath("visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
                                        fieldWithPath("title").type(STRING).description("행사 제목"),
                                        fieldWithPath("content").type(STRING).description("행사 설명"),
                                        fieldWithPath("startAt").type(STRING).description("행사 시작일시 (예: 2025-03-20T14:00:00)"),
                                        fieldWithPath("endAt").type(STRING).description("행사 종료일시 (예: 2025-03-20T17:00:00)"),
                                        fieldWithPath("place").type(STRING).description("행사 장소"),
                                        fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
                                        fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
                                        fieldWithPath("contact").type(STRING).description("연락처").optional(),
                                        fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시 (예: 2025-03-01T00:00:00)"),
                                        fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시 (예: 2025-03-19T23:59:00)"))
                                .responseFields(
                                        fieldWithPath("id").type(STRING).description("행사 ID (UUID)"),
                                        fieldWithPath("visible").type(STRING).description("공개 범위 (PUBLIC, PRIVATE)"),
                                        fieldWithPath("title").type(STRING).description("행사 제목"),
                                        fieldWithPath("content").type(STRING).description("행사 설명"),
                                        fieldWithPath("startAt").type(STRING).description("행사 시작일시 (예: 2025-03-20T14:00:00)"),
                                        fieldWithPath("endAt").type(STRING).description("행사 종료일시 (예: 2025-03-20T17:00:00)"),
                                        fieldWithPath("place").type(STRING).description("행사 장소"),
                                        fieldWithPath("imageUrl").type(STRING).description("행사 이미지 URL").optional(),
                                        fieldWithPath("gatheringUrl").type(STRING).description("행사 관련 URL").optional(),
                                        fieldWithPath("contact").type(STRING).description("연락처").optional(),
                                        fieldWithPath("registerStartAt").type(STRING).description("참가 등록 시작일시 (예: 2025-03-01T00:00:00)"),
                                        fieldWithPath("registerEndAt").type(STRING).description("참가 등록 종료일시 (예: 2025-03-19T23:59:00)"))
                                .requestSchema(schema(RequestUpdateGatheringDto.class.getSimpleName()))
                                .responseSchema(schema(ResponseGatheringDetailDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 수정 실패 - 권한 없음")
    void updateGatheringFail() throws Exception {
        Long teamId = 1L;
        UUID gatheringId = UUID.randomUUID();

        RequestUpdateGatheringDto dto = new RequestUpdateGatheringDto(
                GatheringVisibleType.PRIVATE, "수정된 행사", "설명",
                LocalDateTime.of(2025, 4, 1, 10, 0),
                LocalDateTime.of(2025, 4, 1, 18, 0),
                "서울", null, null, null,
                LocalDateTime.of(2025, 3, 15, 0, 0),
                LocalDateTime.of(2025, 3, 31, 23, 59)
        );

        doReturn(false).when(memberService).isAdmin(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/teams/{teamId}/gatherings/{gatheringId}", teamId, gatheringId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 삭제")
    void deleteGathering() throws Exception {
        Long teamId = 1L;
        UUID gatheringId = UUID.randomUUID();

        doReturn(true).when(memberService).isAdmin(any(Account.class), anyLong());
        doNothing().when(gatheringService).delete(anyLong(), any(UUID.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .delete("/teams/{teamId}/gatherings/{gatheringId}", teamId, gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("deleteGathering",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 삭제")
                                .description("행사를 삭제합니다. 팀 관리자만 삭제할 수 있습니다. "
                                        + "실제 데이터는 삭제되지 않고 soft delete 처리됩니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"),
                                        parameterWithName("gatheringId").description("행사 ID (UUID)"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 템플릿 조회")
    void getTemplate() throws Exception {
        UUID gatheringId = UUID.randomUUID();

        ResponseIntroduceTemplateDto response = new ResponseIntroduceTemplateDto(
                1,
                "안녕하세요. 저는 ${introduce} 개발자입니다. 가장 뿌듯했던 경험은 ${proudestExperience} 입니다.",
                java.util.Map.of("introduce", "직무를 입력하세요", "proudestExperience", "경험을 입력하세요")
        );

        doReturn(response).when(gatheringService).getLatestTemplate(any(UUID.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gatherings/{gatheringId}/template", gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getTemplate",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 템플릿 조회")
                                .description("행사의 최신 자기소개 템플릿을 조회합니다. " +
                                        "카드 작성 시 이 템플릿의 version과 fieldPlaceholders를 참고하여 introductionText를 구성합니다. " +
                                        "text 내의 ${변수명} 패턴이 입력 필드가 되며, " +
                                        "fieldPlaceholders의 key는 변수명, value는 placeholder 텍스트입니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                                .responseFields(
                                        fieldWithPath("version").type(NUMBER)
                                                .description("템플릿 버전. 카드 수정 시 이 값을 version 필드에 전달합니다."),
                                        fieldWithPath("text").type(STRING)
                                                .description("템플릿 원문. ${변수명} 패턴을 파싱하여 입력 필드를 구성합니다. " +
                                                        "예: \"안녕하세요. 저는 ${introduce} 개발자입니다.\""),
                                        subsectionWithPath("fieldPlaceholders").type("OBJECT")
                                                .description("필드별 placeholder (동적 Map<String, String>). " +
                                                        "key는 템플릿 변수명, value는 입력 힌트 텍스트. " +
                                                        "예: {\"introduce\": \"직무를 입력하세요\"}"))
                                .responseSchema(schema(ResponseIntroduceTemplateDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 삭제 실패 - 권한 없음")
    void deleteGatheringFail() throws Exception {
        Long teamId = 1L;
        UUID gatheringId = UUID.randomUUID();

        doReturn(false).when(memberService).isAdmin(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .delete("/teams/{teamId}/gatherings/{gatheringId}", teamId, gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
