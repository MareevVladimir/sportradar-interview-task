package org.sportradar.scoreboardlib;

import java.util.Comparator;

/**
 * GameByScoreComparator
 * compare 2 games by score or by started date
 */
final class GameByScoreComparator implements Comparator<Game> {
    @Override
    public int compare(Game lhsGame, Game rhsGame) {
        int scoreCompare = Integer.compare(
                lhsGame.gameStat().homeStat().score() + lhsGame.gameStat().awayStat().score(),
                rhsGame.gameStat().homeStat().score() + rhsGame.gameStat().awayStat().score());
        if (scoreCompare != 0) {
            return scoreCompare;
        }
        return Long.compare(lhsGame.gameInfo().startTime().toEpochMilli(), rhsGame.gameInfo().startTime().toEpochMilli());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GameByScoreComparator;
    }
}
