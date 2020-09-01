package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.*;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashMap;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static com.webcheckers.ui.PostValidateMoveRoute.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link PostValidateMoveRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class PostValidateMoveRouteTest {

    private PostValidateMoveRoute CuT;
    private GameLibrary dependant;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Gson gson;
    private Request request;
    private Response response;

    private Session sessionOne;
    private Session sessionTwo;
    private Player testPlayer1;
    private Player testPlayer2;
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
        this.testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        this.testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        when(request.session()).thenReturn(sessionOne);
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);

        CuT = new PostValidateMoveRoute(gson);
        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        dependant = new GameLibrary(activeGameList, endedGameList);
        gameCenter = dependant.createGame(testPlayer1, testPlayer2);

        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));
    }

    /**
     * Make sure a valid move on a checkers board is seen as valid by the server,
     * and that the server responds accordingly.
     */
    @Test
    public void valid_move() {
        // setup a good move.
        Position start = new Position(5,4);
        Position end = new Position(4, 3);
        Move move = new Move(start, end);

        // setup the response to the query for the move data
        when(request.queryParams(ACTION_DATA_ATTR)).thenReturn(gson.toJson(move));

        // invoke the test
        Object checkValidJson = CuT.handle(request, response);

        // verify the response from the server.
        Message checkValidMessage = gson.fromJson((String) checkValidJson, Message.class);
        assertEquals(VALID_MOVE, checkValidMessage.getText());
        assertSame(Message.Type.INFO, checkValidMessage.getType());
    }

    /**
     * Make sure an invalid move (horizontal move in the same row) on a
     * checkers board is seen as invalid by the server, and that the server
     * responds accordingly.
     */
    @Test
    public void invalid_move_same_row() {
        // setup a bad move (move horizontally in the same row)
        Position start = new Position(5, 4);
        Position end = new Position(5, 6);
        Move move = new Move(start, end);

        // setup the response to the query for the move data
        when(request.queryParams(ACTION_DATA_ATTR)).thenReturn(gson.toJson(move));

        // invoke the test
        Object checkValidJson = CuT.handle(request, response);

        // verify the response from the server.
        Message checkValidMessage = gson.fromJson((String) checkValidJson, Message.class);
        assertEquals("You must move diagonally.", checkValidMessage.getText());
        assertSame(Message.Type.ERROR, checkValidMessage.getType());
    }

    /**
     * Make sure an invalid move (vertical move in the same column) on a
     * checkers board is seen as invalid by the server, and that the server
     * responds accordingly.
     */
    @Test
    public void invalid_move_same_column() {
        // setup a bad move (move vertically in the same column)
        Position start = new Position(7, 4);
        Position end = new Position(5, 4);
        Move move = new Move(start, end);

        // setup the response to the query for the move data
        when(request.queryParams(ACTION_DATA_ATTR)).thenReturn(gson.toJson(move));

        // invoke the test
        Object checkValidJson = CuT.handle(request, response);

        // verify the response from the server.
        Message checkValidMessage = gson.fromJson((String) checkValidJson, Message.class);
        assertEquals("You must move diagonally.", checkValidMessage.getText());
        assertSame(Message.Type.ERROR, checkValidMessage.getType());
    }

    /**
     * Make sure an invalid move (diagonal move backwards toward the player)
     * on a checkers board is seen as invalid by the server, and that the
     * server responds accordingly.
     */
    @Test
    public void backwards_move() {
        // setup a bad move (move diagonally back towards the player)
        Position start = new Position(5, 4);
        Position end = new Position(6, 3);
        Move move = new Move(start, end);

        // setup the response to the query for the move data
        when(request.queryParams(ACTION_DATA_ATTR)).thenReturn(gson.toJson(move));

        // invoke the test
        Object checkValidJson = CuT.handle(request, response);

        // verify the response from the server.
        Message checkValidMessage = gson.fromJson((String) checkValidJson, Message.class);
        assertEquals("This isn't a valid simple move.", checkValidMessage.getText());
        assertSame(Message.Type.ERROR, checkValidMessage.getType());
    }

    /**
     * Make sure an invalid move (jump over your own piece) on a checkers board
     * is seen as invalid be the server, and that the server responds accordingly.
     */
    @Test
    public void invalid_jump_over_own_piece() {
        Position start = new Position(6, 3);
        Position end = new Position(4, 1);
        Move move = new Move(start, end);

        when(request.queryParams(ACTION_DATA_ATTR)).thenReturn(gson.toJson(move));

        Object checkValidJson = CuT.handle(request, response);

        Message checkValidMessage = gson.fromJson((String) checkValidJson, Message.class);
        assertEquals("This isn't a valid jump move.", checkValidMessage.getText());
        assertSame(Message.Type.ERROR, checkValidMessage.getType());
    }

    /**
     * Make sure when a King piece moves backwards that it's a valid move
     */
    @Test
    public void backwards_move_for_king(){
        BoardView checkersBoard = gameCenter.getCheckersBoard();
        Piece piece = new Piece(Type.KING, Color.RED);
        Space space = checkersBoard.getRow(5).getSpace(4);
        space.setPiece(piece);
        checkersBoard.getRow(6).getSpace(3).setPiece(null);
        Position start = new Position(5, 4);
        Position end = new Position(6, 3);
        Move move = new Move(start, end);

        // setup the response to the query for the move data
        when(request.queryParams(ACTION_DATA_ATTR)).thenReturn(gson.toJson(move));

        // invoke the test
        Object checkValidJson = CuT.handle(request, response);

        // verify the response from the server.
        Message checkValidMessage = gson.fromJson((String) checkValidJson, Message.class);
        assertEquals(VALID_MOVE, checkValidMessage.getText());
        assertSame(Message.Type.INFO, checkValidMessage.getType());
    }

}
