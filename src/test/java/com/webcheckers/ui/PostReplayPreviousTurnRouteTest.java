package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.MoveController;
import com.webcheckers.model.BoardView;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.LinkedList;

import static com.webcheckers.ui.GetReplayGameRoute.MOVE_CONTROLLER_ATTR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link PostReplayPreviousTurnRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class PostReplayPreviousTurnRouteTest {

    private PostReplayPreviousTurnRoute CuT;

    private Gson gson;
    private Request request;
    private Session session;
    private Response response;

    private MoveController moveController;

    private LinkedList<BoardView> moves;
    private BoardView originalBoardState;
    private BoardView newBoardState;

    /**
     * Before each test, setup mock Spark objects, the CuT, a MoveController,
     * and map what object to return when the session (or session attributes)
     * are requested.
     */
    @BeforeEach
    public void setup() {
        gson = new Gson();
        request = mock(Request.class);
        session = mock(Session.class);
        response = mock(Response.class);

        CuT = new PostReplayPreviousTurnRoute(gson);

        moves = new LinkedList<>();
        originalBoardState = new BoardView();
        newBoardState = new BoardView();
        moves.add(originalBoardState);
        moves.add(newBoardState);
        moveController = new MoveController(moves);

        when(request.session()).thenReturn(session);
        when(session.attribute(MOVE_CONTROLLER_ATTR)).thenReturn(moveController);
    }

    /**
     * Make sure the next turn is received from the moveController, and attached
     * to the session.
     */
    @Test
    public void verify_got_previous_turn() {
        // simulate the linked list being pointed to where a previous
        // move is available.
        moveController.getNext();

        Object previousMoveMessageJSON = CuT.handle(request, response);

        Message nextMoveMessage = gson.fromJson((String) previousMoveMessageJSON, Message.class);
        assertEquals("true", nextMoveMessage.getText());
        assertEquals(Message.Type.INFO, nextMoveMessage.getType());

        // make sure the moveController now has a next move, but not
        // a previous, since we are pointing to the beginning of the linked list.
        assertTrue(moveController.hasNext());
        assertFalse(moveController.hasPrevious());
    }
}
