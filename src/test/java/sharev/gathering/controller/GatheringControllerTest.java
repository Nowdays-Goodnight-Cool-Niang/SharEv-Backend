package sharev.gathering.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.BOOLEAN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import sharev.ControllerTestSupport;
import sharev.WithCustomMockUser;
import sharev.domain.card.dto.response.ResponseParticipantFlagDto;

class GatheringControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("행사 참여 유무 확인")
    void isJoined() throws Exception {

        // given
        doReturn(new ResponseParticipantFlagDto(false))
                .when(cardService)
                .isJoined(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/gatherings/{gatheringId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("isParticipant",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 참여 유무 확인")
                                .description(
                                        "사용자가 특정 행사에 참여했는지 확인합니다. " +
                                                "참여한 경우 true, 참여하지 않은 경우 false를 반환합니다. " +
                                                "이 API는 행사 참여 여부를 확인하여 카드 작성 가능 여부를 판단하는 데 사용됩니다.")
                                .pathParameters(
                                        parameterWithName("gatheringId").description("확인할 행사의 ID (UUID 형식)")
                                )
                                .responseFields(
                                        fieldWithPath("isParticipant").type(BOOLEAN)
                                                .description("행사 참여 유무 (true: 참여함, false: 참여하지 않음)")
                                )
                                .responseSchema(schema(ResponseParticipantFlagDto.class.getSimpleName()))
                                .build())));
    }
}
