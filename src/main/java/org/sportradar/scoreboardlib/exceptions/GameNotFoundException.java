package org.sportradar.scoreboardlib.exceptions;

public class GameNotFoundException extends IllegalArgumentException {
    public GameNotFoundException(String id) {
        super(String.format("Game with id[%s] not found", id));
    }
}
