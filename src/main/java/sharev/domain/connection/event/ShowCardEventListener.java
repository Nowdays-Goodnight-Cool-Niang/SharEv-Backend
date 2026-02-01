package sharev.domain.connection.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sharev.domain.connection.service.ConnectionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowCardEventListener {

    private final ConnectionService connectionService;

    @Async
    @EventListener
    public void connect(ShowCardEvent showCardEvent) {
        try {
            connectionService.connect(showCardEvent.eventId(), showCardEvent.accountId(), showCardEvent.targetCardId());

        } catch (Exception e) {
            log.error("Failed to process ShowCardEvent - eventId: {}, accountId: {}, targetCardId: {}",
                    showCardEvent.eventId(), showCardEvent.accountId(), showCardEvent.targetCardId(), e);
        }
    }
}
