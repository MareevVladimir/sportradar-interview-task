package org.sportradar.scoreboardlib;

import org.sportradar.scoreboardlib.exceptions.InvalidScoreException;
import org.sportradar.scoreboardlib.exceptions.StartGameException;

import java.util.Collection;

/**
 * Score board
 */
public interface IScoreBoard {

    /**
     * Starting the game
     * @param gameInfo game metadata
     * @return id of the game
     * @throws StartGameException if at least one of the team already playing
     */
    String startGame(GameInfo gameInfo) throws StartGameException;

    /**
     * Removes game from ScoreBoard
     * @param id id of the game
     */
    void finishGame(String id);

    /**
     * Updates the statistics of the game
     * @param id id of the game
     * @param stat game statistics
     */
    void updateStat(String id, GameStat stat);

    /**
     * Return list of games in order depending on implementation
     * @return Collection<IGame>
     */
    Collection<Game> getGames();
}