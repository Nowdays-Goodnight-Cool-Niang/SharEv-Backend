package sharev.account.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType.NUMBER
import com.epages.restdocs.apispec.SimpleType.STRING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.security.core.authority.FactorGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import sharev.ControllerTestSupport
import sharev.WithCustomMockUser
import sharev.account.adapter.inbound.web.dto.request.DeleteAccountRequest
import sharev.account.adapter.inbound.web.dto.request.UpdateAccountHandleRequest
import sharev.account.adapter.inbound.web.dto.request.UpdateAccountInfoRequest
import sharev.account.adapter.inbound.web.dto.response.AccountInfoResponse
import sharev.account.adapter.inbound.web.dto.response.DeleteAccountResponse
import sharev.account.adapter.inbound.web.dto.response.UpdateAccountHandleResponse
import sharev.account.adapter.inbound.web.dto.response.UpdateAccountInfoResponse
import sharev.account.application.port.inbound.command.DeleteAccountCommand
import sharev.account.application.port.inbound.command.UpdateAccountHandleCommand
import sharev.account.application.port.inbound.command.UpdateAccountInfoCommand
import sharev.account.application.port.inbound.result.DeleteAccountResult
import sharev.account.application.port.inbound.result.UpdateAccountHandleResult
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.account.application.port.inbound.usecase.DeleteAccountUseCase
import sharev.account.application.port.inbound.usecase.UpdateAccountHandleUseCase
import sharev.account.application.port.inbound.usecase.UpdateAccountInfoUseCase
import sharev.common.adapter.inbound.security.model.AccountPrincipal

class AccountControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("회원 정보 업데이트")
    fun updateAccountInfo() {
        val requestDto = UpdateAccountInfoRequest("김주호", "eora21@naver.com", setOf("https://link.com"), setOf(1L))
        val command = UpdateAccountInfoCommand(1L, "김주호", "eora21@naver.com", setOf("https://link.com"), setOf(1L))

        given(mockBean<UpdateAccountInfoUseCase>().updateAccountInfo(command)).willReturn(
            UpdateAccountInfoResult(1L, "김주호", "eora21@naver.com")
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
                                fieldWithPath("addLinkUrls[]").type(STRING).description("추가할 링크 URL 목록"),
                                fieldWithPath("deleteLinkIds[]").type(NUMBER).description("삭제할 링크 ID 목록"),
                            )
                            .responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(STRING).description("회원 이름"),
                                fieldWithPath("email").type(STRING).description("이메일"),
                            )
                            .requestSchema(schema(UpdateAccountInfoRequest::class.java.simpleName))
                            .responseSchema(schema(UpdateAccountInfoResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("회원 정보 업데이트 후에도 세션의 인증 정보는 로그인 인증 요소와 요청 정보를 유지한다")
    fun updateAccountInfo_keepsFactorAuthorityAndDetails() {
        val principal = AccountPrincipal(1L, "USER", "test", "test@test.com", "handle", emptyMap())
        val details = WebAuthenticationDetails("127.0.0.1", "session-id")
        val token = OAuth2AuthenticationToken(
            principal,
            principal.authorities + FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.AUTHORIZATION_CODE_AUTHORITY),
            "kakao",
        ).apply { this.details = details }

        val requestDto = UpdateAccountInfoRequest("김주호", "eora21@naver.com", setOf("https://link.com"), setOf(1L))
        val command = UpdateAccountInfoCommand(1L, "김주호", "eora21@naver.com", setOf("https://link.com"), setOf(1L))

        given(mockBean<UpdateAccountInfoUseCase>().updateAccountInfo(command)).willReturn(
            UpdateAccountInfoResult(1L, "김주호", "eora21@naver.com")
        )

        val request = RestDocumentationRequestBuilders.patch("/accounts")
            .with(authentication(token))
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        val session = mockMvc.perform(request)
            .andExpect(status().isOk)
            .andReturn()
            .request
            .session

        val context = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY) as SecurityContext
        val authentication = context.authentication as OAuth2AuthenticationToken

        assertThat((authentication.principal as AccountPrincipal).accountName).isEqualTo("김주호")
        assertThat(authentication.authorities.map { it.authority })
            .containsExactlyInAnyOrder("ROLE_USER", "ROLE_VERIFIED", FactorGrantedAuthority.AUTHORIZATION_CODE_AUTHORITY)
        assertThat(authentication.details).isEqualTo(details)
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

        given(mockBean<DeleteAccountUseCase>().delete(command))
            .willReturn(DeleteAccountResult(1L))

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
                            )
                            .requestSchema(schema(DeleteAccountRequest::class.java.simpleName))
                            .responseSchema(schema(DeleteAccountResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser(handle = "") // handle null 처리
    @DisplayName("handle 미등록 시 VERIFIED 필요 엔드포인트에서 거부")
    fun unverifiedUserIsDenied() {
        val request = RestDocumentationRequestBuilders.get("/accounts")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
    }

    @Test
    @WithCustomMockUser(handle = "")
    @DisplayName("handle 미등록 유저도 handle 업데이트는 허용")
    fun unverifiedUserCanRegisterHandle() {
        val requestDto = UpdateAccountHandleRequest("new_handle")
        given(mockBean<UpdateAccountHandleUseCase>().updateAccountHandle(UpdateAccountHandleCommand(1L, "new_handle")))
            .willReturn(UpdateAccountHandleResult("new_handle"))

        val request = RestDocumentationRequestBuilders.patch("/accounts/handle")
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                documentResource(
                    "updateHandle",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("핸들 등록/수정")
                            .description("핸들을 등록/수정합니다. 핸들 미등록 사용자도 접근할 수 있습니다.")
                            .requestFields(
                                fieldWithPath("handle").type(STRING).description("등록할 핸들 (영문·숫자·언더바 4~20자)"),
                            )
                            .responseFields(
                                fieldWithPath("handle").type(STRING).description("등록된 핸들"),
                            )
                            .requestSchema(schema(UpdateAccountHandleRequest::class.java.simpleName))
                            .responseSchema(schema(UpdateAccountHandleResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }
}
