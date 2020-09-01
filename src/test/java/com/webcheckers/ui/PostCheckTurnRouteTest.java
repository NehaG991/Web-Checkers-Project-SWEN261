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

import static com.webcheckers.ui.GetGameRoute.ACTIVE_COLOR;
import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link PostCheckTurnRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class PostCheckTurnRouteTest {

    private PostCheckTurnRoute CuT;
    private GameLibrary dependant;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";
    private static final String SPECTATOR_NAME = "Spectator";

    private Gson gson;
    private Request request;
    private Response response;

    private Session sessionOne;
    private Session sessionTwo;
    private Session spectatorSession;
    private Player testPlayer1;
    private Player testPlayer2;
    private Player spectator;
    private GameCenter gameCenter;

    /**
     * Setup two Players, Sessions for those players, and a GameCenter for the
     * players before each test.
     */
    @BeforeEach
    public void setup() {
        this.gson = new Gson();
        request = mock(Request.class);
        response = mock(Response.class);

        this.sessionOne = mock(Session.class);
        this.sessionTwo = mock(Session.class);
        this.spectatorSession = mock(Session.class);
        this.testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        this.testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);
        this.spectator = new Player(SPECTATOR_NAME, spectatorSession);

        CuT = new PostCheckTurnRoute(gson);

        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        dependant = new GameLibrary(activeGameList, endedGameList);
        gameCenter = dependant.createGame(testPlayer1, testPlayer2);
    }

    /**
     * Make sure that when testPlayer1 is the currentUser, and is set to the
     * Red player of a checkers game, that their turn is first.
     */
    @Test
    public void verify_my_turn() {
        // CurrentUser will be testPlayer1, who is stored as the Red player in the GameCenter.
        when(request.session()).thenReturn(sessionOne);
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        // invoke the test.
        Object checkTurnMessageJson = CuT.handle(request, response);

        // we now have a JSON version of the checkTurnMessage the handle method generated. It should be red's turn.
        Message checkTurnMessage = gson.fromJson((String) checkTurnMessageJson, Message.class);
        assertEquals("true", checkTurnMessage.getText());
        assertEquals(Message.Type.INFO, checkTurnMessage.getType());
    }

    /**
     * Make sure that when testPlayer2 is the currentUser, and is set to the
     * White player of a checkers game, that their turn is *not* first.
     */
    @Test
    public void verify_not_my_turn() {
        // CurrentUser will be testPlayer2, who is stored as the White player in the GameCenter.
        when(request.session()).thenReturn(sessionTwo);
        when(sessionTwo.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer2);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        // invoke the test.
        Object checkTurnMessageJson =  CuT.handle(request, response);

        // we now have a JSON version of the checkTurnMessage the handle method generated. It should be red's turn.
        Message checkTurnMessage = gson.fromJson((String) checkTurnMessageJson, Message.class);
        assertEquals("false", checkTurnMessage.getText());
        assertEquals(Message.Type.INFO, checkTurnMessage.getType());
    }

    /**
     * Make sure that the spectator's page gets refreshed, since it's session attribute for the active
     * color is set to WHITE, and the current turn is RED.
     */
    @Test
    public void verify_spectator_refresh() {
        when(request.session()).thenReturn(spectatorSession);
        when(spectatorSession.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(spectator);
        // we set the active color on the spectator session to white to force a refresh,
        // since the gameCenter will be on RED's turn by default.
        when(spectatorSession.attribute(ACTIVE_COLOR)).thenReturn(Color.WHITE);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        Object checkTurnMessageJson = CuT.handle(request, response);

        // we now have a JSON version of the checkTurnMessage the handle method generated. It should be red's turn.
        Message checkTurnMessage = gson.fromJson((String) checkTurnMessageJson, Message.class);
        assertEquals("true", checkTurnMessage.getText());
        assertEquals(Message.Type.INFO, checkTurnMessage.getType());
    }

    /**
     * Make sure that the spectator's page does not get refreshed, since it's session attribute for the active
     * color is set to RED, and the current turn is RED.
     */
    @Test
    public void verify_spectator_no_refresh() {
        when(request.session()).thenReturn(spectatorSession);
        when(spectatorSession.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(spectator);
        // we set the active color on the spectator session to RED so that we don't refresh,
        // since the gameCenter will be on RED's turn by default.
        when(spectatorSession.attribute(ACTIVE_COLOR)).thenReturn(Color.RED);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        Object checkTurnMessageJson = CuT.handle(request, response);

        // we now have a JSON version of the checkTurnMessage the handle method generated. It should be red's turn.
        Message checkTurnMessage = gson.fromJson((String) checkTurnMessageJson, Message.class);
        assertEquals("false", checkTurnMessage.getText());
        assertEquals(Message.Type.INFO, checkTurnMessage.getType());
    }
}
