package org.example.backend.event.util;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventKeyGenerator {

    private static final String EVENT_PIN_PREFIX = "event";
    private static final String EVENT_PIN_SUFFIX = "pin-numbers";

    public static String calculateEventPinKey(UUID eventId) {
        return String.join(":", EVENT_PIN_PREFIX, eventId.toString(), EVENT_PIN_SUFFIX);
    }
}
