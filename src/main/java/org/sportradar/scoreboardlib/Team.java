package org.sportradar.scoreboardlib;

import java.util.Objects;

public record Team (String name) {
    public Team {
        Objects.requireNonNull(name);
    }
}
