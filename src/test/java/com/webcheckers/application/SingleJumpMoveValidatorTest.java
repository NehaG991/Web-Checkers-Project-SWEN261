package com.webcheckers.application;

import com.webcheckers.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit test suite for the {@link SingleJumpMoveValidator} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Application-tier")
public class SingleJumpMoveValidatorTest {

    private SingleJumpMoveValidator CuT;

    private BoardView checkersBoard;
    private Move jumpMove;
    private Move backwardsJumpMove;

    /**
     * Setup a new checkers board, a forward jump move, and backwards jump move,
     * and instantiate the validator.
     */
    @BeforeEach
    public void setup() {
        this.checkersBoard = new BoardView();
        jumpMove = new Move(new Position(5, 4), new Position(3, 6));
        backwardsJumpMove = new Move(new Position(3, 6), new Position(5, 4));

        CuT = new SingleJumpMoveValidator(checkersBoard);
    }

    /**
     * Make sure that you can use a single piece to jump over an opponents
     * piece in the forward direction.
     */
    @Test
    public void verify_single_piece_jump() {
        String moveString = String.format(jumpMove.toString());
        Piece whitePiece = new Piece(Type.SINGLE, Color.WHITE);
        // put the white piece in the 'middle' space to be jumped over.
        checkersBoard.getRow(4).getSpace(5).setPiece(whitePiece);

        // check for available jumps
        CuT.findValidMoves();
        // we should have two available jumps.
        assertEquals(2, CuT.redMoveList.size());

        assertTrue(CuT.redMoveList.containsKey(moveString));
        assertTrue(CuT.isMoveValid(jumpMove));
    }

    /**
     * Make sure that you can't jump over an opponents piece in the backwards
     * direction using a single piece.
     */
    @Test
    public void verify_invalid_single_piece_jump() {
        String moveString = String.format(backwardsJumpMove.toString());
        Piece whitePiece = new Piece(Type.SINGLE, Color.WHITE);
        // put the white piece in the 'middle' space to be jumped over.
        checkersBoard.getRow(4).getSpace(5).setPiece(whitePiece);

        // move one red single piece to try to jump backwards over the white piece.
        Piece redPiece = checkersBoard.getPiece(5, 4);
        checkersBoard.getRow(3).getSpace(6).setPiece(redPiece);
        checkersBoard.getRow(5).getSpace(4).setPiece(null);

        CuT.findValidMoves();
        assertEquals(1, CuT.redMoveList.size());

        // the moveList should not contain this move we are attempting.
        assertFalse(CuT.redMoveList.containsKey(moveString));
        assertFalse(CuT.isMoveValid(backwardsJumpMove));
        // after the move is validated, the move list should still have one element.
        assertEquals(1, CuT.redMoveList.size());
    }

    /**
     * Make sure that you can't jump over your own pieces.
     */
    @Test
    public void verify_invalid_jump_over_own_piece() {
        String moveString = String.format(jumpMove.toString());
        Piece redPiece = new Piece(Type.SINGLE, Color.RED);
        // put the white piece in the 'middle' space to be jumped over.
        checkersBoard.getRow(4).getSpace(5).setPiece(redPiece);

        CuT.findValidMoves();
        assertEquals(0, CuT.redMoveList.size());

        assertFalse(CuT.redMoveList.containsKey(moveString));
        assertFalse(CuT.isMoveValid(jumpMove));
    }

    /**
     * Make sure that a King piece has the ability to jump over an opponents
     * piece in the backwards direction.
     */
    @Test
    public void verify_backwards_king_jump() {
        String moveString = String.format(backwardsJumpMove.toString());
        Piece whitePiece = new Piece(Type.SINGLE, Color.WHITE);
        // put the white piece in the 'middle' space to be jumped over.
        checkersBoard.getRow(4).getSpace(5).setPiece(whitePiece);

        // make a new King piece to jump backwards over the white piece.
        Piece redKing = new Piece(Type.KING, Color.RED);
        checkersBoard.getRow(3).getSpace(6).setPiece(redKing);
        checkersBoard.getRow(5).getSpace(4).setPiece(null);

        CuT.findValidMoves();
        assertEquals(2, CuT.redMoveList.size());

        assertTrue(CuT.redMoveList.containsKey(moveString));
        assertTrue(CuT.isMoveValid(backwardsJumpMove));
    }
}
