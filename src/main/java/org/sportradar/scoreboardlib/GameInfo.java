package org.sportradar.scoreboardlib;

import java.time.Instant;
import java.util.Objects;

public record GameInfo(Instant startTime, Team homeTeam, Team awayTeam) {
    public GameInfo {
        Objects.requireNonNull(startTime);
        Objects.requireNonNull(homeTeam);
        Objects.requireNonNull(awayTeam);
    }
}
