package sharev.domain.card.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.domain.card.dto.request.RequestUpdateCardInfoDto;
import sharev.domain.card.dto.response.ResponseCardDto;
import sharev.domain.card.dto.response.ResponseMyPinNumberDto;
import sharev.domain.card.dto.response.ResponseUpdateCardInfoDto;
import sharev.util.Type;

class CardControllerTest extends ControllerTestSupport {

    private static final String TEMPLATE_TEXT =
            "안녕하세요. 저는 ${introduce} 개발자입니다. 가장 뿌듯했던 경험은 ${proudestExperience} 입니다.";

    @Test
    @WithCustomMockUser
    @DisplayName("카드 생성")
    void join() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        doNothing().when(cardService).join(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/gatherings/{gatheringId}/cards", gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("join",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 참여")
                                .description(
                                        "카드를 생성하여 행사에 참여합니다. " +
                                                "생성 직후 카드는 미완성 상태이며, " +
                                                "PATCH /gathering/{gatheringId}/cards 로 version과 introductionText를 전달해야 완성됩니다. "
                                                +
                                                "카드가 완성되어야 다른 참여자의 카드를 조회할 수 있습니다. " +
                                                "응답 body는 없으며, 201 Created 상태코드만 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("카드 수정")
    void updateInfo() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        Map<String, String> introText = Map.of(
                "introduce", "백엔드",
                "proudestExperience", "해커톤 우승");
        RequestUpdateCardInfoDto requestDto = new RequestUpdateCardInfoDto(1, introText);
        ResponseUpdateCardInfoDto responseDto = new ResponseUpdateCardInfoDto(1, introText);

        doReturn(responseDto).when(cardService).updateInfo(any(UUID.class), anyLong(), anyInt(), anyMap());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/gatherings/{gatheringId}/cards", gatheringId)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("updateCardInfo",
                        resource(ResourceSnippetParameters.builder()
                                .summary("카드 수정")
                                .description(
                                        "카드 내용을 수정합니다. " +
                                                "version은 카드 조회 시 응답의 lastIntroduceTemplateVersion 값을 사용합니다. " +
                                                "introductionText의 key는 introduceTemplateContentText에 포함된 ${변수명} 패턴에서 추출합니다. "
                                                +
                                                "예: 템플릿이 \"저는 ${introduce} 개발자입니다\" 이면, key는 \"introduce\" 입니다. " +
                                                "key 개수와 이름이 템플릿과 정확히 일치해야 하며, 불일치 시 400 에러가 발생합니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                                .requestFields(
                                        fieldWithPath("version").type(NUMBER)
                                                .description(
                                                        "자기소개 템플릿 버전. 카드 조회 응답의 lastIntroduceTemplateVersion 값을 사용합니다."),
                                        subsectionWithPath("introductionText").type("OBJECT")
                                                .description("자기소개 내용 (동적 Map<String, String>). " +
                                                        "key는 템플릿의 ${변수명}과 일치해야 합니다. " +
                                                        "예: {\"introduce\": \"백엔드\", \"proudestExperience\": \"해커톤 우승\"}"))
                                .responseFields(
                                        fieldWithPath("templateVersion").type(NUMBER)
                                                .description("저장된 템플릿 버전"),
                                        subsectionWithPath("introductionText").type("OBJECT")
                                                .description("저장된 자기소개 내용 (동적 Map<String, String>). " +
                                                        "key는 요청 시 전달한 템플릿 변수명과 동일합니다."))
                                .requestSchema(schema(RequestUpdateCardInfoDto.class.getSimpleName()))
                                .responseSchema(schema(ResponseUpdateCardInfoDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("모든 카드 조회")
    void getAllCard() throws Exception {
        UUID gatheringId = UUID.randomUUID();

        ResponseCardDto fullCard = new ResponseCardDto(
                Type.FULL, 1L, "김주호", "test@test.com",
                List.of("https://github.com/test"), 1, 1,
                TEMPLATE_TEXT,
                Map.of("introduce", "백엔드", "proudestExperience", "해커톤 우승"));

        ResponseCardDto minimumCard = new ResponseCardDto(
                Type.MINIMUM, 2L, "홍길동", "",
                List.of(), 1, 1,
                TEMPLATE_TEXT,
                Map.of());

        PageImpl<ResponseCardDto> page = new PageImpl<>(
                List.of(fullCard, minimumCard), PageRequest.of(0, 20), 2);

        doReturn(true).when(cardService).hasCompletedCard(any(), any(UUID.class));
        doReturn(page).when(cardService).getAllCard(any(UUID.class), anyLong(), any(LocalDateTime.class), any());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gatherings/{gatheringId}/cards", gatheringId)
                .param("snapshotTime", "2025-01-15T10:30:00")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getAllCards",
                        resource(ResourceSnippetParameters.builder()
                                .summary("모든 카드 조회")
                                .description(
                                        "행사의 모든 카드를 페이지네이션으로 조회합니다. " +
                                                "정렬 기준: 1차 - 도감 등록(커넥션)된 사람 우선, " +
                                                "2차 - 커넥션된 사람 중 최신 커넥션 순, 미커넥션은 참여 순번 기준입니다. " +
                                                "snapshotTime은 첫 페이지 요청 시의 시간을 이후 페이지에서도 동일하게 재사용해야 " +
                                                "페이지 간 데이터 일관성이 보장됩니다. " +
                                                "type이 FULL인 카드는 도감 등록된 사용자로 모든 정보가 포함되고, " +
                                                "MINIMUM인 카드는 미등록 사용자로 email, linkUrls, introductionText가 빈 값입니다. " +
                                                "본인 카드가 완성되지 않은 경우 400 에러가 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                                .queryParameters(
                                        parameterWithName("snapshotTime")
                                                .description("조회 기준 시간 (ISO 8601 형식, 예: 2025-01-15T10:30:00). " +
                                                        "첫 페이지 요청 시의 값을 이후 페이지에서 재사용해야 합니다."),
                                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                        parameterWithName("size").description("페이지 크기"))
                                .responseFields(
                                        fieldWithPath("content[].type").type(STRING)
                                                .description("카드 타입 (FULL: 도감 등록된 사용자, MINIMUM: 미등록 사용자)"),
                                        fieldWithPath("content[].cardId").type(NUMBER)
                                                .description("카드 ID"),
                                        fieldWithPath("content[].name").type(STRING)
                                                .description("사용자 이름"),
                                        fieldWithPath("content[].email").type(STRING)
                                                .description("이메일 (MINIMUM인 경우 빈 문자열)"),
                                        fieldWithPath("content[].linkUrls").type("ARRAY")
                                                .description("링크 URL 목록 (MINIMUM인 경우 빈 배열)"),
                                        fieldWithPath("content[].lastIntroduceTemplateVersion").type(NUMBER)
                                                .description("최신 자기소개 템플릿 버전"),
                                        fieldWithPath("content[].nowIntroduceTemplateVersion").type(NUMBER)
                                                .description("해당 카드에 저장된 템플릿 버전"),
                                        fieldWithPath("content[].introduceTemplateContentText").type(STRING)
                                                .description("자기소개 템플릿 원문 (${변수명} 패턴 포함)"),
                                        subsectionWithPath("content[].introductionText").type("OBJECT")
                                                .description("자기소개 내용 (동적 Map<String, String>). " +
                                                        "FULL 타입: 템플릿 변수명을 key로 한 작성 값 포함. " +
                                                        "MINIMUM 타입: 빈 객체. " +
                                                        "예: {\"introduce\": \"백엔드\", \"proudestExperience\": \"해커톤 우승\"}"),
                                        fieldWithPath("page").type("OBJECT").description("페이지 정보"),
                                        fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                                        fieldWithPath("page.number").type(NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("page.totalElements").type(NUMBER).description("총 요소 수"),
                                        fieldWithPath("page.totalPages").type(NUMBER).description("총 페이지 수"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("내 카드 조회")
    void getMyCard() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        ResponseCardDto myCard = new ResponseCardDto(
                Type.FULL, 1L, "김주호", "test@test.com",
                List.of("https://github.com/test"), 1, 1,
                TEMPLATE_TEXT,
                Map.of("introduce", "백엔드", "proudestExperience", "해커톤 우승"));

        doReturn(true).when(cardService).hasCompletedCard(any(), any(UUID.class));
        doReturn(myCard).when(cardService).getMyCard(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gatherings/{gatheringId}/cards/me", gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getMyCard",
                        resource(ResourceSnippetParameters.builder()
                                .summary("내 카드 조회")
                                .description(
                                        "본인 카드를 조회합니다. " +
                                                "본인 카드는 항상 type=FULL 입니다. " +
                                                "lastIntroduceTemplateVersion과 nowIntroduceTemplateVersion이 다르면 " +
                                                "관리자가 템플릿을 업데이트한 것이므로, 사용자에게 재작성을 안내할 수 있습니다. " +
                                                "카드가 완성되지 않은 경우 400 에러가 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                                .responseFields(
                                        fieldWithPath("type").type(STRING)
                                                .description("카드 타입 (본인 카드는 항상 FULL)"),
                                        fieldWithPath("cardId").type(NUMBER)
                                                .description("카드 ID"),
                                        fieldWithPath("name").type(STRING)
                                                .description("사용자 이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("linkUrls").type("ARRAY")
                                                .description("링크 URL 목록"),
                                        fieldWithPath("lastIntroduceTemplateVersion").type(NUMBER)
                                                .description("최신 자기소개 템플릿 버전"),
                                        fieldWithPath("nowIntroduceTemplateVersion").type(NUMBER)
                                                .description("현재 카드에 저장된 템플릿 버전. " +
                                                        "lastIntroduceTemplateVersion과 다르면 템플릿이 업데이트된 것입니다."),
                                        fieldWithPath("introduceTemplateContentText").type(STRING)
                                                .description("자기소개 템플릿 원문. ${변수명} 패턴을 파싱하여 입력 필드를 구성합니다. " +
                                                        "예: \"안녕하세요. 저는 ${introduce} 개발자입니다.\""),
                                        subsectionWithPath("introductionText").type("OBJECT")
                                                .description("사용자가 작성한 자기소개 내용 (동적 Map<String, String>). " +
                                                        "key는 템플릿 변수명, value는 작성한 값. " +
                                                        "예: {\"introduce\": \"백엔드\", \"proudestExperience\": \"해커톤 우승\"}"))
                                .responseSchema(schema(ResponseCardDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("내 PIN 번호 조회")
    void getMyPinNumber() throws Exception {
        UUID gatheringId = UUID.randomUUID();

        doReturn(true).when(cardService).hasCompletedCard(any(), any(UUID.class));
        doReturn(1234).when(cardService).getMyPinNumber(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gatherings/{gatheringId}/cards/me/pin", gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getMyPinNumber",
                        resource(ResourceSnippetParameters.builder()
                                .summary("내 PIN 번호 조회")
                                .description("본인 카드의 PIN 번호를 조회합니다. " +
                                        "PIN 번호는 1000~9999 범위의 4자리 정수입니다. " +
                                        "카드가 완성되지 않은 경우 400 에러가 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID (UUID 형식)"))
                                .responseFields(
                                        fieldWithPath("pinNumber").type(NUMBER)
                                                .description("PIN 번호 (1000~9999 범위 정수)"))
                                .responseSchema(schema(ResponseMyPinNumberDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("PIN으로 카드 조회")
    void getCardByPinNumber() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        ResponseCardDto card = new ResponseCardDto(
                Type.FULL, 21L, "홍길동", "hong@test.com",
                List.of("https://github.com/hong"), 1, 1,
                TEMPLATE_TEXT,
                Map.of("introduce", "프론트엔드", "proudestExperience", "오픈소스 기여"));

        doReturn(true).when(cardService).hasCompletedCard(any(), any(UUID.class));
        doReturn(card).when(cardService).getCardByPinNumber(any(UUID.class), anyLong(), anyInt());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gatherings/{gatheringId}/cards/by-pin/{pinNumber}", gatheringId, 2840)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getCardByPinNumber",
                        resource(ResourceSnippetParameters.builder()
                                .summary("PIN으로 카드 조회")
                                .description(
                                        "PIN 번호로 카드를 조회합니다. " +
                                                "조회 시 상대방과 자동으로 도감 등록(양방향)이 이루어집니다. " +
                                                "자기 자신 또는 이미 등록된 상대의 경우 조회만 수행되며 에러는 발생하지 않습니다. " +
                                                "PIN 번호는 1000~9999 범위의 4자리 정수입니다. " +
                                                "본인 카드가 완성되지 않은 경우 400 에러가 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID (UUID 형식)"),
                                        parameterWithName("pinNumber")
                                                .description("조회할 카드의 PIN 번호 (1000~9999 범위 정수)"))
                                .responseFields(
                                        fieldWithPath("type").type(STRING)
                                                .description("카드 타입 (PIN 조회는 항상 FULL)"),
                                        fieldWithPath("cardId").type(NUMBER)
                                                .description("카드 ID"),
                                        fieldWithPath("name").type(STRING)
                                                .description("사용자 이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("linkUrls").type("ARRAY")
                                                .description("링크 URL 목록"),
                                        fieldWithPath("lastIntroduceTemplateVersion").type(NUMBER)
                                                .description("최신 자기소개 템플릿 버전"),
                                        fieldWithPath("nowIntroduceTemplateVersion").type(NUMBER)
                                                .description("해당 카드에 저장된 템플릿 버전"),
                                        fieldWithPath("introduceTemplateContentText").type(STRING)
                                                .description("자기소개 템플릿 원문 (${변수명} 패턴 포함)"),
                                        subsectionWithPath("introductionText").type("OBJECT")
                                                .description("사용자가 작성한 자기소개 내용 (동적 Map<String, String>). " +
                                                        "key는 템플릿 변수명, value는 작성한 값. " +
                                                        "예: {\"introduce\": \"프론트엔드\", \"proudestExperience\": \"오픈소스 기여\"}"))
                                .responseSchema(schema(ResponseCardDto.class.getSimpleName()))
                                .build())));
    }
}
