package org.sportradar.scoreboardlib;

import org.junit.jupiter.api.Test;
import org.sportradar.scoreboardlib.exceptions.InvalidScoreException;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StatTest {
    @Test
    public void constructorWithPositiveScoreShouldNotThrowException() {
        Integer score = new Random().nextInt(0, 100);
        assertDoesNotThrow(() -> new Stat(score.shortValue()));
    }

    @Test
    public void constructorWithNegativeScoreShouldThrowInvalidScoreException() {
        Integer score = new Random().nextInt(-101, -1);
        assertThrows(InvalidScoreException.class, () -> new Stat(score.shortValue()));
    }
}