package sharev.common.adapter.outbound.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import sharev.common.application.port.outbound.PublishEventPort

@Component
class SpringPublisherEvent(
    private val applicationEventPublisher: ApplicationEventPublisher
) : PublishEventPort {

    override fun publish(event: Any) {
        applicationEventPublisher.publishEvent(event)
    }
}
