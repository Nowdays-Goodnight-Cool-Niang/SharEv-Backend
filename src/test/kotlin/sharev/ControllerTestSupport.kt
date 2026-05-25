package sharev

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippet
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import sharev.account.adapter.inbound.web.controller.AccountController
import sharev.account.application.port.inbound.usecase.DeleteAccountUseCase
import sharev.account.application.port.inbound.usecase.UpdateAccountInfoUseCase
import sharev.card.adapter.inbound.web.controller.CardController
import sharev.card.application.port.inbound.usecase.*
import sharev.config.SecurityConfig
import sharev.gathering.adapter.inbound.web.controller.GatheringController
import sharev.gathering.application.port.inbound.usecase.*
import sharev.link.adapter.inbound.web.controller.LinkController
import sharev.link.application.port.inbound.usecase.GetLinksUseCase
import sharev.member.adapter.inbound.web.controller.MemberController
import sharev.member.application.port.inbound.usecase.*
import sharev.team.adapter.inbound.web.controller.TeamController
import sharev.team.application.port.inbound.usecase.CreateTeamUseCase
import sharev.team.application.port.inbound.usecase.GetMyTeamsUseCase
import sharev.team.application.port.inbound.usecase.GetTeamDetailUseCase
import sharev.team.application.port.inbound.usecase.UpdateTeamInfoUseCase

@WebMvcTest(
    controllers = [
        AccountController::class,
        CardController::class,
        GatheringController::class,
        LinkController::class,
        MemberController::class,
        TeamController::class,
    ]
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(
    SecurityConfig::class,
)
abstract class ControllerTestSupport {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockitoBean
    protected lateinit var updateAccountInfoUseCase: UpdateAccountInfoUseCase

    @MockitoBean
    protected lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @MockitoBean
    protected lateinit var joinCardUseCase: JoinCardUseCase

    @MockitoBean
    protected lateinit var updateCardInfoUseCase: UpdateCardInfoUseCase

    @MockitoBean
    protected lateinit var getAllCardsUseCase: GetAllCardsUseCase

    @MockitoBean
    protected lateinit var getMyCardUseCase: GetMyCardUseCase

    @MockitoBean
    protected lateinit var getMyPinNumberUseCase: GetMyPinNumberUseCase

    @MockitoBean
    protected lateinit var getCardByPinNumberUseCase: GetCardByPinNumberUseCase

    @MockitoBean
    protected lateinit var createGatheringUseCase: CreateGatheringUseCase

    @MockitoBean
    protected lateinit var getTeamGatheringUseCase: GetTeamGatheringUseCase

    @MockitoBean
    protected lateinit var updateGatheringUseCase: UpdateGatheringUseCase

    @MockitoBean
    protected lateinit var deleteGatheringUseCase: DeleteGatheringUseCase

    @MockitoBean
    protected lateinit var getIntroduceTemplateUseCase: GetIntroduceTemplateUseCase

    @MockitoBean
    protected lateinit var checkGatheringParticipantUseCase: CheckGatheringParticipantUseCase

    @MockitoBean
    protected lateinit var getParticipatedGatheringsUseCase: GetParticipatedGatheringsUseCase

    @MockitoBean
    protected lateinit var getGatheringsUseCase: GetGatheringsUseCase

    @MockitoBean
    protected lateinit var getLinksUseCase: GetLinksUseCase

    @MockitoBean
    protected lateinit var getMembersUseCase: GetMembersUseCase

    @MockitoBean
    protected lateinit var inviteMemberUseCase: InviteMemberUseCase

    @MockitoBean
    protected lateinit var acceptInvitationUseCase: AcceptInvitationUseCase

    @MockitoBean
    protected lateinit var leaveTeamUseCase: LeaveTeamUseCase

    @MockitoBean
    protected lateinit var updateMemberRoleUseCase: UpdateMemberRoleUseCase

    @MockitoBean
    protected lateinit var removeMemberUseCase: RemoveMemberUseCase

    @MockitoBean
    protected lateinit var checkTeamAdminUseCase: CheckTeamAdminUseCase

    @MockitoBean
    protected lateinit var createTeamUseCase: CreateTeamUseCase

    @MockitoBean
    protected lateinit var getMyTeamsUseCase: GetMyTeamsUseCase

    @MockitoBean
    protected lateinit var getTeamDetailUseCase: GetTeamDetailUseCase

    @MockitoBean
    protected lateinit var updateTeamInfoUseCase: UpdateTeamInfoUseCase

    protected fun documentResource(
        identifier: String,
        resourceSnippet: ResourceSnippet
    ): RestDocumentationResultHandler =
        MockMvcRestDocumentationWrapper.document(identifier, snippets = arrayOf(resourceSnippet))

    protected fun schema(name: String): Schema = Schema.schema(name)
}
