package org.sportradar.scoreboardlib;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.time.Instant;

class GameByScoreComparatorTest {

    private final GameByScoreComparator comparator = new GameByScoreComparator();

    private Game gameWithScoreAndTime(String teamHome, String teamAway, int scoreHome, int scoreAway, Instant startTime) {
        return new Game(
                new GameInfo(startTime, new Team(teamHome), new Team(teamAway)),
                new GameStat(new Stat((short)scoreHome), new Stat((short) scoreAway))
        );
    }

    @Test
    void comparesByTotalScore() {
        Game game1 = gameWithScoreAndTime("Arsenal", "Barcelona", 1, 2, Instant.parse("2023-01-01T10:00:00Z")); // score 3
        Game game2 = gameWithScoreAndTime("Real Madrid", "Bayern Munich",2, 2, Instant.parse("2023-01-01T10:00:00Z")); // score 4

        assertTrue(comparator.compare(game1, game2) < 0);
        assertTrue(comparator.compare(game2, game1) > 0);
    }

    @Test
    void breaksTieWithStartTime() {
        Game game1 = gameWithScoreAndTime("Arsenal", "Barcelona", 2, 2, Instant.parse("2023-01-01T09:00:00Z"));
        Game game2 = gameWithScoreAndTime("Real Madrid", "Bayern Munich",2, 2, Instant.parse("2023-01-01T10:00:00Z"));

        assertTrue(comparator.compare(game1, game2) < 0);
        assertTrue(comparator.compare(game2, game1) > 0);
    }

    @Test
    void considersGamesEqualIfScoreAndTimeAreEqual() {
        Game game1 = gameWithScoreAndTime("Real Madrid", "Bayern Munich",2, 2, Instant.parse("2023-01-01T10:00:00Z"));
        Game game2 = gameWithScoreAndTime("Chelsea", "Juventus", 2, 2, Instant.parse("2023-01-01T10:00:00Z"));

        assertEquals(0, comparator.compare(game1, game2));
    }

    @Test
    void equalityCheckForComparatorItself() {
        assertEquals(new GameByScoreComparator(), comparator);
        assertNotEquals(comparator, new Object());
    }
}