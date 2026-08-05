package sharev.member.adapter.inbound.web.controller

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
import sharev.member.adapter.inbound.web.dto.request.InviteMemberRequest
import sharev.member.adapter.inbound.web.dto.request.UpdateMemberRoleRequest
import sharev.member.adapter.inbound.web.dto.response.*
import sharev.member.application.port.inbound.command.*
import sharev.member.application.port.inbound.result.*
import sharev.member.application.port.inbound.usecase.*
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode

class MemberControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("멤버 목록 조회")
    fun getMembers() {
        val teamId = 1L
        val response = listOf(
            MemberResult(1L, "김주호", "admin@test.com", MemberRole.ADMIN, MemberStatus.ACTIVATE),
            MemberResult(2L, "홍길동", "hong@test.com", MemberRole.COMMON, MemberStatus.ACTIVATE),
            MemberResult(3L, "이영희", "lee@test.com", MemberRole.COMMON, MemberStatus.INVITE),
        )

        given(mockBean<GetMembersUseCase>().getMembers(GetMembersCommand(1L, teamId)))
            .willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/teams/{teamId}/members", teamId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getMembers",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("멤버 목록 조회")
                            .description("팀의 모든 멤버를 조회합니다. 팀 멤버만 조회할 수 있습니다. INVITE 상태의 멤버도 함께 반환됩니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .responseFields(
                                fieldWithPath("[].memberId").type(NUMBER).description("멤버 ID"),
                                fieldWithPath("[].name").type(STRING).description("사용자 이름"),
                                fieldWithPath("[].email").type(STRING).description("이메일"),
                                fieldWithPath("[].role").type(STRING).description("역할 (ADMIN, COMMON)"),
                                fieldWithPath("[].status").type(STRING)
                                    .description("상태 (INVITE, ACTIVATE, DEACTIVATE)"),
                            )
                            .responseSchema(schema(MemberResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 초대")
    fun invite() {
        val teamId = 1L
        val dto = InviteMemberRequest("new+user")

        given(mockBean<InviteMemberUseCase>().invite(InviteMemberCommand(1L, teamId, dto.handle)))
            .willReturn(InviteMemberResult(2L, MemberRole.COMMON, MemberStatus.INVITE))

        val request = RestDocumentationRequestBuilders.post("/teams/{teamId}/members", teamId)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "inviteMember",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("멤버 초대")
                            .description("handle로 팀에 멤버를 초대합니다. 팀 관리자만 초대할 수 있습니다. 초대된 멤버는 INVITE 상태로 생성됩니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .requestFields(fieldWithPath("handle").type(STRING).description("초대할 사용자의 handle"))
                            .responseFields(
                                fieldWithPath("memberId").type(NUMBER).description("생성된 멤버 ID"),
                                fieldWithPath("role").type(STRING).description("역할 (ADMIN, COMMON)"),
                                fieldWithPath("status").type(STRING).description("상태 (INVITE, ACTIVATE, DEACTIVATE)"),
                            )
                            .requestSchema(schema(InviteMemberRequest::class.java.simpleName))
                            .responseSchema(schema(InviteMemberResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 초대 실패 - 권한 없음")
    fun inviteFail() {
        val teamId = 1L
        val dto = InviteMemberRequest("newuser@test.com")

        given(mockBean<InviteMemberUseCase>().invite(InviteMemberCommand(1L, teamId, dto.handle)))
            .willThrow(TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE))

        val request = RestDocumentationRequestBuilders.post("/teams/{teamId}/members", teamId)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("초대 수락")
    fun acceptInvitation() {
        val teamId = 1L

        given(mockBean<AcceptInvitationUseCase>().acceptInvitation(AcceptInvitationCommand(1L, teamId)))
            .willReturn(AcceptInvitationResult(1L, MemberStatus.ACTIVATE))

        val request = RestDocumentationRequestBuilders.patch("/teams/{teamId}/members/me/accept", teamId)
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

    @Test
    @WithCustomMockUser
    @DisplayName("초대 거절 / 팀 탈퇴")
    fun leave() {
        val teamId = 1L

        given(mockBean<LeaveTeamUseCase>().leave(LeaveTeamCommand(1L, teamId)))
            .willReturn(LeaveTeamResult(1L))

        val request = RestDocumentationRequestBuilders.delete("/teams/{teamId}/members/me", teamId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "leaveTeam",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("초대 거절 / 팀 탈퇴")
                            .description("팀에서 탈퇴하거나 초대를 거절합니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .responseFields(fieldWithPath("memberId").type(NUMBER).description("멤버 ID"))
                            .responseSchema(schema(LeaveTeamResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("역할 변경")
    fun updateRole() {
        val teamId = 1L
        val memberId = 2L
        val role = MemberRole.ADMIN
        val dto = UpdateMemberRoleRequest(role)

        given(mockBean<UpdateMemberRoleUseCase>().updateRole(UpdateMemberRoleCommand(1L, teamId, memberId, role)))
            .willReturn(UpdateMemberRoleResult(memberId, role))

        val request = RestDocumentationRequestBuilders
            .patch("/teams/{teamId}/members/{memberId}/role", teamId, memberId)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "updateMemberRole",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("멤버 역할 변경")
                            .description("멤버의 역할을 변경합니다. 팀 관리자만 변경할 수 있습니다.")
                            .pathParameters(
                                parameterWithName("teamId").description("팀 ID"),
                                parameterWithName("memberId").description("대상 멤버 ID"),
                            )
                            .requestFields(fieldWithPath("role").type(STRING).description("변경할 역할 (ADMIN, COMMON)"))
                            .responseFields(
                                fieldWithPath("memberId").type(NUMBER).description("멤버 ID"),
                                fieldWithPath("role").type(STRING).description("변경된 역할 (ADMIN, COMMON)"),
                            )
                            .requestSchema(schema(UpdateMemberRoleRequest::class.java.simpleName))
                            .responseSchema(schema(UpdateMemberRoleResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("역할 변경 실패 - 권한 없음")
    fun updateRoleFail() {
        val teamId = 1L
        val memberId = 2L
        val role = MemberRole.ADMIN
        val dto = UpdateMemberRoleRequest(role)

        given(mockBean<UpdateMemberRoleUseCase>().updateRole(UpdateMemberRoleCommand(1L, teamId, memberId, role)))
            .willThrow(TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE))

        val request = RestDocumentationRequestBuilders
            .patch("/teams/{teamId}/members/{memberId}/role", teamId, memberId)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 제거")
    fun removeMember() {
        val teamId = 1L
        val memberId = 2L

        given(mockBean<RemoveMemberUseCase>().removeMember(RemoveMemberCommand(1L, teamId, memberId)))
            .willReturn(RemoveMemberResult(memberId))

        val request = RestDocumentationRequestBuilders.delete("/teams/{teamId}/members/{memberId}", teamId, memberId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "removeMember",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("멤버 제거")
                            .description("팀에서 멤버를 제거합니다. 팀 관리자만 제거할 수 있습니다.")
                            .pathParameters(
                                parameterWithName("teamId").description("팀 ID"),
                                parameterWithName("memberId").description("제거할 멤버 ID"),
                            )
                            .responseFields(fieldWithPath("memberId").type(NUMBER).description("제거된 멤버 ID"))
                            .responseSchema(schema(RemoveMemberResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 제거 실패 - 권한 없음")
    fun removeMemberFail() {
        val teamId = 1L
        val memberId = 2L

        given(mockBean<RemoveMemberUseCase>().removeMember(RemoveMemberCommand(1L, teamId, memberId)))
            .willThrow(TeamException(TeamExceptionCode.UNAUTHORIZED_TEAM_MANAGE))

        val request = RestDocumentationRequestBuilders.delete("/teams/{teamId}/members/{memberId}", teamId, memberId)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }
}
