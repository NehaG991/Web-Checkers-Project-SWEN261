package com.webcheckers.application;

import com.webcheckers.model.*;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Session;

import static com.webcheckers.application.GameCenter.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * The unit test suite for the {@link GameCenter} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Application-tier")
public class GameCenterTest {

    private GameCenter CuT;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Player testPlayer1;
    private Player testPlayer2;
    private Session sessionOne;
    private Session sessionTwo;
    private SimpleMoveValidator testValidator;

    /**
     * Set up mock Sessions due to Player dependencies, and setup a new
     * GameCenter with two players.
     */
    @BeforeEach
    public void setup() {
        this.sessionOne = mock(Session.class);
        this.sessionTwo = mock(Session.class);
        this.testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        this.testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);
        CuT = new GameCenter(0, testPlayer1, testPlayer2);
        this.testValidator = new SimpleMoveValidator(CuT.getCheckersBoard());
    }


    /**
     * Make sure that the Red Player in the GameCenter is indeed testPlayer1.
     */
    @Test
    public void verify_red_player() {
        assertEquals(testPlayer1, CuT.getRedPlayer(),
                "The Red Player in the Game Center should be testPlayer1");
    }

    /**
     * Make sure that the White Player in the GameCenter is indeed testPlayer2.
     */
    @Test
    public void verify_white_player() {
        assertEquals(testPlayer2, CuT.getWhitePlayer(),
                "The White Player in the Game Center should be testPlayer2");
    }

    /**
     * Make sure the game board has been created.
     */
    @Test
    public void verify_game_board_exists() {
        assertNotNull(CuT.getCheckersBoard(), "The game board should not be null!");
    }

    /**
     * Make sure the current turn is initially RED.
     */
    @Test
    public void verify_initial_turn() {
        assertSame(Color.RED, CuT.getCurrentTurn(), "The initial turn should be RED");
    }

    /**
     * Make sure the current turn changes from RED to WHITE.
     */
    @Test
    public void verify_turn_change() {
        // simulate making a 'move'
        CuT.makeMove();
        // verify the turn has changed.
        assertSame(Color.WHITE, CuT.getCurrentTurn(), "The turn should now be WHITE, since RED made a move.");
        // make another 'move'
        CuT.makeMove();
        // verify the turn has changed.
        assertSame(Color.RED, CuT.getCurrentTurn(), "The turn should now be RED, since WHITE made a move.");
    }

    /**
     * Test the {@link GameCenter#capturedAllPieces()} ()} method.
     */
    @Test
    public void verify_game_over_condition() {
        assertFalse(CuT.capturedAllPieces(), "Game should not be over yet.");
        // simulate a player capturing enough pieces to end the game.
        for (int i = 0; i < 12; i++) {
            CuT.getRedPlayer().capture();
        }
        assertTrue(CuT.capturedAllPieces());
    }

    /**
     * Test the {@link GameCenter#blockedPiecesRed()} ()} method.
     */
    @Test
    public void verify_red_blocked_pieces(){
        CuT.getSimpleMoveValidator().clearMoveLists();
        assertTrue(CuT.blockedPiecesRed());
    }

    /**
     * Test the {@link GameCenter#blockedPiecesRed()} ()} method.
     */
    @Test
    public void verify_white_blocked_pieces(){
        CuT.getSimpleMoveValidator().clearMoveLists();
        assertTrue(CuT.blockedPiecesWhite());
    }

    /**
     * Make sure a simple diagonal move using a single piece in the forward
     * direction is a valid move.
     */
    @Test
    public void verify_valid_simple_move() {
        // setup a valid simple move
        Move simpleMove = new Move(new Position(5, 4), new Position(4, 5));
        // invoke the test
        Message moveStatus = CuT.validateMove(testPlayer1, simpleMove);
        assertEquals(VALID_MOVE, moveStatus.getText());
        assertSame(Message.Type.INFO, moveStatus.getType());
    }

    /**
     * Make sure a simple diagonal move using a single piece in the backward
     * direction is an invalid move.
     */
    @Test
    public void verify_invalid_simple_move() {
        // setup an invalid simple move
        Move simpleMove = new Move(new Position(3, 2), new Position(2, 1));

        // move the white piece from it's default location of (2, 1) to a new location (3, 2)
        // then try to move backwards with a single piece.
        BoardView checkersBoard = CuT.getCheckersBoard();
        Piece whitePiece = checkersBoard.getPiece(2, 1);
        checkersBoard.getRow(3).getSpace(2).setPiece(whitePiece);
        checkersBoard.getRow(2).getSpace(1).setPiece(null);

        // invoke the test
        Message moveStatus = CuT.validateMove(testPlayer2, simpleMove);
        assertEquals(INVALID_SIMPLE, moveStatus.getText());
        assertSame(Message.Type.ERROR, moveStatus.getType());
    }

    /**
     * Make sure a jump move over an opponent's piece in the forward direction
     * is a valid move.
     */
    @Test
    public void verify_valid_jump_move() {
        // setup a valid jump move
        Move jumpMove = new Move(new Position(5, 4), new Position(3, 6));

        // place a white piece on the board to jump over.
        Piece whitePiece = new Piece(Type.SINGLE, Color.WHITE);
        BoardView checkersBoard = CuT.getCheckersBoard();
        checkersBoard.getRow(4).getSpace(5).setPiece(whitePiece);

        // invoke the test
        Message moveStatus = CuT.validateMove(testPlayer1, jumpMove);
        assertEquals(VALID_MOVE, moveStatus.getText());
        assertSame(Message.Type.INFO, moveStatus.getType());
    }

    /**
     * Make sure a jump over your own piece in the forward direction is an
     * invalid move.
     */
    @Test
    public void verify_invalid_jump_move() {
        //setup an invalid jump move (jump over own piece)
        Move jumpMove = new Move(new Position(5, 1), new Position(3, 3));

        // place a white piece on the board to try to jump over.
        Piece whitePiece = new Piece(Type.SINGLE, Color.WHITE);
        BoardView checkersBoard = CuT.getCheckersBoard();
        checkersBoard.getRow(3).getSpace(2).setPiece(whitePiece);

        // invoke the test
        Message moveStatus = CuT.validateMove(testPlayer2, jumpMove);
        assertEquals(INVALID_JUMP, moveStatus.getText());
        assertSame(Message.Type.ERROR, moveStatus.getType());
    }

    /**
     * Make sure a jump over more than one space is an invalid move.
     */
    @Test
    public void verify_invalid_big_jump_move() {
        // setup an invalid jump move (jump over more than one space)
        Move jumpMove = new Move(new Position(5, 4), new Position(2, 7));
        //invoke the test
        Message moveStatus = CuT.validateMove(testPlayer1, jumpMove);
        assertEquals(BIG_JUMP, moveStatus.getText());
        assertSame(Message.Type.ERROR, moveStatus.getType());
    }
}
