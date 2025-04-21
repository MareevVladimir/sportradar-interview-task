package org.sportradar.scoreboardlib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sportradar.scoreboardlib.exceptions.StartGameException;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardTest {

    Comparator<Game> mockComparator = new GameByScoreComparator();
    @Mock GameIdProvider mockGameIdProvider;
    @Mock GameInfo mockGameInfo;
    Team mockTeamHome;
    Team mockTeamAway;

    private List<String> fillWithMockGames(ScoreBoard scoreBoard, int gameCount) {
        var ids = new ArrayList<String>();
        for (int i = 0; i < gameCount; ++i) {
            ids.add(scoreBoard.startGame(mockGameInfo("teamHome" + i, "teamAway" + i)));
        }
        return ids;
    }

    private Team mockTeam(String name) {
        var team = Mockito.mock(Team.class);
        Mockito.when(team.name()).thenReturn(name);
        return team;
    }

    private GameInfo mockGameInfo(Team homeTeam, Team awayTeam) {
        var gameInfo = Mockito.mock(GameInfo.class);
        Mockito.when(gameInfo.homeTeam()).thenReturn(homeTeam);
        Mockito.when(gameInfo.awayTeam()).thenReturn(awayTeam);
        Mockito.when(gameInfo.startTime()).thenReturn(Instant.now());
        return gameInfo;
    }

    private GameInfo mockGameInfo(String homeTeamName, String awayTeamName) {
        return mockGameInfo(mockTeam(homeTeamName), mockTeam(awayTeamName));
    }

    private GameStat mockGameStat(int scoreHome, int scoreAway) {
        var gameStat = Mockito.mock(GameStat.class);
        var statHome = Mockito.mock(Stat.class);
        var statAway = Mockito.mock(Stat.class);
        Mockito.when(statHome.score()).thenReturn((short) scoreHome);
        Mockito.when(statAway.score()).thenReturn((short) scoreAway);
        Mockito.when(gameStat.homeStat()).thenReturn(statHome);
        Mockito.when(gameStat.awayStat()).thenReturn(statAway);
        return gameStat;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockGameIdProvider = new GameIdProvider();
        mockTeamHome = mockTeam("Home");
        mockTeamAway = mockTeam("Away");
        mockGameInfo = mockGameInfo(mockTeamHome, mockTeamAway);
    }

    @Test
    public void scoreBoardInitShouldReturnEmptyGamesList() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        assertTrue(scoreBoard.getGames().isEmpty());
    }

    @Test
    public void scoreBoardStartGameWithTheSameTeamsShouldThrowStartGameException() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        assertThrows(StartGameException.class, () -> scoreBoard.startGame(mockGameInfo(mockTeamHome, mockTeamHome)));
    }

    @Test
    public void afterStartGameGetGamesShouldContainGameWithProvidedTeams() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        scoreBoard.startGame(mockGameInfo);
        assertTrue(() -> scoreBoard.getGames().stream().anyMatch(game ->
                game.gameInfo().homeTeam().equals(mockTeamHome) && game.gameInfo().awayTeam().equals(mockTeamAway)));
    }

    @Test
    public void startGameShouldReturnIdFromProvider() {
        mockGameIdProvider = Mockito.mock(GameIdProvider.class);
        Mockito.when(mockGameIdProvider.getID()).thenReturn("testValue");
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        assertEquals("testValue", scoreBoard.startGame(mockGameInfo));
    }

    @Test
    public void startGameShouldCreateZeroZeroScore() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        scoreBoard.startGame(mockGameInfo);
        var findGame = scoreBoard.getGames().stream().filter(game ->
                game.gameInfo().homeTeam().equals(mockTeamHome) && game.gameInfo().awayTeam().equals(mockTeamAway)).findAny();
        assertTrue(findGame.isPresent());
        var game = findGame.get();
        assertEquals(0, game.gameStat().homeStat().score());
        assertEquals(0, game.gameStat().awayStat().score());
    }

    @Test
    public void updateStatByIdShouldUpdateCorrectGame() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        String gameToUpdateID = scoreBoard.startGame(mockGameInfo);
        fillWithMockGames(scoreBoard, new Random().nextInt(3, 10));

        var updatedStat = mockGameStat(2, 1);
        scoreBoard.updateStat(gameToUpdateID, updatedStat);

        Optional<Game> findGame = scoreBoard.getGames().stream().filter(game -> game.gameInfo().equals(mockGameInfo)).findAny();
        assertTrue(findGame.isPresent());
        Game updatedGame = findGame.get();
        assertEquals(updatedStat, updatedGame.gameStat());
    }

    @Test
    public void updateStatByIdDoNotUpdateOtherGames() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        String gameToUpdateID = scoreBoard.startGame(mockGameInfo);
        var ids = fillWithMockGames(scoreBoard, new Random().nextInt(3, 10));

        GameStat updatedStat = mockGameStat(2, 1);
        scoreBoard.updateStat(gameToUpdateID, updatedStat);

        scoreBoard.getGames().forEach(game -> {
            if (!game.gameInfo().homeTeam().name().equals(mockGameInfo.homeTeam().name())) {
                assertNotSame(game.gameStat(), updatedStat);
            }
        });
    }

    @Test
    public void startGameWithTheTeamAlreadyPlayingShouldThrowStartGameException() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);

        Team arsenal = mockTeam("Arsenal");
        Team manutd = mockTeam("Manchester United");
        Team mancity = mockTeam("Manchester City");

        assertDoesNotThrow(() -> scoreBoard.startGame(mockGameInfo(arsenal, manutd)));
        assertThrows(StartGameException.class, () -> scoreBoard.startGame(mockGameInfo(mancity, arsenal)));
    }

    @Test
    public void startGameWithTheTeamEndedToPlayShouldStartSuccessfully() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);

        var arsenal = mockTeam("Arsenal");
        var manutd = mockTeam("Manchester United");
        var mancity = mockTeam("Manchester City");

        String id = scoreBoard.startGame(mockGameInfo(arsenal, manutd));
        scoreBoard.finishGame(id);
        assertDoesNotThrow(() -> scoreBoard.startGame(mockGameInfo(mancity, arsenal)));
    }

    @Test
    public void finishGameShouldRemoveGameFromTheCollection() {
        var scoreBoard = new ScoreBoard(mockComparator, mockGameIdProvider);
        String id = scoreBoard.startGame(mockGameInfo);
        scoreBoard.finishGame(id);
        assertFalse(() -> scoreBoard.getGames().stream().anyMatch(game ->
                game.gameInfo().equals(mockGameInfo)));
    }
}