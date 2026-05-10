package sharev.link.adapter.inbound.event

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import sharev.account.domain.event.AccountLinkUpdatedEvent
import sharev.link.application.port.outbound.DeleteLinkPort
import sharev.link.application.port.outbound.SaveLinkPort

@Component
class AccountLinkUpdatedEventListener(
    private val saveLinkPort: SaveLinkPort,
    private val deleteLinkPort: DeleteLinkPort,
) {

    @EventListener
    fun handle(event: AccountLinkUpdatedEvent) {
        if (event.deleteLinkIds.isNotEmpty()) {
            deleteLinkPort.deleteAllByIds(event.accountId, event.deleteLinkIds)
        }

        if (event.addLinkUrls.isNotEmpty()) {
            saveLinkPort.saveAll(event.accountId, event.addLinkUrls)
        }
    }
}
