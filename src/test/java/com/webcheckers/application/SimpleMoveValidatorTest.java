package com.webcheckers.application;

import com.webcheckers.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit test suite for the {@link SimpleMoveValidator} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Application-tier")
public class SimpleMoveValidatorTest {

    private SimpleMoveValidator CuT;

    private BoardView checkersBoard;
    private Move validMove;
    private Move validKingMove;
    private Move invalidMove;

    /**
     * Before each test, setup a new checkers board model, create a good move,
     * as well as a bad one, instantiate the Validator, and find any available
     * moves.
     */
    @BeforeEach
    public void setup() {
        this.checkersBoard = new BoardView();
        validMove = new Move(new Position(5, 4), new Position(4, 5));
        validKingMove = new Move(new Position(4, 5), new Position(5, 4));
        invalidMove = new Move(new Position(7, 4), new Position(6, 5));

        CuT = new SimpleMoveValidator(checkersBoard);
        CuT.findValidMoves();
    }

    /**
     * Make sure that the valid move is stored in the redMoveList, and that
     * the isMoveValid method returns true for a valid move, and that after
     * the move is validated, the moveList is cleared.
     */
    @Test
    public void verify_valid_move() {
        assertTrue(CuT.redMoveList.containsKey(validMove.toString()));
        assertTrue(CuT.isMoveValid(validMove));
    }

    /**
     * Make sure that the invalid move is not stored in the redMoveList, and
     * that the isMoveValid method returns false for an invalid move, and that
     * after the move is done with validation, the moveList remains uncleared.
     */
    @Test
    public void verify_invalid_move() {
        assertFalse(CuT.redMoveList.containsKey(invalidMove.toString()));
        assertFalse(CuT.isMoveValid(invalidMove));
        // if there is an invalid move, the moveList should NOT be cleared.
        assertNotEquals(0, CuT.redMoveList.size());
    }

    /**
     * Make sure that a King piece can move backwards, and that the moveList
     * contains this move, that the isMoveValid returns true, and that the
     * moveList is cleared after the move is made.
     */
    @Test
    public void verify_king_move() {
        // setup the board to have a king in Space (4, 5), and null the piece in (5, 4)
        checkersBoard.getRow(4).getSpace(5).setPiece(new Piece(Type.KING, Color.RED));
        checkersBoard.getRow(5).getSpace(4).setPiece(null);
        CuT.findValidMoves();

        String moveString = String.format(validKingMove.toString());
        assertTrue(CuT.redMoveList.containsKey(moveString));
        assertTrue(CuT.isMoveValid(validKingMove));
    }
}
