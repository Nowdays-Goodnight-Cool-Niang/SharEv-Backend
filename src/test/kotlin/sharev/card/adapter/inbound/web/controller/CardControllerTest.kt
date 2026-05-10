package sharev.card.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType.NUMBER
import com.epages.restdocs.apispec.SimpleType.STRING
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import sharev.ControllerTestSupport
import sharev.WithCustomMockUser
import sharev.card.adapter.inbound.web.dto.request.UpdateCardIntroduceRequest
import sharev.card.adapter.inbound.web.dto.response.CardResponse
import sharev.card.adapter.inbound.web.dto.response.JoinCardResponse
import sharev.card.adapter.inbound.web.dto.response.MyPinNumberResponse
import sharev.card.adapter.inbound.web.dto.response.UpdateCardIntroduceResponse
import sharev.card.application.port.inbound.result.CardResult
import sharev.card.application.port.inbound.result.JoinCardResult
import sharev.card.application.port.inbound.result.UpdateCardInfoResult
import sharev.card.domain.model.CardDisplay
import java.time.LocalDateTime
import java.util.*

class CardControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("카드 생성")
    fun join() {
        val gatheringId = UUID.randomUUID()

        given(joinCardUseCase.join(any())).willReturn(JoinCardResult(1L, 1234))

        val request = RestDocumentationRequestBuilders.post("/gatherings/{gatheringId}/cards", gatheringId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(
                documentResource(
                    "join",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("행사 참여")
                            .description("카드를 생성하여 행사에 참여합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                            .responseFields(
                                fieldWithPath("cardId").type(NUMBER).description("생성된 카드 ID"),
                                fieldWithPath("pinNumber").type(NUMBER).description("생성된 PIN 번호"),
                            )
                            .responseSchema(schema(JoinCardResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("카드 수정")
    fun updateIntroduce() {
        val gatheringId = UUID.randomUUID()
        val introText = mapOf("introduce" to "백엔드")
        val requestDto = UpdateCardIntroduceRequest(1, introText)

        given(updateCardInfoUseCase.updateIntroduce(any()))
            .willReturn(UpdateCardInfoResult(1, introText))

        val request = RestDocumentationRequestBuilders.patch("/gatherings/{gatheringId}/cards", gatheringId)
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "updateCardInfo",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("카드 수정")
                            .description("카드 자기소개 내용을 수정합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                            .requestFields(
                                fieldWithPath("version").type(NUMBER).description("자기소개 템플릿 버전"),
                                subsectionWithPath("introductionText").type("OBJECT")
                                    .description("템플릿 변수명별 자기소개 내용"),
                            )
                            .responseFields(
                                fieldWithPath("templateVersion").type(NUMBER).description("저장된 템플릿 버전"),
                                subsectionWithPath("introductionText").type("OBJECT").description("저장된 자기소개 내용"),
                            )
                            .requestSchema(schema(UpdateCardIntroduceRequest::class.java.simpleName))
                            .responseSchema(schema(UpdateCardIntroduceResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("모든 카드 조회")
    fun getAllCard() {
        val gatheringId = UUID.randomUUID()
        val fullCard = CardResult(
            CardDisplay.FULL,
            1L,
            "김주호",
            "test@test.com",
            listOf("https://github.com/test"),
            1,
            1,
            TEMPLATE_TEXT,
            mapOf("introduce" to "백엔드"),
        )
        val minimumCard =
            CardResult(CardDisplay.MINIMUM, 2L, "홍길동", "", emptyList(), 1, 1, TEMPLATE_TEXT, emptyMap())

        val snapshotTime = LocalDateTime.of(2025, 1, 15, 10, 30)
        val pageable = PageRequest.of(0, 20)

        given(getAllCardsUseCase.getAllCards(any()))
            .willReturn(PageImpl(listOf(fullCard, minimumCard), pageable, 2))

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}/cards", gatheringId)
            .param("snapshotTime", "2025-01-15T10:30:00")
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getAllCards",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("모든 카드 조회")
                            .description("행사의 모든 카드를 페이지네이션으로 조회합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                            .queryParameters(
                                parameterWithName("snapshotTime").description("조회 기준 시각"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                            )
                            .responseFields(
                                fieldWithPath("content[].type").type(STRING).description("카드 타입"),
                                fieldWithPath("content[].cardId").type(NUMBER).description("카드 ID"),
                                fieldWithPath("content[].name").type(STRING).description("사용자 이름"),
                                fieldWithPath("content[].email").type(STRING).description("이메일"),
                                fieldWithPath("content[].linkUrls").type("ARRAY").description("링크 URL 목록"),
                                fieldWithPath("content[].lastIntroduceTemplateVersion").type(NUMBER)
                                    .description("최신 템플릿 버전"),
                                fieldWithPath("content[].nowIntroduceTemplateVersion").type(NUMBER)
                                    .description("카드에 저장된 템플릿 버전"),
                                fieldWithPath("content[].introduceTemplateContentText").type(STRING)
                                    .description("템플릿 원문"),
                                subsectionWithPath("content[].introductionText").type("OBJECT")
                                    .description("자기소개 내용"),
                                fieldWithPath("page").type("OBJECT").description("페이지 정보"),
                                fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                                fieldWithPath("page.number").type(NUMBER).description("현재 페이지"),
                                fieldWithPath("page.totalElements").type(NUMBER).description("총 요소 수"),
                                fieldWithPath("page.totalPages").type(NUMBER).description("총 페이지 수"),
                            )
                            .responseSchema(schema(CardResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("내 카드 조회")
    fun getMyCard() {
        val gatheringId = UUID.randomUUID()

        given(getMyCardUseCase.getMyCard(any())).willReturn(cardResult(1L))

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}/cards/me", gatheringId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getMyCard",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("내 카드 조회")
                            .description("행사에서 내가 작성한 카드를 조회합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                            .responseFields(*cardFields())
                            .responseSchema(schema(CardResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("내 PIN 조회")
    fun getMyPinNumber() {
        val gatheringId = UUID.randomUUID()

        given(getMyPinNumberUseCase.getMyPinNumber(any())).willReturn(1234)

        val request = RestDocumentationRequestBuilders.get("/gatherings/{gatheringId}/cards/me/pin", gatheringId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getMyPinNumber",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("내 PIN 조회")
                            .description("내 카드를 열람할 수 있는 PIN 번호를 조회합니다.")
                            .pathParameters(parameterWithName("gatheringId").description("행사 ID"))
                            .responseFields(fieldWithPath("pinNumber").type(NUMBER).description("PIN 번호"))
                            .responseSchema(schema(MyPinNumberResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("PIN으로 카드 조회")
    fun getCardByPinNumber() {
        val gatheringId = UUID.randomUUID()
        val pinNumber = 1234

        given(getCardByPinNumberUseCase.getCardByPinNumber(any())).willReturn(cardResult(2L))

        val request = RestDocumentationRequestBuilders
            .get("/gatherings/{gatheringId}/cards/by-pin/{pinNumber}", gatheringId, pinNumber)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getCardByPinNumber",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("PIN으로 카드 조회")
                            .description("PIN 번호로 행사 참여자의 카드를 조회합니다.")
                            .pathParameters(
                                parameterWithName("gatheringId").description("행사 ID"),
                                parameterWithName("pinNumber").description("PIN 번호"),
                            )
                            .responseFields(*cardFields())
                            .responseSchema(schema(CardResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    private fun cardResult(cardId: Long): CardResult = CardResult(
        CardDisplay.FULL,
        cardId,
        "김주호",
        "test@test.com",
        listOf("https://github.com/test"),
        1,
        1,
        TEMPLATE_TEXT,
        mapOf("introduce" to "백엔드"),
    )

    private fun cardFields(): Array<FieldDescriptor> = arrayOf(
        fieldWithPath("type").type(STRING).description("카드 타입"),
        fieldWithPath("cardId").type(NUMBER).description("카드 ID"),
        fieldWithPath("name").type(STRING).description("사용자 이름"),
        fieldWithPath("email").type(STRING).description("이메일"),
        fieldWithPath("linkUrls").type("ARRAY").description("링크 URL 목록"),
        fieldWithPath("lastIntroduceTemplateVersion").type(NUMBER).description("최신 템플릿 버전"),
        fieldWithPath("nowIntroduceTemplateVersion").type(NUMBER).description("카드에 저장된 템플릿 버전"),
        fieldWithPath("introduceTemplateContentText").type(STRING).description("템플릿 원문"),
        subsectionWithPath("introductionText").type("OBJECT").description("자기소개 내용"),
    )

    companion object {
        private const val TEMPLATE_TEXT = "안녕하세요. 저는 \${introduce} 개발자입니다."
    }
}
