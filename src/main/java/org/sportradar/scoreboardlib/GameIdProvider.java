package org.sportradar.scoreboardlib;

import java.util.UUID;

class GameIdProvider {
    public String getID() {
        return UUID.randomUUID().toString();
    }
}