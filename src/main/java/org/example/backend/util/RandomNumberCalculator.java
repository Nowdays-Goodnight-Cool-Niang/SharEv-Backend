package org.example.backend.util;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomNumberCalculator {
    public int getRandom(int startNumber, int endNumber) {

        if (startNumber > endNumber) {
            throw new RandomRangeException();
        }

        return ThreadLocalRandom.current()
                .nextInt(startNumber, endNumber + 1);
    }
}
