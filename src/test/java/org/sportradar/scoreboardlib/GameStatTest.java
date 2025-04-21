package org.sportradar.scoreboardlib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStatTest {

    @Test
    public void constructorWithNullShouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new GameStat(null, null));
    }

}