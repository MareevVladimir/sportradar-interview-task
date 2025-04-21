package org.sportradar.scoreboardlib;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public record Game (GameInfo gameInfo, GameStat gameStat ) {
    public Game {
        Objects.requireNonNull(gameInfo);
        Objects.requireNonNull(gameStat);
    }

    @Override
    public String toString() {
        return String.format("%s %d - %s %d",
                gameInfo.homeTeam().name(), gameStat.homeStat().score(),
                gameInfo.awayTeam().name(), gameStat.awayStat().score()
        );
    }
}

