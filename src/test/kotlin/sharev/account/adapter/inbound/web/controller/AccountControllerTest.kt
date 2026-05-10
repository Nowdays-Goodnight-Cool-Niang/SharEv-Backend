package sharev.account.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.SimpleType.NUMBER
import com.epages.restdocs.apispec.SimpleType.STRING
import com.epages.restdocs.apispec.ResourceSnippetParameters
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
import sharev.account.adapter.inbound.web.dto.request.DeleteAccountRequest
import sharev.account.adapter.inbound.web.dto.request.UpdateAccountInfoRequest
import sharev.account.adapter.inbound.web.dto.response.AccountInfoResponse
import sharev.account.adapter.inbound.web.dto.response.DeleteAccountResponse
import sharev.account.adapter.inbound.web.dto.response.UpdateAccountInfoResponse
import sharev.account.application.port.inbound.command.DeleteAccountCommand
import sharev.account.application.port.inbound.command.UpdateAccountInfoCommand
import sharev.account.application.port.inbound.result.DeleteAccountResult
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.account.domain.model.AccountRole
import java.time.LocalDateTime

class AccountControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("회원 정보 업데이트")
    fun updateAccountInfo() {
        val requestDto = UpdateAccountInfoRequest("김주호", "eora21@naver.com")
        val command = UpdateAccountInfoCommand(1L, "김주호", "eora21@naver.com")

        given(updateAccountInfoUseCase.updateAccountInfo(command)).willReturn(
            UpdateAccountInfoResult(1L, "김주호", "eora21@naver.com", AccountRole.USER, LocalDateTime.now())
        )

        val request = RestDocumentationRequestBuilders.patch("/accounts")
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                documentResource(
                    "updateAccountInfo",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("회원 정보 업데이트")
                            .description("자신의 정보를 갱신합니다.")
                            .requestFields(
                                fieldWithPath("name").type(STRING).description("회원 이름"),
                                fieldWithPath("email").type(STRING).description("이메일"),
                            )
                            .responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(STRING).description("회원 이름"),
                                fieldWithPath("email").type(STRING).description("이메일"),
                                fieldWithPath("updatedAt").type(STRING).description("수정 일시"),
                            )
                            .requestSchema(schema(UpdateAccountInfoRequest::class.java.simpleName))
                            .responseSchema(schema(UpdateAccountInfoResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("회원 정보 조회")
    fun getAccountInfo() {
        val request = RestDocumentationRequestBuilders.get("/accounts")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                documentResource(
                    "getAccountInfo",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("회원 정보 조회")
                            .description("자신의 정보를 조회합니다.")
                            .responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(STRING).description("회원 이름"),
                                fieldWithPath("email").type(STRING).description("이메일"),
                            )
                            .responseSchema(schema(AccountInfoResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("탈퇴")
    fun deleteAccountInfo() {
        val requestDto = DeleteAccountRequest("test")
        val command = DeleteAccountCommand(1L, "test")

        given(deleteAccountUseCase.delete(command)).willReturn(DeleteAccountResult(1L, LocalDateTime.now()))

        val request = RestDocumentationRequestBuilders.delete("/accounts")
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                documentResource(
                    "delete",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("탈퇴")
                            .description("서비스에서 탈퇴합니다.")
                            .requestFields(
                                fieldWithPath("feedback").type(STRING).optional().description("탈퇴 피드백"),
                            )
                            .responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("deletedAt").type(STRING).description("탈퇴 일시"),
                            )
                            .requestSchema(schema(DeleteAccountRequest::class.java.simpleName))
                            .responseSchema(schema(DeleteAccountResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }
}
