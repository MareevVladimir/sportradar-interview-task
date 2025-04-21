package org.sportradar.scoreboardlib.exceptions;

public class InvalidScoreException extends IllegalArgumentException {
    public InvalidScoreException(String message) {
        super(message);
    }

    public static void throwInvalidNegativeScore(short score) throws InvalidScoreException {
        throw new InvalidScoreException(String.format("Score can not be negative, but value[%d] provided", score));
    }
}
