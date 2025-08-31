package sharev.card_connection.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.BOOLEAN;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.account.entity.Account;
import sharev.card_connection.dto.request.RequestUpdateCardConnectionDto;
import sharev.card_connection.dto.response.ConnectedCardDto;
import sharev.card_connection.dto.response.NonConnectedCardDto;
import sharev.card_connection.dto.response.ResponseConnectionInfoDto;

class CardConnectionControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("카드 연결")
    void connect() throws Exception {

        // given
        doReturn(true)
                .when(cardService).hasCompletedCard(any(Account.class), any(UUID.class));

        RequestUpdateCardConnectionDto requestUpdateCardConnectionDto = new RequestUpdateCardConnectionDto(4285);

        doNothing()
                .when(cardConnectionService).connect(any(UUID.class), anyLong(), anyInt());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/events/{eventId}/card-connections", UUID.randomUUID())
                .content(objectMapper.writeValueAsString(requestUpdateCardConnectionDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())

                .andDo(document("connect",
                        resource(ResourceSnippetParameters.builder()
                                .summary("카드 연결")
                                .description("상대방과 연결합니다. 연결은 양방향 관계로 구성됩니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .requestFields(
                                        fieldWithPath("targetPinNumber").type(NUMBER)
                                                .description("상대방 PIN 번호")
                                )
                                .requestSchema(schema(RequestUpdateCardConnectionDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 내 카드 목록 조회")
    void getCardConnectionInfos() throws Exception {

        // given
        doReturn(true)
                .when(cardService).hasCompletedCard(any(Account.class), any(UUID.class));

        ResponseConnectionInfoDto responseConnectionInfoDto = new ResponseConnectionInfoDto(1L,
                new PageImpl<>(List.of(
                        new ConnectedCardDto(22L, "훈여정", "test@hun.com", null, null, null, 1,
                                "훈여정입니다.", "GDG에 쓰일 코드를 작성한 경험", "swagger를 위한 테스트코드를 짜던 기억", false),
                        new NonConnectedCardDto("권나연", 2, "당근"),
                        new NonConnectedCardDto("이유진", 3, "오늘의집")
                )));

        doReturn(responseConnectionInfoDto)
                .when(cardConnectionService)
                .getConnectionInfos(any(UUID.class), anyLong(), any(LocalDateTime.class), any(Pageable.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/events/{eventId}/card-connections", UUID.randomUUID())
                .param("snapshotTime", "2025-07-17T22:45:32")
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("getCardConnectionInfos",
                        resource(ResourceSnippetParameters.builder()
                                .summary("카드 연결 정보 확인")
                                .description(
                                        "카드 연결 정보를 확인합니다. 연결된 카드는 모든 정보를 볼 수 있지만, 연결되지 않은 카드는 특정 정보만 확인할 수 있습니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .queryParameters(
                                        parameterWithName("snapshotTime").description(
                                                "조회 기준 시간입니다. yyyy-MM-ddTHH:mm:ss 형식으로 작성하시면 됩니다."),
                                        parameterWithName("size").optional().description("조회 페이지 사이즈입니다."),
                                        parameterWithName("page").optional().description("조회 페이지 숫자입니다."),
                                        parameterWithName("sort").optional().description("페이지 정렬 기준입니다(미구현).")
                                )
                                .responseFields(
                                        fieldWithPath("registerCount").type(NUMBER)
                                                .description("연결된 사람 수"),
                                        fieldWithPath("relationProfiles.content[].type").type(STRING)
                                                .description("타입"),
                                        fieldWithPath("relationProfiles.content[].profileId").optional()
                                                .type(NUMBER)
                                                .description("참여 번호"),
                                        fieldWithPath("relationProfiles.content[].name").type(STRING)
                                                .description("이름"),
                                        fieldWithPath("relationProfiles.content[].email").optional().type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("relationProfiles.content[].linkedinUrl").optional().type(STRING)
                                                .description("링크드인 url"),
                                        fieldWithPath("relationProfiles.content[].githubUrl").optional().type(STRING)
                                                .description("깃헙 url"),
                                        fieldWithPath("relationProfiles.content[].instagramUrl").optional().type(STRING)
                                                .description("인스타그램 url"),
                                        fieldWithPath("relationProfiles.content[].iconNumber").type(NUMBER)
                                                .description("프로필 이미지 번호"),
                                        fieldWithPath("relationProfiles.content[].introduce").optional()
                                                .type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath(
                                                "relationProfiles.content[].proudestExperience")
                                                .type(STRING)
                                                .description("뿌듯했던 경험"),
                                        fieldWithPath(
                                                "relationProfiles.content[].toughExperience").optional()
                                                .type(STRING)
                                                .description("힘들었던 경험"),
                                        fieldWithPath("relationProfiles.content[].relationFlag").optional()
                                                .type(BOOLEAN)
                                                .description("연결 유무"),
                                        fieldWithPath("relationProfiles.page.size").type(NUMBER)
                                                .description("페이지 사이즈"),
                                        fieldWithPath("relationProfiles.page.number").type(NUMBER)
                                                .description("페이지 수"),
                                        fieldWithPath("relationProfiles.page.totalElements").type(NUMBER)
                                                .description("전체 참여자 수(자신 제외)"),
                                        fieldWithPath("relationProfiles.page.totalPages").type(NUMBER)
                                                .description("전체 페이지")
                                )
                                .responseSchema(schema(ResponseConnectionInfoDto.class.getSimpleName()))
                                .build())));
    }
}
