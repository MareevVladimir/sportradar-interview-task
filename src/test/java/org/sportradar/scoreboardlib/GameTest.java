package org.sportradar.scoreboardlib;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    @Test
    public void constructorWithNullShouldThrowNPE() {
        assertDoesNotThrow(() -> new Game(Mockito.mock(GameInfo.class), Mockito.mock(GameStat.class)));
        assertThrows(NullPointerException.class, () -> new Game(null, Mockito.mock(GameStat.class)));
        assertThrows(NullPointerException.class, () -> new Game(Mockito.mock(GameInfo.class), null));
    }

}