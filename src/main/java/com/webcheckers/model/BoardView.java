package com.webcheckers.model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A class to represent a Checkers board.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 * @author Neha Ghanta, ng8975@rit.edu
 */
public class BoardView implements Iterable<Row> {
    private Row[] rows;

    /**
     * Instantiates a new Checkers board with 8 rows.
     */
    public BoardView() {
        rows = new Row[8];
        for (int i = 0; i < 8; i++) {
            rows[i] = new Row(i);
        }
    }

    /**
     * A copy constructor used to create a deep copy of the board model.
     *
     * @param otherBoard: The model to create a deep copy of.
     */
    public BoardView (BoardView otherBoard) {
        rows = new Row[8];
        for (int row = 0; row < 8; row++) {
            rows[row] = new Row(otherBoard, row);
        }
    }

    /**
     * Get a {@link Row} by it's index.
     *
     * @param index: the index of the Row
     * @return the Row at index 'index' on the board.
     */
    public Row getRow(int index) {
        return this.rows[index];
    }

    /**
     * Creates an iterator for the rows object of the Checkers board, and
     * returns that iterator to the caller.
     *
     * @return an iterator for the {@link Row}s object of the Checkers board.
     */
    @Override
    public Iterator<Row> iterator() {
        return Arrays.asList(rows).iterator();
    }

    /**
     * Checks the pieces after a valid move is made to determine if the piece
     * should be promoted to a King Type piece.
     *
     * @param checkersBoard: Model for the game board
     */
    public void promotePiece(BoardView checkersBoard) {
        Row farthestRow = checkersBoard.getRow(0);
        Row farthestFlippedRow = checkersBoard.getRow(7);

        for (int i = 0; i < 8; i++) {
            Space checkSpace = farthestRow.getSpace(i);
            Piece redPiece = checkSpace.getPiece();

            if (checkSpace.hasPiece() && redPiece.getColor() != Color.WHITE) {
                redPiece.setKing();
            }
        }

        for (int i = 0; i < 8; i++) {
            Space checkFlippedSpace = farthestFlippedRow.getSpace(i);
            Piece whitePiece = checkFlippedSpace.getPiece();

            if (checkFlippedSpace.hasPiece() && whitePiece.getColor() != Color.RED) {
                whitePiece.setKing();
            }
        }
    }

    /**
     * Returns the piece from the row and cell params
     *
     * @param row:  the row that the piece is located in
     * @param cell: the space that the piece is located on
     * @return Piece object in the row and cell
     */
    public Piece getPiece(int row, int cell) {
        Row searchRow = rows[row];
        Space space = searchRow.getSpace(cell);
        return space.getPiece();
    }

}
