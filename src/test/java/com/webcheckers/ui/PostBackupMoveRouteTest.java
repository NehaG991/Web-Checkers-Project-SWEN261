package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.Move;
import com.webcheckers.model.Player;
import com.webcheckers.model.Position;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.ArrayList;
import java.util.HashMap;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static com.webcheckers.ui.PostBackupMoveRoute.MOVE_CANCELLED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link PostBackupMoveRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class PostBackupMoveRouteTest {

    private PostBackupMoveRoute CuT;
    private GameLibrary dependant;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Gson gson;
    private Request request;
    private Response response;
    private Session sessionOne;
    private Session sessionTwo;

    private GameCenter gameCenter;
    private Player testPlayer1;
    private Player testPlayer2;

    private Move currentMove;

    /**
     * Setup a session, a move, attach that move to the session, and the
     * component to test before each test.
     */
    @BeforeEach
    public void setup() {
        this.gson = new Gson();
        request = mock(Request.class);
        response = mock(Response.class);

        this.sessionOne = mock(Session.class);
        this.sessionTwo = mock(Session.class);
        when(request.session()).thenReturn(sessionOne);

        testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        this.currentMove = new Move(new Position(5, 4), new Position(4, 5));

        CuT = new PostBackupMoveRoute(gson);

        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        dependant = new GameLibrary(activeGameList, endedGameList);
        gameCenter = dependant.createGame(testPlayer1, testPlayer2);
    }

    /**
     * Check that the move gets removed from the HTTP session, when a move is
     * attached to the session.
     */
    @Test
    public void verify_move_removed_from_session() {
        ArrayList<Move> moveList = new ArrayList<>(1);
        moveList.add(currentMove);

        // simulate gameCenter has already accepted a move, and it's stored in the linked list.
        gameCenter.updateModel(testPlayer1, currentMove, false);
        // setup the session attributes, with currentMove being a special case.
        // currentMove needs the object returned the first time it's called,
        // then null the second time.
        when(sessionOne.attribute(PostValidateMoveRoute.CURRENT_MOVE_ATTR)).thenReturn(moveList).thenReturn(null);
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));
        Object removalStatusJson = CuT.handle(request, response);

        // make sure the returned message contains the expected content.
        Message removalStatus = gson.fromJson((String) removalStatusJson, Message.class);
        assertEquals(MOVE_CANCELLED, removalStatus.getText());
        assertSame(Message.Type.INFO, removalStatus.getType());

        // make sure the move was removed from the session
        assertNull(sessionOne.attribute(PostValidateMoveRoute.CURRENT_MOVE_ATTR));
    }
}
