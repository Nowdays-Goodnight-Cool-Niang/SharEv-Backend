package sharev.domain.account.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.domain.account.dto.request.RequestDeleteDto;
import sharev.domain.account.dto.request.RequestUpdateInfoDto;
import sharev.domain.account.dto.response.ResponseAccountInfo;
import sharev.domain.account.entity.Account;

class AccountControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("회원 정보 업데이트")
    void updateAccountInfo() throws Exception {

        // given
        doAnswer(invocation -> {
            Long accountId = invocation.getArgument(0);
            String name = invocation.getArgument(1);
            String email = invocation.getArgument(2);

            Account account = new Account(name, email);
            ReflectionTestUtils.setField(account, "id", accountId);

            return account;
        })
                .when(accountService)
                .updateAccountInfo(anyLong(), anyString(), anyString());

        RequestUpdateInfoDto requestUpdateInfoDto = new RequestUpdateInfoDto("김주호", "eora21@naver.com", 
                "https://linkedin.com/in/kimjooho",
                "https://github.com/eora21", 
                "https://instagram.com/kimjooho");

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/accounts")
                .content(objectMapper.writeValueAsString(requestUpdateInfoDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("updateAccountInfo",
                        resource(ResourceSnippetParameters.builder()
                                .summary("회원 정보 업데이트")
                                .description("자신의 정보를 갱신합니다. 링크 URL은 현재 Link 엔티티로 저장되며, 추후 별도 엔드포인트를 통해 조회할 수 있습니다.")
                                .requestFields(
                                        fieldWithPath("name").type(STRING)
                                                .description("회원 이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("linkedinUrl").type(STRING).optional()
                                                .description("링크드인 URL (선택사항)"),
                                        fieldWithPath("githubUrl").type(STRING).optional()
                                                .description("깃헙 URL (선택사항)"),
                                        fieldWithPath("instagramUrl").type(STRING).optional()
                                                .description("인스타그램 URL (선택사항)")
                                )
                                .requestSchema(schema(RequestUpdateInfoDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("회원 정보 조회")
    void getAccountInfo() throws Exception {

        // given
        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/accounts")
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("getAccountInfo",
                        resource(ResourceSnippetParameters.builder()
                                .summary("회원 정보 조회")
                                .description("자신의 정보를 조회합니다.")
                                .responseFields(
                                        fieldWithPath("id").type(NUMBER)
                                                .description("회원 id"),
                                        fieldWithPath("name").type(STRING)
                                                .description("회원 이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일")
                                )
                                .responseSchema(schema(ResponseAccountInfo.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("탈퇴")
    void deleteAccountInfo() throws Exception {

        // given
        RequestDeleteDto requestDeleteDto = new RequestDeleteDto("test");

        RequestBuilder request = RestDocumentationRequestBuilders
                .delete("/accounts")
                .content(objectMapper.writeValueAsString(requestDeleteDto))
                .contentType(MediaType.APPLICATION_JSON);

        doNothing()
                .when(accountService).delete(any(Account.class));

        doNothing()
                .when(accountService).saveFeedback(anyString());

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())

                .andDo(document("delete",
                        resource(ResourceSnippetParameters.builder()
                                .summary("탈퇴")
                                .description("서비스에서 탈퇴합니다.")
                                .requestFields(
                                        fieldWithPath("feedback").type(STRING).optional()
                                                .description("탈퇴회원이 남기는 피드백 내용")
                                )
                                .requestSchema(schema(RequestDeleteDto.class.getSimpleName()))
                                .build())));
    }
}
