package com.webcheckers.application;

import com.webcheckers.model.Color;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Session;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * The unit test suite for the {@link GameLibrary} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Application-tier")
public class GameLibraryTest {

    private GameLibrary CuT;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Player testPlayer1;
    private Player testPlayer2;
    private Session sessionOne;
    private Session sessionTwo;

    private HashMap<Integer, GameCenter> activeGameMap;
    private HashMap<Integer, GameCenter> endedGameMap;

    /**
     * Before each test, setup two players and sessions for those players,
     * as well as two maps to hold both active games and games that have ended.
     */
    @BeforeEach
    public void setup() {
        this.sessionOne = mock(Session.class);
        this.sessionTwo = mock(Session.class);
        this.testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        this.testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        activeGameMap = new HashMap<>();
        endedGameMap = new HashMap<>();
        CuT = new GameLibrary(activeGameMap, endedGameMap);
    }

    /**
     * Make sure that GameCenters are setup correctly. They should be placed
     * into the active game list, and players should be assigned colors
     * for their pieces.
     */
    @Test
    public void verify_game_setup() {
        // there should initially be no games in the active game list
        assertEquals(0, CuT.getActiveGameList().size());

        CuT.createGame(testPlayer1, testPlayer2);

        // make sure the player's colors are set properly.
        assertSame(Color.RED, testPlayer1.getColor());
        assertSame(Color.WHITE, testPlayer2.getColor());

        // make sure there is one game in the active game list.
        assertEquals(1, CuT.getActiveGameList().size());
    }

    /**
     * Test the {@link GameLibrary#createGame(Player, Player)} method returns
     * the expected {@link GameCenter}.
     */
    @Test
    public void verify_game_accessor() {
        GameCenter gameCenter = CuT.createGame(testPlayer1, testPlayer2);
        assertEquals(gameCenter, CuT.getGameByID(gameCenter.getGameID()));
    }

    /**
     * Make sure that when a game ends, it's placed in the correct list.
     */
    @Test
    public void verify_game_moved_to_correct_list() {
        GameCenter gameCenter = CuT.createGame(testPlayer1, testPlayer2);

        // there should be one game in the active list, and
        // none in the ended list.
        assertEquals(1, CuT.getActiveGameList().size());
        assertEquals(0, CuT.getEndedGameList().size());

        // simulate game ending
        CuT.gameHasEnded(gameCenter.getGameID());

        assertEquals(0, CuT.getActiveGameList().size());
        assertEquals(1, CuT.getEndedGameList().size());
    }

    /**
     * Make sure that the {@link GameLibrary#getActiveGameList()} and
     * {@link GameLibrary#getEndedGameList()} methods return the expected lists
     */
    @Test
    public void verify_list_accessors() {
        assertEquals(0, CuT.getActiveGameList().size());
        assertEquals(0, CuT.getEndedGameList().size());

        // create two games, and make sure the accessors produce the expected results.
        GameCenter game1 = CuT.createGame(testPlayer1, testPlayer2);
        assertEquals(1, CuT.getActiveGameList().size());

        GameCenter game2 = CuT.createGame(testPlayer1, testPlayer2);
        assertEquals(2, CuT.getActiveGameList().size());

        // ended game list should still be empty.
        assertEquals(0, CuT.getEndedGameList().size());

        // simulate a game ending, and check the list sizes.
        CuT.gameHasEnded(game2.getGameID());
        assertEquals(1, CuT.getActiveGameList().size());
        assertEquals(1, CuT.getEndedGameList().size());
    }
}
