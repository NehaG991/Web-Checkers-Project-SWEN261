package com.webcheckers.application;

import com.webcheckers.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import spark.Session;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * The unit test suite for the {@link PlayerLobby} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Application-tier")
public class PlayerLobbyTest {

    private PlayerLobby CuT;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Map<String, Player> playerList;
    private Session sessionOne;
    private Session sessionTwo;
    private Player testPlayer1;
    private Player testPlayer2;

    /**
     * Setup new mock objects for each test. We need more than one player,
     * and therefore a session for each player.
     */
    @BeforeEach
    public void setup() {
        this.sessionOne = mock(Session.class);
        this.sessionTwo = mock(Session.class);
        this.playerList = new HashMap<>();
        this.testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        this.testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        CuT = new PlayerLobby(playerList);
    }

    /**
     * Test that players being signed-in are being placed in the list of active
     * players.
     */
    @Test
    public void sign_in() {
        // sign in Player One, check that the size of the list of active
        // players is now one.
        CuT.signIn(PLAYER_ONE_NAME, testPlayer1);
        assertEquals(1, playerList.size(),
                "The player list should have one player in it, but it does not.");

        // sign in Player Two, check that the size of the list of active
        // players is now two.
        CuT.signIn(PLAYER_TWO_NAME, testPlayer2);
        assertEquals(2, playerList.size(),
                "The player list should now have two players in it, but it does not.");
    }

    /**
     * Test that the active players' names are marked as in-use, therefore
     * unavailable for anyone else to use.
     */
    @Test
    public void name_in_use() {
        sign_in();
        // assert that Player One and Player Two's names are in-use.
        assertTrue(CuT.userNameInUse(PLAYER_ONE_NAME));
        assertTrue(CuT.userNameInUse(PLAYER_TWO_NAME));

        // assert that the name "Not in use" is in fact, not in use.
        assertFalse(CuT.userNameInUse("Not in use"));
    }

    /**
     * Test that in the case where no players are signed-in, checking if a name
     * is in use will return false, indicating that the name is not being used.
     */
    @Test
    public void name_in_use_no_active_players() {
        assertFalse(CuT.userNameInUse(PLAYER_ONE_NAME));
    }

    /**
     * Test that a Player object can be retrieved from the list of active
     * players, provided their username.
     */
    @Test
    public void get_by_name() {
        sign_in();
        // retrieve the Objects from the PlayerLobby associated with each
        // player's name.
        Player resultPlayerOne = CuT.getPlayerByName(PLAYER_ONE_NAME);
        Player resultPlayerTwo = CuT.getPlayerByName(PLAYER_TWO_NAME);

        // assert that the objects retrieved match the objects we placed into
        // the lobby.
        assertEquals(testPlayer1, resultPlayerOne);
        assertEquals(testPlayer2, resultPlayerTwo);
    }

    /**
     * Test that in the case where no players are signed-in, getting a player
     * object by name returns null.
     */
    @Test
    public void get_by_name_no_active_players() {
        assertNull(CuT.getPlayerByName(PLAYER_ONE_NAME));
    }

    /**
     * Test that players being signed-out are being removed from the list of active
     * @author Neha Ghanta, ng8975@rit.edu
     */
    @Test
    public void sign_out(){

        // signing in both test players so that playerLobby is greater than zero
        CuT.signIn(PLAYER_ONE_NAME, testPlayer1);
        CuT.signIn(PLAYER_TWO_NAME, testPlayer2);

        // signing out testPlayer2
        CuT.signOut(PLAYER_TWO_NAME, testPlayer2);
        assertEquals(1, playerList.size(),
                "The player list should have one player in it");

        // signing out testPlayer1
        CuT.signOut(PLAYER_ONE_NAME, testPlayer1);
        assertEquals(0, playerList.size(),
                "The player list should have zero players in it");
    }
}
