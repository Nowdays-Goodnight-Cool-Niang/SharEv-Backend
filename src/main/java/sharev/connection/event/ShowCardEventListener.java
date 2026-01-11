package sharev.connection.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sharev.connection.service.ConnectionService;

@Component
@RequiredArgsConstructor
public class ShowCardEventListener {

    private final ConnectionService connectionService;

    @Async
    @EventListener
    public void connect(ShowCardEvent showCardEvent) {
        connectionService.connect(showCardEvent.eventId(), showCardEvent.accountId(), showCardEvent.targetCardId());
    }
}
