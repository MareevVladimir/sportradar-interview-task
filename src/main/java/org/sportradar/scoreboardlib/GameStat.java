package org.sportradar.scoreboardlib;

import java.time.Instant;
import java.util.Objects;

public record GameStat (Stat homeStat, Stat awayStat) {
    public GameStat {
        Objects.requireNonNull(homeStat);
        Objects.requireNonNull(awayStat);
    }
}
