package sharev.member.adapter.inbound.web.participant.controller

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
import sharev.member.adapter.inbound.web.participant.dto.response.AcceptInvitationResponse
import sharev.member.application.port.inbound.command.AcceptInvitationCommand
import sharev.member.application.port.inbound.result.AcceptInvitationResult
import sharev.member.application.port.inbound.usecase.AcceptInvitationUseCase
import sharev.member.domain.model.MemberStatus

class MemberControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("초대 수락")
    fun acceptInvitation() {
        val teamId = 1L

        given(mockBean<AcceptInvitationUseCase>().acceptInvitation(AcceptInvitationCommand(1L, teamId)))
            .willReturn(AcceptInvitationResult(1L, MemberStatus.ACTIVATE))

        val request = RestDocumentationRequestBuilders.patch("/members/invitations/{teamId}", teamId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "acceptInvitation",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("초대 수락")
                            .description("팀 초대를 수락합니다. INVITE 상태인 본인의 멤버십을 ACTIVATE로 변경합니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .responseFields(
                                fieldWithPath("memberId").type(NUMBER).description("멤버 ID"),
                                fieldWithPath("status").type(STRING).description("상태 (INVITE, ACTIVATE, DEACTIVATE)"),
                            )
                            .responseSchema(schema(AcceptInvitationResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }
}
