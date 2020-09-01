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

import java.util.ArrayList;
import java.util.HashMap;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link PostSubmitTurn} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class PostSubmitTurnTest {

    private PostSubmitTurn CuT;
    private PostValidateMoveRoute dependant;
    private GameLibrary dependant2;

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

    private Move redMove;
    private Move whiteMove;

    /**
     * Setup two Players, Sessions for those players, a Move for each player,
     * and a GameCenter for the players before each test.
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

        this.redMove = new Move(new Position(5, 4), new Position(4, 5));
        this.whiteMove = new Move(new Position(2, 1), new Position(3, 2));

        CuT = new PostSubmitTurn(gson);
        dependant = new PostValidateMoveRoute(gson);
        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        dependant2 = new GameLibrary(activeGameList, endedGameList);
        gameCenter = dependant2.createGame(testPlayer1, testPlayer2);
    }

    /**
     * Make sure that a move submission from a RED player acts appropriately.
     * This means that the current turn should be changed from RED to WHITE,
     * a message is returned confirming the submission, and the model is
     * updated correctly.
     */
    @Test
    public void verify_red_submission() {
        ArrayList<Move> moveList = new ArrayList<>(1);
        moveList.add(redMove);
        when(request.session()).thenReturn(sessionOne);
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        when(sessionOne.attribute(PostValidateMoveRoute.CURRENT_MOVE_ATTR)).thenReturn(moveList);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        // check that current turn is RED
        assertEquals(Color.RED, gameCenter.getCurrentTurn());

        // get Piece from start position
        Piece pieceToMove = gameCenter.getCheckersBoard().getRow(5).getSpace(4).getPiece();

        // invoke the test
        Object turnSubmissionJson = CuT.handle(request, response);
        Message turnSubmission = gson.fromJson((String) turnSubmissionJson, Message.class);
        assertEquals("Turn Submitted.", turnSubmission.getText());
        assertEquals(Message.Type.INFO, turnSubmission.getType());

        // make sure the turn is changed to WHITE
        assertEquals(Color.WHITE, gameCenter.getCurrentTurn());

        // make sure model has been updated.
        // start space should no longer have a piece on it (null)
        assertNull(gameCenter.getCheckersBoard().getRow(5).getSpace(4).getPiece());
        assertEquals(pieceToMove, gameCenter.getCheckersBoard().getRow(4).getSpace(5).getPiece());
        assertTrue(dependant.consecutiveMoves.size() == 0);
    }

    /**
     * Make sure that a move submission from a WHITE player acts appropriately.
     * This means that the current turn should be changed from WHITE to RED,
     * a message is returned confirming the submission, and the model is
     * updated correctly.
     */
    @Test
    public void verify_white_submission() {
        ArrayList<Move> moveList = new ArrayList<>(1);
        moveList.add(whiteMove);
        // simulate white turn
        gameCenter.makeMove();

        when(request.session()).thenReturn(sessionTwo);
        when(sessionTwo.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer2);
        when(sessionTwo.attribute(PostValidateMoveRoute.CURRENT_MOVE_ATTR)).thenReturn(moveList);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));


        // check that current turn is WHITE
        assertEquals(Color.WHITE, gameCenter.getCurrentTurn());

        // get Piece from start position
        Piece pieceToMove = gameCenter.getCheckersBoard().getRow(5).getSpace(1).getPiece();

        // invoke the test
        Object turnSubmissionJson = CuT.handle(request, response);
        Message turnSubmission = gson.fromJson((String) turnSubmissionJson, Message.class);
        assertEquals("Turn Submitted.", turnSubmission.getText());
        assertEquals(Message.Type.INFO, turnSubmission.getType());

        // make sure the turn is changed to RED
        assertEquals(Color.RED, gameCenter.getCurrentTurn());

        // make sure model has been updated.
        // start space should no longer have a piece on it (null)
        assertNull(gameCenter.getCheckersBoard().getRow(5).getSpace(1).getPiece());
        assertEquals(pieceToMove, gameCenter.getCheckersBoard().getRow(4).getSpace(2).getPiece());
        assertTrue(dependant.consecutiveMoves.size() == 0);
    }
}
