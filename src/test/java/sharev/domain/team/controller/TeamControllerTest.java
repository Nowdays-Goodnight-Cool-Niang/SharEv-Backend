package sharev.domain.team.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.domain.team.dto.request.RequestCreateTeamDto;
import sharev.domain.team.dto.request.RequestUpdateTeamDto;
import sharev.domain.team.dto.response.ResponseTeamInfoDto;

class TeamControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("팀 생성")
    void createTeam() throws Exception {
        RequestCreateTeamDto requestDto = new RequestCreateTeamDto("새로운 팀");

        doNothing().when(teamService).create(any(), anyString());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/teams")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("createTeam",
                        resource(ResourceSnippetParameters.builder()
                                .summary("팀 생성")
                                .description("새로운 팀을 생성합니다. 생성한 사용자는 자동으로 ADMIN 권한을 부여받습니다.")
                                .requestFields(
                                        fieldWithPath("title").type(STRING).description("팀 이름"))
                                .requestSchema(schema(RequestCreateTeamDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("내 팀 목록 조회")
    void getMyTeams() throws Exception {
        List<ResponseTeamInfoDto> teams = List.of(
                new ResponseTeamInfoDto(1L, "개발팀", "개발 관련 팀", LocalDateTime.now(), 5),
                new ResponseTeamInfoDto(2L, "기획팀", "기획 관련 팀", LocalDateTime.now(), 3)
        );

        doReturn(teams).when(teamService).getMyTeams(any());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/teams")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("getMyTeams",
                        resource(ResourceSnippetParameters.builder()
                                .summary("내 팀 목록 조회")
                                .description("현재 사용자가 속한 모든 팀의 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("[].id").type(NUMBER).description("팀 ID"),
                                        fieldWithPath("[].title").type(STRING).description("팀 이름"),
                                        fieldWithPath("[].content").type(STRING).description("팀 설명").optional(),
                                        fieldWithPath("[].createdAt").type(STRING).description("생성일시"),
                                        fieldWithPath("[].headcount").type(NUMBER).description("팀 인원 수"))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("팀 정보 수정")
    void updateTeamInfo() throws Exception {
        Long teamId = 1L;
        RequestUpdateTeamDto requestDto = new RequestUpdateTeamDto("수정된 팀 이름");

        doReturn(true).when(teamService).isMember(any(), anyLong());
        doNothing().when(teamService).updateTeamInfo(anyLong(), anyString());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/teams/{teamId}", teamId)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("updateTeamInfo",
                        resource(ResourceSnippetParameters.builder()
                                .summary("팀 정보 수정")
                                .description("팀 정보를 수정합니다. 팀 멤버만 수정할 수 있습니다.")
                                .pathParameters(
                                        parameterWithName("teamId").description("팀 ID"))
                                .requestFields(
                                        fieldWithPath("title").type(STRING).description("팀 이름"))
                                .requestSchema(schema(RequestUpdateTeamDto.class.getSimpleName()))
                                .build())));
    }
}
