package sharev.card.controller;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.account.entity.Account;
import sharev.card.dto.request.RequestUpdateCardInfoDto;
import sharev.card.dto.response.ResponseCardDto;
import sharev.card.dto.response.ResponseCardInfoDto;

class CardControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("카드 생성을 통한 행사 참여")
    void join() throws Exception {

        // given
        doNothing()
                .when(cardService)
                .join(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/events/{eventId}/cards", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())

                .andDo(document("join",
                        resource(ResourceSnippetParameters.builder()
                                .summary("카드 생성을 통한 행사 참여")
                                .description("행사에 참여하여 카드를 생성합니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여할 이벤트 id")
                                )
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("카드 내용 수정")
    void updateInfo() throws Exception {

        // given
        RequestUpdateCardInfoDto requestUpdateCardInfoDto = new RequestUpdateCardInfoDto("자기소개",
                "인상깊은 경험", "다시 겪고 싶은 경험");

        doAnswer(invocation -> new ResponseCardInfoDto(invocation.getArgument(2), invocation.getArgument(3),
                invocation.getArgument(4)))
                .when(cardService).updateInfo(any(UUID.class), anyLong(), anyString(), anyString(), anyString());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/events/{eventId}/cards", UUID.randomUUID())
                .content(objectMapper.writeValueAsString(requestUpdateCardInfoDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("updateInfo",
                        resource(ResourceSnippetParameters.builder()
                                .summary("카드 내용 수정")
                                .description("카드 내용을 수정합니다(이 항목은 추후 DB 변경에 따라 행사마다 다른 필드값을 지닐 수 있습니다).")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .requestFields(
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("인상깊은 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("다시 겪고 싶은 경험")
                                )
                                .responseFields(
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("인상깊은 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("다시 겪고 싶은 경험")
                                )
                                .requestSchema(schema(RequestUpdateCardInfoDto.class.getSimpleName()))
                                .responseSchema(schema(ResponseCardInfoDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("pin 번호로 카드 획득")
    void getCardByPinNumber() throws Exception {

        // given
        doReturn(true)
                .when(cardService).hasCompletedCard(any(Account.class), any(UUID.class));

        ResponseCardDto responseCardDto = new ResponseCardDto(21L, "김주호", "eora21@naver.com", null,
                "https://github.com/eora21", null, 1111, 1, "자기소개", "뿌듯했던 경험", "힘들었던 경험", true);

        doReturn(responseCardDto)
                .when(cardService).getCardByPinNumber(any(UUID.class), anyLong(), anyInt());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/events/{eventId}/cards/{pinNumber}", UUID.randomUUID(), 2840)
                .content(objectMapper.writeValueAsString(responseCardDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("getProfileByPinNumber",
                        resource(ResourceSnippetParameters.builder()
                                .summary("pin 번호로 카드 획득")
                                .description("pin 번호를 통해 카드를 획득합니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id"),
                                        parameterWithName("pinNumber").description("상대방 PIN 번호")
                                )
                                .responseFields(
                                        fieldWithPath("type").type(STRING)
                                                .description("타입"),
                                        fieldWithPath("profileId").type(NUMBER)
                                                .description("참여 번호"),
                                        fieldWithPath("name").type(STRING)
                                                .description("이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("linkedinUrl").type(STRING)
                                                .description("링크드인 url"),
                                        fieldWithPath("githubUrl").type(STRING)
                                                .description("깃헙 url"),
                                        fieldWithPath("instagramUrl").type(STRING)
                                                .description("인스타그램 url"),
                                        fieldWithPath("iconNumber").type(NUMBER)
                                                .description("카드 이미지 번호"),
                                        fieldWithPath("pinNumber").type(NUMBER)
                                                .description("상대방 PIN 번호"),
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("뿌듯했던 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("힘들었던 경험"),
                                        fieldWithPath("registerRequireFlag").type(BOOLEAN)
                                                .description("도감 등록 필요 유무")
                                )
                                .responseSchema(schema(ResponseCardDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("본인 카드 획득")
    void getMyCard() throws Exception {

        // given
        ResponseCardDto responseCardDto = new ResponseCardDto(21L, "김주호", "eora21@naver.com", null,
                "https://github.com/eora21", null, 1111, 1, "자기소개", "뿌듯했던 경험", "힘들었던 경험", true);

        doReturn(responseCardDto)
                .when(cardService).getMyCard(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/events/{eventId}/cards", UUID.randomUUID())
                .content(objectMapper.writeValueAsString(responseCardDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("getMyCard",
                        resource(ResourceSnippetParameters.builder()
                                .summary("본인 카드 획득")
                                .description("본인 카드를 획득합니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .responseFields(
                                        fieldWithPath("type").type(STRING)
                                                .description("타입"),
                                        fieldWithPath("profileId").type(NUMBER)
                                                .description("참여 번호"),
                                        fieldWithPath("name").type(STRING)
                                                .description("이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("linkedinUrl").type(STRING)
                                                .description("링크드인 url"),
                                        fieldWithPath("githubUrl").type(STRING)
                                                .description("깃헙 url"),
                                        fieldWithPath("instagramUrl").type(STRING)
                                                .description("인스타그램 url"),
                                        fieldWithPath("iconNumber").type(NUMBER)
                                                .description("카드 이미지 번호"),
                                        fieldWithPath("pinNumber").type(NUMBER)
                                                .description("본인 PIN 번호"),
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("뿌듯했던 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("힘들었던 경험"),
                                        fieldWithPath("registerRequireFlag").type(BOOLEAN)
                                                .description("도감 등록 필요 유무")
                                )
                                .responseSchema(schema(ResponseCardDto.class.getSimpleName()))
                                .build())));
    }
}
