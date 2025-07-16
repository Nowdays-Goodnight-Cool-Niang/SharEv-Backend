package org.example.backend.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomNumberCalculator {
    private final Random random = new Random();

    public int getRandom(int startNumber, int endNumber) {
        return random.nextInt(startNumber, endNumber + 1);
    }
}
