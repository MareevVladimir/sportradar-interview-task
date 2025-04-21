package org.sportradar.scoreboardlib;

import org.sportradar.scoreboardlib.exceptions.InvalidScoreException;

public record Stat (short score) {
    public Stat {
        if (score < 0) {
            InvalidScoreException.throwInvalidNegativeScore(score);
        }
    }
}
