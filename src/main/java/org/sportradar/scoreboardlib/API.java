package org.sportradar.scoreboardlib;

import org.sportradar.scoreboardlib.exceptions.GameNotFoundException;
import org.sportradar.scoreboardlib.exceptions.InvalidScoreException;
import org.sportradar.scoreboardlib.exceptions.StartGameException;

import java.time.Instant;
import java.util.*;

/**
 * Main class to use in ScoreBoardLib. Better to use as one instance
 */
public class API {
    private final IScoreBoard scoreBoard;

    public API() {
        scoreBoard = new ScoreBoard(new GameByScoreComparator(), new GameIdProvider());
    }

    /**
     * Returns a single scoreboard per API instance
     * @return IScoreBoard
     */
    public IScoreBoard getScoreBoard() {
        return scoreBoard;
    }
}
