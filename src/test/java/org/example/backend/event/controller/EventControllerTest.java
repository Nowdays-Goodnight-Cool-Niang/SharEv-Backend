package org.example.backend.event.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.BOOLEAN;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.backend.ControllerTestSupport;
import org.example.backend.WithCustomMockUser;
import org.example.backend.profile.dto.request.RequestUpdateProfileInfoDto;
import org.example.backend.profile.dto.response.ResponseParticipantFlagDto;
import org.example.backend.profile.dto.response.ResponseProfileDto;
import org.example.backend.profile.dto.response.ResponseProfileInfoDto;
import org.example.backend.relation.dto.request.RequestUpdateRelationDto;
import org.example.backend.relation.dto.response.NonRelatedProfileDto;
import org.example.backend.relation.dto.response.RelationProfileDto;
import org.example.backend.relation.dto.response.ResponseRelationInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;

class EventControllerTest extends ControllerTestSupport {

    @Test
    @WithCustomMockUser
    @DisplayName("행사 참여")
    void join() throws Exception {

        // given
        doNothing()
                .when(profileService)
                .join(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/events/{eventId}/profiles", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())

                .andDo(document("join",
                        resource(ResourceSnippetParameters.builder()
                                .summary("행사 참여")
                                .description("행사에 참여하여 프로필을 생성합니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여할 이벤트 id")
                                )
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("프로필 수정")
    void updateInfo() throws Exception {

        // given
        RequestUpdateProfileInfoDto requestUpdateProfileInfoDto = new RequestUpdateProfileInfoDto("자기소개",
                "인상깊은 경험", "다시 겪고 싶은 경험");

        doAnswer(invocation -> new ResponseProfileInfoDto(invocation.getArgument(2), invocation.getArgument(3),
                invocation.getArgument(4)))
                .when(profileService).updateInfo(any(UUID.class), anyLong(), anyString(), anyString(), anyString());

        RequestBuilder request = RestDocumentationRequestBuilders
                .patch("/events/{eventId}/profiles", UUID.randomUUID())
                .content(objectMapper.writeValueAsString(requestUpdateProfileInfoDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("updateInfo",
                        resource(ResourceSnippetParameters.builder()
                                .summary("프로필 수정")
                                .description("프로필 정보를 수정합니다(이 항목은 추후 DB 변경에 따라 행사마다 다른 필드값을 지닐 수 있습니다).")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .requestFields(
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("인상깊은 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("다시 겪고 싶은 경험")
                                )
                                .responseFields(
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("인상깊은 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("다시 겪고 싶은 경험")
                                )
                                .requestSchema(schema(RequestUpdateProfileInfoDto.class.getSimpleName()))
                                .responseSchema(schema(ResponseProfileInfoDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("pin 번호로 프로필 획득")
    void getProfileByPinNumber() throws Exception {

        // given
        ResponseProfileDto responseProfileDto = new ResponseProfileDto(21L, "김주호", "eora21@naver.com", null,
                "https://github.com/eora21", null, 1111, 1, "자기소개", "뿌듯했던 경험", "힘들었던 경험", true);

        doReturn(responseProfileDto)
                .when(profileService).getProfileByPinNumber(any(UUID.class), anyLong(), anyInt());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/events/{eventId}/profiles/{pinNumber}", UUID.randomUUID(), 2840)
                .content(objectMapper.writeValueAsString(responseProfileDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("getProfileByPinNumber",
                        resource(ResourceSnippetParameters.builder()
                                .summary("pin 번호로 프로필 획득")
                                .description("pin 번호를 통해 프로필을 획득합니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id"),
                                        parameterWithName("pinNumber").description("상대방 PIN 번호")
                                )
                                .responseFields(
                                        fieldWithPath("type").type(STRING)
                                                .description("타입"),
                                        fieldWithPath("profileId").type(NUMBER)
                                                .description("참여 번호"),
                                        fieldWithPath("name").type(STRING)
                                                .description("이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("linkedinUrl").type(STRING)
                                                .description("링크드인 url"),
                                        fieldWithPath("githubUrl").type(STRING)
                                                .description("깃헙 url"),
                                        fieldWithPath("instagramUrl").type(STRING)
                                                .description("인스타그램 url"),
                                        fieldWithPath("iconNumber").type(NUMBER)
                                                .description("프로필 이미지 번호"),
                                        fieldWithPath("pinNumber").type(NUMBER)
                                                .description("상대방 PIN 번호"),
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("뿌듯했던 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("힘들었던 경험"),
                                        fieldWithPath("registerRequireFlag").type(BOOLEAN)
                                                .description("도감 등록 필요 유무")
                                )
                                .responseSchema(schema(ResponseProfileDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("본인 프로필 획득")
    void getMyProfile() throws Exception {

        // given
        ResponseProfileDto responseProfileDto = new ResponseProfileDto(21L, "김주호", "eora21@naver.com", null,
                "https://github.com/eora21", null, 1111, 1, "자기소개", "뿌듯했던 경험", "힘들었던 경험", true);

        doReturn(responseProfileDto)
                .when(profileService).getMyProfile(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/events/{eventId}/profiles", UUID.randomUUID())
                .content(objectMapper.writeValueAsString(responseProfileDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("getMyProfile",
                        resource(ResourceSnippetParameters.builder()
                                .summary("본인 프로필 획득")
                                .description("본인 프로필을 획득합니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .responseFields(
                                        fieldWithPath("type").type(STRING)
                                                .description("타입"),
                                        fieldWithPath("profileId").type(NUMBER)
                                                .description("참여 번호"),
                                        fieldWithPath("profileId").type(NUMBER)
                                                .description("참여 번호"),
                                        fieldWithPath("name").type(STRING)
                                                .description("이름"),
                                        fieldWithPath("email").type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("linkedinUrl").type(STRING)
                                                .description("링크드인 url"),
                                        fieldWithPath("githubUrl").type(STRING)
                                                .description("깃헙 url"),
                                        fieldWithPath("instagramUrl").type(STRING)
                                                .description("인스타그램 url"),
                                        fieldWithPath("iconNumber").type(NUMBER)
                                                .description("프로필 이미지 번호"),
                                        fieldWithPath("pinNumber").type(NUMBER)
                                                .description("본인 PIN 번호"),
                                        fieldWithPath("introduce").type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath("proudestExperience").type(STRING)
                                                .description("뿌듯했던 경험"),
                                        fieldWithPath("toughExperience").type(STRING)
                                                .description("힘들었던 경험"),
                                        fieldWithPath("registerRequireFlag").type(BOOLEAN)
                                                .description("도감 등록 필요 유무")
                                )
                                .responseSchema(schema(ResponseProfileDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("도감 등록")
    void register() throws Exception {

        // given
        RequestUpdateRelationDto requestUpdateRelationDto = new RequestUpdateRelationDto(4285);

        doNothing()
                .when(relationService).register(any(UUID.class), anyLong(), anyInt());

        RequestBuilder request = RestDocumentationRequestBuilders
                .post("/events/{eventId}/participants", UUID.randomUUID())
                .content(objectMapper.writeValueAsString(requestUpdateRelationDto))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())

                .andDo(document("register",
                        resource(ResourceSnippetParameters.builder()
                                .summary("도감 등록")
                                .description("상대방을 도감에 등록합니다. 도감은 양방향 관계로 구성됩니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .requestFields(
                                        fieldWithPath("targetPinNumber").type(NUMBER)
                                                .description("상대방 PIN 번호")
                                )
                                .requestSchema(schema(RequestUpdateRelationDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("참여자 목록 조회")
    void getParticipants() throws Exception {

        // given
        ResponseRelationInfoDto responseRelationInfoDto = new ResponseRelationInfoDto(1L,
                new PageImpl<>(List.of(
                        new RelationProfileDto(22L, "훈여정", "test@hun.com", null, null, null, 1,
                                "훈여정입니다.", "GDG에 쓰일 코드를 작성한 경험", "swagger를 위한 테스트코드를 짜던 기억", false),
                        new NonRelatedProfileDto("권나연", 2, "당근"),
                        new NonRelatedProfileDto("이유진", 3, "오늘의집")
                )));

        doReturn(responseRelationInfoDto)
                .when(relationService)
                .getParticipants(any(UUID.class), anyLong(), any(LocalDateTime.class), any(Pageable.class));

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/events/{eventId}/participants", UUID.randomUUID())
                .param("snapshotTime", "2025-07-17T22:45:32")
                .contentType(MediaType.APPLICATION_JSON);

        // when
        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("getParticipants",
                        resource(ResourceSnippetParameters.builder()
                                .summary("참여자 목록 확인")
                                .description(
                                        "참여자 목록을 확인합니다. 도감에 등록된 사람은 모든 정보를 볼 수 있지만, 도감에 등록되지 않은 사람은 이름만 확인할 수 있습니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .queryParameters(
                                        parameterWithName("snapshotTime").description(
                                                "조회 기준 시간입니다. yyyy-MM-ddTHH:mm:ss 형식으로 작성하시면 됩니다."),
                                        parameterWithName("size").optional().description("조회 페이지 사이즈입니다."),
                                        parameterWithName("page").optional().description("조회 페이지 숫자입니다."),
                                        parameterWithName("sort").optional().description("페이지 정렬 기준입니다(미구현).")
                                )
                                .responseFields(
                                        fieldWithPath("registerCount").type(NUMBER)
                                                .description("도감에 등록된 사람 수"),
                                        fieldWithPath("relationProfiles.content[].type").type(STRING)
                                                .description("타입"),
                                        fieldWithPath("relationProfiles.content[].profileId").optional()
                                                .type(NUMBER)
                                                .description("참여 번호"),
                                        fieldWithPath("relationProfiles.content[].name").type(STRING)
                                                .description("이름"),
                                        fieldWithPath("relationProfiles.content[].email").optional().type(STRING)
                                                .description("이메일"),
                                        fieldWithPath("relationProfiles.content[].linkedinUrl").optional().type(STRING)
                                                .description("링크드인 url"),
                                        fieldWithPath("relationProfiles.content[].githubUrl").optional().type(STRING)
                                                .description("깃헙 url"),
                                        fieldWithPath("relationProfiles.content[].instagramUrl").optional().type(STRING)
                                                .description("인스타그램 url"),
                                        fieldWithPath("relationProfiles.content[].iconNumber").type(NUMBER)
                                                .description("프로필 이미지 번호"),
                                        fieldWithPath("relationProfiles.content[].introduce").optional()
                                                .type(STRING)
                                                .description("자기소개"),
                                        fieldWithPath(
                                                "relationProfiles.content[].proudestExperience")
                                                .type(STRING)
                                                .description("뿌듯했던 경험"),
                                        fieldWithPath(
                                                "relationProfiles.content[].toughExperience").optional()
                                                .type(STRING)
                                                .description("힘들었던 경험"),
                                        fieldWithPath("relationProfiles.content[].relationFlag").optional()
                                                .type(BOOLEAN)
                                                .description("도감 등록 유무"),
                                        fieldWithPath("relationProfiles.page.size").type(NUMBER)
                                                .description("페이지 사이즈"),
                                        fieldWithPath("relationProfiles.page.number").type(NUMBER)
                                                .description("페이지 수"),
                                        fieldWithPath("relationProfiles.page.totalElements").type(NUMBER)
                                                .description("전체 참여자 수(자신 제외)"),
                                        fieldWithPath("relationProfiles.page.totalPages").type(NUMBER)
                                                .description("전체 페이지")
                                )
                                .responseSchema(schema(ResponseRelationInfoDto.class.getSimpleName()))
                                .build())));
    }

    @Test
    @WithCustomMockUser
    @DisplayName("행사 참여 유무 확인")
    void isParticipant() throws Exception {

        // given
        doReturn(new ResponseParticipantFlagDto(false))
                .when(profileService)
                .isParticipant(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/events/{eventId}", UUID.randomUUID())
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
                                        "행사에 참여했는지, 참여하지 않았는지 확인합니다.")
                                .pathParameters(
                                        parameterWithName("eventId").description("참여한 이벤트 id")
                                )
                                .responseFields(
                                        fieldWithPath("isParticipant").type(BOOLEAN)
                                                .description("행사 참여 유무")
                                )
                                .responseSchema(schema(ResponseParticipantFlagDto.class.getSimpleName()))
                                .build())));
    }
}
