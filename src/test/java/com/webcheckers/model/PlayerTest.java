package com.webcheckers.model;

import com.webcheckers.application.PlayerLobby;
import org.junit.jupiter.api.BeforeEach;
import com.webcheckers.model.Player;
import com.webcheckers.model.Color;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import spark.Session;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
/**
 * The unit test suite for the {@link PlayerLobby} component.
 *
 * @author Neha Ghanta, ng8975@rit.edu
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Model-tier")
class PlayerTest {

    private Player CuT;
    private static final String PLAYER_NAME = "NAME";
    private Session playerSession;
    private Color playerColor;
    private static final Boolean GAME_STATUS = false;


    /**
     * Setup new mock objects for each test.
     */
    @BeforeEach
    void setUp() {
        this.playerSession = mock(Session.class);
        CuT = new Player(PLAYER_NAME, playerSession);
        this.playerColor = Color.RED;
    }

    /**
     * Test that the main constructor works without failure.
     */
    @Test
    public void ctor(){
        new Player(PLAYER_NAME, playerSession);
    }

    /**
     * Test the {@link Player#getColor()} method.
     */
    @Test
    public void getColorTest(){
        assertEquals(Color.RED, CuT.getColor(), "It should return the color red " +
                "as that is the initial player color");
    }

    /**
     * Test the {@link Player#getName()} method.
     */
    @Test
    public void getNameTest(){
        assertEquals(PLAYER_NAME, CuT.getName(), "It should return the name: NAME");
    }

    /**
     * Test the {@link Player#getSession()}  method.
     */
    @Test
    public void getSessionTest(){
        assertEquals(playerSession, CuT.getSession(), "Should return player HTTP session");
    }

    /**
     * Test the {@link Player#setInGame(boolean)} method.
     * Test the {@link Player#getInGameStatus()} method.
     */
    @Test
    public void setInGameTest(){
        CuT.setInGame(GAME_STATUS);
        assertEquals(GAME_STATUS, CuT.getInGameStatus(), "Should return false as the InGameStatus");
    }

    /**
     * Test the {@link Player#equals(Object)}  method.
     * Object will be not be a player
     */
    @Test
    public void equalsNoPlayer(){
        Color notPlayerObjTest = Color.WHITE;
        assertEquals(false, CuT.equals(notPlayerObjTest), "Should return false as " +
                "the object is not a player");
    }

    /**
     * Test the {@link Player#equals(Object)}  method.
     * Object will be a player
     */
    @Test
    public void equalsPlayer(){
        Player sameName = new Player(PLAYER_NAME, playerSession);
        assertEquals(true, CuT.equals(sameName), "Should return true since the names" +
                "are the same");
    }

    /**
     * Test the {@link Player#capture()} method.
     */
    @Test
    public void verifyCaptures() {
        assertEquals(0, CuT.getCaptures());
        CuT.capture();
        assertEquals(1, CuT.getCaptures());
    }

}