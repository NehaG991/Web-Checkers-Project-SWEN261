package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.Color;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashMap;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link PostResignGameRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class PostResignGameRouteTest {

    private PostResignGameRoute CuT;
    private GameLibrary dependant;

    private GameCenter gameCenter;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Gson gson;
    private Request request;
    private Session sessionOne;
    private Session sessionTwo;
    private Response response;
    private Player testPlayer1;
    private Player testPlayer2;

    /**
     * Setup two Players, mock sessions for those players, a GameCenter to
     * simulate a game with those players, and instantiate the test component.
     */
    @BeforeEach
    public void setup() {
        gson = new Gson();
        request = mock(Request.class);
        sessionOne = mock(Session.class);
        sessionTwo = mock(Session.class);
        response = mock(Response.class);

        // set up the Player objects needed to start a game.
        testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        // simulate both players being in a game with each other.
        testPlayer1.setInGame(true);
        testPlayer2.setInGame(true);

        CuT = new PostResignGameRoute(gson);
        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        dependant = new GameLibrary(activeGameList, endedGameList);
        gameCenter = dependant.createGame(testPlayer1, testPlayer2);
    }

    /**
     * Make sure the server responds correctly to the red player resigning.
     */
    @Test
    public void red_resigns() {
        when(request.session()).thenReturn(sessionOne);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        when(sessionOne.attribute(GetGameRoute.OPPONENT_NAME)).thenReturn(testPlayer2);
        // it should be red's turn first
        assertEquals(Color.RED, gameCenter.getCurrentTurn());

        Object resignMessageJson = CuT.handle(request, response);

        Message resignMessage = gson.fromJson((String) resignMessageJson, Message.class);
        assertEquals(PLAYER_ONE_NAME + " has resigned.", resignMessage.getText());
        assertSame(Message.Type.INFO, resignMessage.getType());

        // make sure the gameCenter says that it's white's turn now.
        assertEquals(Color.WHITE, gameCenter.getCurrentTurn());
    }

    /**
     * Make sure the server responds correctly to the white player resigning.
     */
    @Test
    public void white_resigns() {
        when(request.session()).thenReturn(sessionTwo);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));
        when(sessionTwo.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer2);
        when(sessionTwo.attribute(GetGameRoute.OPPONENT_NAME)).thenReturn(testPlayer1);
        // it should be red's turn first
        assertEquals(Color.RED, gameCenter.getCurrentTurn());

        Object resignMessageJson = CuT.handle(request, response);

        Message resignMessage = gson.fromJson((String) resignMessageJson, Message.class);
        assertEquals(PLAYER_TWO_NAME + " has resigned.", resignMessage.getText());
        assertSame(Message.Type.INFO, resignMessage.getType());

        // make sure the gameCenter keeps it on RED's turn.
        assertEquals(Color.RED, gameCenter.getCurrentTurn());
    }
}
