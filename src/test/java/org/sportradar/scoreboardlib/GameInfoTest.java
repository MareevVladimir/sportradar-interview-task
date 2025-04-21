package org.sportradar.scoreboardlib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameInfoTest {
    @Test
    public void constructorWithNullShouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new GameInfo(null, null, null));
    }
}