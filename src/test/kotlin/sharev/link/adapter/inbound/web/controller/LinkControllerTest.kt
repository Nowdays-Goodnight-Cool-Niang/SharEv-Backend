package sharev.link.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
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
import sharev.link.adapter.inbound.web.dto.request.CreateLinkRequest
import sharev.link.adapter.inbound.web.dto.response.CreateLinkResponse
import sharev.link.adapter.inbound.web.dto.response.DeleteLinkResponse
import sharev.link.adapter.inbound.web.dto.response.LinkResponse
import sharev.link.application.port.inbound.command.CreateLinkCommand
import sharev.link.application.port.inbound.command.DeleteLinkCommand
import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.inbound.result.CreateLinkResult
import sharev.link.application.port.inbound.result.DeleteLinkResult
import sharev.link.application.port.inbound.result.LinkResult

class LinkControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("링크 생성")
    fun addLink() {
        val requestDto = CreateLinkRequest("https://github.com/sharev")
        val command = CreateLinkCommand(1L, requestDto.url)

        given(createLinkUseCase.create(command)).willReturn(CreateLinkResult(1L, requestDto.url))

        val request = RestDocumentationRequestBuilders.post("/accounts/links")
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(
                documentResource(
                    "addLink",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("링크 생성")
                            .description("내 계정에 외부 링크를 추가합니다.")
                            .requestFields(
                                fieldWithPath("url").type(STRING).description("추가할 링크 URL"),
                            )
                            .responseFields(
                                fieldWithPath("id").type(NUMBER).description("링크 ID"),
                                fieldWithPath("url").type(STRING).description("링크 URL"),
                            )
                            .requestSchema(schema(CreateLinkRequest::class.java.simpleName))
                            .responseSchema(schema(CreateLinkResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

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

    @Test
    @WithCustomMockUser
    @DisplayName("링크 삭제")
    fun deleteLink() {
        val linkId = 1L

        given(deleteLinkUseCase.delete(DeleteLinkCommand(1L, linkId))).willReturn(DeleteLinkResult(linkId))

        val request = RestDocumentationRequestBuilders.delete("/accounts/links/{linkId}", linkId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "deleteLink",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("링크 삭제")
                            .description("내 계정에 등록된 링크를 삭제합니다.")
                            .pathParameters(parameterWithName("linkId").description("링크 ID"))
                            .responseFields(fieldWithPath("linkId").type(NUMBER).description("삭제된 링크 ID"))
                            .responseSchema(schema(DeleteLinkResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }
}
