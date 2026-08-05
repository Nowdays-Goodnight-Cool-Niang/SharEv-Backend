package sharev.team.adapter.inbound.event

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import sharev.account.domain.event.AccountSignupEvent
import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.usecase.CreateTeamUseCase
import sharev.team.domain.model.TeamType

@Component
class AccountSignupEventListener(
    private val createTeamUseCase: CreateTeamUseCase,
) {

    @EventListener
    fun handle(event: AccountSignupEvent) {
        createTeamUseCase.create(CreateTeamCommand(event.accountId, null, "", TeamType.PERSONAL))
    }
}
