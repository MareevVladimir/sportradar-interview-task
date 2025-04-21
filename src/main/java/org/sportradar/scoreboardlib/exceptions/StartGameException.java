package org.sportradar.scoreboardlib.exceptions;

import org.sportradar.scoreboardlib.Team;

public class StartGameException extends IllegalArgumentException {
    public StartGameException(String message) {
        super(message);
    }

    public static void throwGameByTeamAlreadyStarted(Team team) throws StartGameException {
        throw new StartGameException(String.format("Game by team[%s] already started", team));
    }

    public static void throwSameTeamArgumentsProvided(Team team) throws StartGameException {
        throw new StartGameException(String.format("Home and Away teams are the same objects team[%s]", team));
    }
}
