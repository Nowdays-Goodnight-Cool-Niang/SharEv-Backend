package sharev.domain.member.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.domain.account.entity.Account;
import sharev.domain.member.dto.request.RequestInviteMemberDto;
import sharev.domain.member.dto.request.RequestUpdateMemberRoleDto;
import sharev.domain.member.dto.response.ResponseMemberDto;
import sharev.domain.member.entity.MemberRoleType;
import sharev.domain.member.entity.MemberStatusType;

class MemberControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 목록 조회")
    void getMembers() throws Exception {
        Long teamId = 1L;

        List<ResponseMemberDto> response = List.of(
                new ResponseMemberDto(1L, "김주호", "admin@test.com", MemberRoleType.ADMIN, MemberStatusType.ACTIVATE),
                new ResponseMemberDto(2L, "홍길동", "hong@test.com", MemberRoleType.COMMON, MemberStatusType.ACTIVATE),
                new ResponseMemberDto(3L, "이영희", "lee@test.com", MemberRoleType.COMMON, MemberStatusType.INVITE)
        );

        doReturn(true).when(teamService).isMember(any(Account.class), anyLong());
        doReturn(response).when(memberService).getMembers(anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/teams/{teamId}/members", teamId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getMembers",
                        resource(ResourceSnippetParameters.builder()
                                .summary("멤버 목록 조회")
                                .description("팀의 모든 멤버를 조회합니다. 팀 멤버만 조회할 수 있습니다. " +
                                        "INVITE 상태의 멤버(초대 대기중)도 함께 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"))
                                .responseFields(
                                        fieldWithPath("[].memberId").type(NUMBER).description("멤버 ID"),
                                        fieldWithPath("[].name").type(STRING).description("사용자 이름"),
                                        fieldWithPath("[].email").type(STRING).description("이메일"),
                                        fieldWithPath("[].role").type(STRING).description("역할 (ADMIN, COMMON)"),
                                        fieldWithPath("[].status").type(STRING)
                                                .description("상태 (INVITE, ACTIVATE, DEACTIVATE)"))
                                .responseSchema(schema(ResponseMemberDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 초대")
    void invite() throws Exception {
        Long teamId = 1L;
        RequestInviteMemberDto dto = new RequestInviteMemberDto("newuser@test.com");

        doReturn(true).when(memberService).isAdmin(any(Account.class), anyLong());
        doNothing().when(memberService).invite(any(Account.class), anyLong(), anyString());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/teams/{teamId}/members", teamId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("inviteMember",
                        resource(ResourceSnippetParameters.builder()
                                .summary("멤버 초대(임시)")
                                .description("이메일로 팀에 멤버를 초대합니다(확정 아님). 팀 관리자만 초대할 수 있습니다. " +
                                        "초대된 멤버는 INVITE 상태로 생성되며, 초대 수락 후 ACTIVATE 상태로 전환됩니다. " +
                                        "이미 팀에 존재하는 멤버를 초대하면 400 에러가 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"))
                                .requestFields(
                                        fieldWithPath("email").type(STRING).description("초대할 사용자의 이메일"))
                                .requestSchema(schema(RequestInviteMemberDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 초대 실패 - 권한 없음")
    void inviteFail() throws Exception {
        Long teamId = 1L;
        RequestInviteMemberDto dto = new RequestInviteMemberDto("newuser@test.com");

        doReturn(false).when(memberService).isAdmin(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/teams/{teamId}/members", teamId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("초대 수락")
    void acceptInvitation() throws Exception {
        Long teamId = 1L;

        doReturn(true).when(teamService).isMember(any(Account.class), anyLong());
        doNothing().when(memberService).acceptInvitation(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/teams/{teamId}/members/me/accept", teamId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("acceptInvitation",
                        resource(ResourceSnippetParameters.builder()
                                .summary("초대 수락")
                                .description("팀 초대를 수락합니다. INVITE 상태인 본인의 멤버십을 ACTIVATE로 변경합니다. " +
                                        "이미 ACTIVATE 상태이면 400 에러가 반환됩니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("초대 거절 / 팀 탈퇴")
    void leave() throws Exception {
        Long teamId = 1L;

        doReturn(true).when(teamService).isMember(any(Account.class), anyLong());
        doNothing().when(memberService).leave(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .delete("/teams/{teamId}/members/me", teamId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("leaveTeam",
                        resource(ResourceSnippetParameters.builder()
                                .summary("초대 거절 / 팀 탈퇴")
                                .description("팀에서 탈퇴하거나 초대를 거절합니다. " +
                                        "INVITE 상태이면 초대 거절, ACTIVATE 상태이면 팀 탈퇴로 처리됩니다. " +
                                        "마지막 관리자인 경우 탈퇴할 수 없습니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("역할 변경")
    void updateRole() throws Exception {
        Long teamId = 1L;
        Long memberId = 2L;
        RequestUpdateMemberRoleDto dto = new RequestUpdateMemberRoleDto(MemberRoleType.ADMIN);

        doReturn(true).when(memberService).isAdmin(any(Account.class), anyLong());
        doNothing().when(memberService).updateRole(anyLong(), anyLong(), any(MemberRoleType.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/teams/{teamId}/members/{memberId}/role", teamId, memberId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("updateMemberRole",
                        resource(ResourceSnippetParameters.builder()
                                .summary("멤버 역할 변경")
                                .description("멤버의 역할을 변경합니다. 팀 관리자만 변경할 수 있습니다. " +
                                        "마지막 관리자의 역할을 COMMON으로 변경할 수 없습니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"),
                                        parameterWithName("memberId").description("대상 멤버 ID"))
                                .requestFields(
                                        fieldWithPath("role").type(STRING).description("변경할 역할 (ADMIN, COMMON)"))
                                .requestSchema(schema(RequestUpdateMemberRoleDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("역할 변경 실패 - 권한 없음")
    void updateRoleFail() throws Exception {
        Long teamId = 1L;
        Long memberId = 2L;
        RequestUpdateMemberRoleDto dto = new RequestUpdateMemberRoleDto(MemberRoleType.ADMIN);

        doReturn(false).when(memberService).isAdmin(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/teams/{teamId}/members/{memberId}/role", teamId, memberId)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 제거")
    void removeMember() throws Exception {
        Long teamId = 1L;
        Long memberId = 2L;

        doReturn(true).when(memberService).isAdmin(any(Account.class), anyLong());
        doNothing().when(memberService).removeMember(any(Account.class), anyLong(), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .delete("/teams/{teamId}/members/{memberId}", teamId, memberId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("removeMember",
                        resource(ResourceSnippetParameters.builder()
                                .summary("멤버 제거")
                                .description("팀에서 멤버를 제거합니다. 팀 관리자만 제거할 수 있습니다. " +
                                        "본인을 제거할 수 없으며(탈퇴 사용), 마지막 관리자를 제거할 수 없습니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"),
                                        parameterWithName("memberId").description("제거할 멤버 ID"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("멤버 제거 실패 - 권한 없음")
    void removeMemberFail() throws Exception {
        Long teamId = 1L;
        Long memberId = 2L;

        doReturn(false).when(memberService).isAdmin(any(Account.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .delete("/teams/{teamId}/members/{memberId}", teamId, memberId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
