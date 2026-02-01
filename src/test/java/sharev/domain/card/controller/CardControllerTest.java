package sharev.domain.card.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
import sharev.domain.card.dto.response.ResponseUpdateCardInfoDto;
import sharev.util.Type;

class CardControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("카드 생성")
    void join() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        doNothing().when(cardService).join(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/gathering/{gatheringId}/cards", gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("join",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 참여")
                                .description("카드를 생성하여 행사에 참여합니다.")
                                .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("카드 수정")
    void updateInfo() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        Map<String, String> introText = Map.of("introduce", "자기소개");
        RequestUpdateCardInfoDto requestDto = new RequestUpdateCardInfoDto(1, introText);
        ResponseUpdateCardInfoDto responseDto = new ResponseUpdateCardInfoDto(1, introText);

        doReturn(responseDto).when(cardService).updateInfo(any(UUID.class), anyLong(), anyInt(), anyMap());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/gathering/{gatheringId}/cards", gatheringId)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("updateCardInfo",
                        resource(ResourceSnippetParameters.builder()
                                .summary("카드 수정")
                                .description("카드 내용을 수정합니다.")
                                .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                                .requestSchema(schema(RequestUpdateCardInfoDto.class.getSimpleName()))
                                .responseSchema(schema(ResponseUpdateCardInfoDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("모든 카드 조회")
    void getAllCard() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        ResponseCardDto card = new ResponseCardDto(Type.FULL, 1L, "김주호", "test@test.com",
                List.of("https://github.com/test"), 0, 1, "템플릿", Map.of("intro", "안녕"));
        PageImpl<ResponseCardDto> page = new PageImpl<>(List.of(card), PageRequest.of(0, 20), 1);

        doReturn(true).when(cardService).hasCompletedCard(any(), any(UUID.class));
        doReturn(page).when(cardService).getAllCard(any(UUID.class), anyLong(), any(LocalDateTime.class), any());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gathering/{gatheringId}/cards", gatheringId)
                .param("snapshotTime", "2025-01-15T10:30:00")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getAllCards",
                        resource(ResourceSnippetParameters.builder()
                                .summary("모든 카드 조회")
                                .description("행사의 모든 카드를 조회합니다.")
                                .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                                .queryParameters(parameterWithName("snapshotTime").description("조회 시간"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("내 카드 조회")
    void getMyCard() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        ResponseCardDto myCard = new ResponseCardDto(Type.FULL, 1L, "김주호", "test@test.com",
                List.of("https://github.com/test"), 0, 1, "템플릿", Map.of("intro", "안녕"));

        doReturn(true).when(cardService).hasCompletedCard(any(), any(UUID.class));
        doReturn(myCard).when(cardService).getMyCard(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gathering/{gatheringId}/cards/me", gatheringId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getMyCard",
                        resource(ResourceSnippetParameters.builder()
                                .summary("내 카드 조회")
                                .description("본인 카드를 조회합니다.")
                                .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                                .responseSchema(schema(ResponseCardDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("PIN으로 카드 조회")
    void getCardByPinNumber() throws Exception {
        UUID gatheringId = UUID.randomUUID();
        ResponseCardDto card = new ResponseCardDto(Type.FULL, 21L, "홍길동", "hong@test.com",
                List.of("https://github.com/hong"), 0, 1, "템플릿", Map.of("intro", "안녕"));

        doReturn(true).when(cardService).hasCompletedCard(any(), any(UUID.class));
        doReturn(card).when(cardService).getCardByPinNumber(any(UUID.class), anyLong(), anyInt());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gathering/{gatheringId}/cards/by-pin/{pinNumber}", gatheringId, 2840)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getCardByPinNumber",
                        resource(ResourceSnippetParameters.builder()
                                .summary("PIN으로 카드 조회")
                                .description("PIN 번호로 카드를 조회합니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("행사 ID"),
                                        parameterWithName("pinNumber").description("PIN 번호"))
                                .responseSchema(schema(ResponseCardDto.class.getSimpleName()))
                                .build())));
    }
}
