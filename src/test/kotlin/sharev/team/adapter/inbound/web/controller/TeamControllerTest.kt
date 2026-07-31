package sharev.team.adapter.inbound.web.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType.NUMBER
import com.epages.restdocs.apispec.SimpleType.STRING
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willThrow
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import sharev.ControllerTestSupport
import sharev.WithCustomMockUser
import sharev.member.domain.model.MemberRole
import sharev.team.adapter.inbound.web.dto.request.CreateTeamRequest
import sharev.team.adapter.inbound.web.dto.request.UpdateTeamRequest
import sharev.team.adapter.inbound.web.dto.response.TeamDetailResponse
import sharev.team.adapter.inbound.web.dto.response.TeamInfoResponse
import sharev.team.adapter.inbound.web.dto.response.TeamUpdateInfoResponse
import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.command.UpdateTeamInfoCommand
import sharev.team.application.port.inbound.result.*
import sharev.team.application.port.inbound.usecase.CreateTeamUseCase
import sharev.team.application.port.inbound.usecase.GetMyTeamsUseCase
import sharev.team.application.port.inbound.usecase.GetTeamDetailUseCase
import sharev.team.application.port.inbound.usecase.UpdateTeamInfoUseCase
import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.time.LocalDateTime

class TeamControllerTest : ControllerTestSupport() {
    @Test
    @WithCustomMockUser
    @DisplayName("팀 생성")
    fun createTeam() {
        val requestDto = CreateTeamRequest("새로운 팀")
        given(mockBean<CreateTeamUseCase>().create(CreateTeamCommand(1L, "새로운 팀")))
            .willReturn(CreateTeamResult(1L))

        val request = RestDocumentationRequestBuilders.post("/teams")
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(
                documentResource(
                    "createTeam",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("팀 생성")
                            .description("새로운 팀을 생성합니다. 생성한 사용자는 자동으로 ADMIN 권한을 부여받습니다.")
                            .requestFields(fieldWithPath("title").type(STRING).description("팀 이름"))
                            .requestSchema(schema(CreateTeamRequest::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("내 팀 목록 조회")
    fun getMyTeams() {
        val teams = listOf(
            TeamInfoResult(
                1L,
                "요즘잘자쿨냥이",
                "진짜 잘자",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                MemberRole.ADMIN.name,
                5,
            ),
            TeamInfoResult(
                2L,
                "아산행사",
                "아산에서 열리는 행사 관리팀",
                LocalDateTime.of(2025, 1, 2, 10, 0),
                MemberRole.COMMON.name,
                3,
            ),
        )

        given(mockBean<GetMyTeamsUseCase>().getMyTeams(GetMyTeamsCommand(1L)))
            .willReturn(teams)

        val request = RestDocumentationRequestBuilders.get("/teams")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getMyTeams",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("내 팀 목록 조회")
                            .description("현재 사용자가 속한 모든 팀의 목록을 조회합니다.")
                            .responseFields(
                                fieldWithPath("[].id").type(NUMBER).description("팀 ID"),
                                fieldWithPath("[].title").type(STRING).description("팀 이름"),
                                fieldWithPath("[].content").type(STRING).description("팀 설명").optional(),
                                fieldWithPath("[].createdAt").type(STRING).description("생성일시"),
                                fieldWithPath("[].memberRole").type(STRING).description("권한"),
                                fieldWithPath("[].headcount").type(NUMBER).description("팀 인원 수"),
                            )
                            .responseSchema(schema(TeamInfoResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀 상세 조회 실패 - 팀 미존재 혹은 속하지 않음")
    fun getTeamDetailFail() {
        given(mockBean<GetTeamDetailUseCase>().getTeamDetail(1L, 1L))
            .willThrow(TeamException(TeamExceptionCode.TEAM_NOT_FOUND))

        val request = RestDocumentationRequestBuilders.get("/teams/{teamId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀 상세 조회")
    fun getTeamDetail() {
        val response = TeamDetailResult(
            1L,
            "요즘잘자쿨냥이",
            "진짜 잘자",
            LocalDateTime.of(2025, 1, 1, 10, 0),
            2,
            listOf(
                GatheringInfoResult(
                    "Spring 밋업",
                    LocalDateTime.of(2025, 3, 20, 14, 0),
                    LocalDateTime.of(2025, 3, 20, 17, 0),
                    "서울 강남구",
                )
            ),
            listOf(TeamMemberInfoResult("김주호", "admin@test.com", MemberRole.ADMIN)),
        )

        given(mockBean<GetTeamDetailUseCase>().getTeamDetail(1L, 1L))
            .willReturn(response)

        val request = RestDocumentationRequestBuilders.get("/teams/{teamId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "getTeamDetail",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("팀 상세 조회")
                            .description("팀 상세 정보를 조회합니다. 팀 멤버만 조회할 수 있습니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .responseFields(
                                fieldWithPath("id").type(NUMBER).description("팀 ID"),
                                fieldWithPath("title").type(STRING).description("팀 이름"),
                                fieldWithPath("content").type(STRING).description("팀 설명").optional(),
                                fieldWithPath("createdAt").type(STRING).description("생성일시"),
                                fieldWithPath("headcount").type(NUMBER).description("팀 인원 수"),
                                fieldWithPath("gatherings").type("ARRAY").description("행사 목록"),
                                fieldWithPath("gatherings[].title").type(STRING).description("행사 제목"),
                                fieldWithPath("gatherings[].startAt").type(STRING).description("행사 시작일시"),
                                fieldWithPath("gatherings[].endAt").type(STRING).description("행사 종료일시"),
                                fieldWithPath("gatherings[].place").type(STRING).description("행사 장소").optional(),
                                fieldWithPath("members").type("ARRAY").description("멤버 목록"),
                                fieldWithPath("members[].name").type(STRING).description("멤버 이름"),
                                fieldWithPath("members[].email").type(STRING).description("멤버 이메일"),
                                fieldWithPath("members[].role").type(STRING).description("멤버 권한"),
                            )
                            .responseSchema(schema(TeamDetailResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀 정보 수정 실패 - 팀 미존재 혹은 속하지 않음")
    fun updateTeamInfoTeamFail() {
        val requestDto = UpdateTeamRequest("수정된 팀 이름")

        willThrow(TeamException(TeamExceptionCode.TEAM_NOT_FOUND))
            .given(mockBean<UpdateTeamInfoUseCase>())
            .updateTeamInfo(UpdateTeamInfoCommand(1L, 1L, requestDto.title))

        val request = RestDocumentationRequestBuilders.patch("/teams/{teamId}", 1L)
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀 정보 수정 실패 - 권한 없음")
    fun updateTeamInfoRoleFail() {
        val requestDto = UpdateTeamRequest("수정된 팀 이름")

        willThrow(TeamException(TeamExceptionCode.NOT_TEAM_ADMIN_MEMBER))
            .given(mockBean<UpdateTeamInfoUseCase>())
            .updateTeamInfo(UpdateTeamInfoCommand(1L, 1L, requestDto.title))

        val request = RestDocumentationRequestBuilders.patch("/teams/{teamId}", 1L)
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isForbidden())
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀 정보 수정")
    fun updateTeamInfo() {
        val updateTitle = "수정된 팀 이름"
        val requestDto = UpdateTeamRequest(updateTitle)

        given(mockBean<UpdateTeamInfoUseCase>().updateTeamInfo(UpdateTeamInfoCommand(1L, 1L, updateTitle)))
            .willReturn(TeamUpdateInfoResult(updateTitle))

        val request = RestDocumentationRequestBuilders.patch("/teams/{teamId}", 1L)
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                documentResource(
                    "updateTeamInfo",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("팀 정보 수정")
                            .description("팀 정보를 수정합니다. 팀 관리자만 수정할 수 있습니다.")
                            .pathParameters(parameterWithName("teamId").description("팀 ID"))
                            .requestFields(fieldWithPath("title").type(STRING).description("팀 이름"))
                            .responseFields(fieldWithPath("title").type(STRING).description("수정된 팀 이름"))
                            .requestSchema(schema(UpdateTeamRequest::class.java.simpleName))
                            .responseSchema(schema(TeamUpdateInfoResponse::class.java.simpleName))
                            .build()
                    )
                )
            )
    }
}
