package sharev.link.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType.NUMBER
import com.epages.restdocs.apispec.SimpleType.STRING
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import sharev.ControllerTestSupport
import sharev.WithCustomMockUser
import sharev.link.adapter.inbound.web.dto.response.LinkResponse
import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.inbound.result.LinkResult

class LinkControllerTest : ControllerTestSupport() {

    @Test
    @WithCustomMockUser
    @DisplayName("링크 목록 조회")
    fun getAllLinks() {
        val response = listOf(
            LinkResult(1L, "https://github.com/sharev"),
            LinkResult(2L, "https://linkedin.com/in/sharev"),
        )

        given(getLinksUseCase.getLinks(GetLinksCommand(1L))).willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/accounts/links")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getAllLinks",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("링크 목록 조회")
                            .description("내 계정에 등록된 링크 목록을 조회합니다.")
                            .responseFields(
                                fieldWithPath("[].id").type(NUMBER).description("링크 ID"),
                                fieldWithPath("[].url").type(STRING).description("링크 URL"),
                            )
                            .responseSchema(schema(LinkResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }
}
