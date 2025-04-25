package org.sportradar.scoreboardlib;

import org.sportradar.scoreboardlib.exceptions.GameNotFoundException;
import org.sportradar.scoreboardlib.exceptions.StartGameException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * IScoreBoard thread safe implementation
 */
class ScoreBoard implements IScoreBoard {

    private final Comparator<Game> comparator;
    private final Map<String, GameInfo> gameInfos;
    private final Map<String, GameStat> gameStats;
    private final SortedSet<String> orderedGameIds;
    private final Set<Team> teamsInPlay;
    private final GameIdProvider gameIdProvider;
    private final ReentrantReadWriteLock lock;

    public ScoreBoard(Comparator<Game> comparator, GameIdProvider gameIdProvider) {
        this.comparator = comparator;
        this.gameIdProvider = gameIdProvider;
        gameInfos =  new HashMap<>();
        gameStats = new HashMap<>();
        teamsInPlay = ConcurrentHashMap.newKeySet();
        lock = new ReentrantReadWriteLock(true);
        orderedGameIds = Collections.synchronizedSortedSet(new TreeSet<>(getIDComparator()));
    }

    private synchronized void threadSafeAddTeams(Collection<Team> teams) throws StartGameException {
        teams.stream().filter(teamsInPlay::contains).findFirst().ifPresent(StartGameException::throwGameByTeamAlreadyStarted);
        teamsInPlay.addAll(teams);
    }

    private Comparator<String> getIDComparator() {
        return  (id1, id2) -> {
            Game game1 = new Game(gameInfos.get(id1), gameStats.get(id1));
            Game game2 = new Game(gameInfos.get(id2), gameStats.get(id2));
            return comparator.compare(game1, game2);
        };
    }

    @Override
    public String startGame(GameInfo gameInfo) throws StartGameException {
        if (gameInfo.homeTeam().equals(gameInfo.awayTeam())) {
            StartGameException.throwSameTeamArgumentsProvided(gameInfo.homeTeam());
        }

        threadSafeAddTeams(Arrays.asList(gameInfo.homeTeam(), gameInfo.awayTeam()));

        String id = gameIdProvider.getID();
        gameInfos.put(id, gameInfo);
        gameStats.put(id, new GameStat(new Stat((short) 0), new Stat((short) 0)));
        orderedGameIds.add(id);
        return id;
    }

    @Override
    public void finishGame(String id) {
        if (!orderedGameIds.contains(id)) {
            return;
        }
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            orderedGameIds.remove(id);
        } finally {
            writeLock.unlock();
        }

        var gameInfo = gameInfos.get(id);
        teamsInPlay.remove(gameInfo.homeTeam());
        teamsInPlay.remove(gameInfo.awayTeam());
        gameInfos.remove(id);
        gameStats.remove(id);
    }

    @Override
    public void updateStat(String id, GameStat gameStat) throws GameNotFoundException {
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (!orderedGameIds.contains(id)) {
                throw new GameNotFoundException(id);
            }
            gameStats.put(id, gameStat);

            orderedGameIds.remove(id);
            orderedGameIds.add(id);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Collection<Game> getGames() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            return orderedGameIds.stream().map(id -> new Game(gameInfos.get(id), gameStats.get(id))).toList();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        var games = this.getGames();
        var gamesIt = games.iterator();
        for (int i = 0; i < games.size(); ++i) {
            builder.append(String.format("%d. ", i)).append(gamesIt.next()).append("\n");
        }
        return builder.toString();
    }
}
