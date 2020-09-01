package com.webcheckers.model;

/**
 * A class to represent one square on the Checkers board.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class Space {
    private int cellIdx;
    private Row row;
    private SpaceType spaceType;
    private Piece piece;

    /**
     * Instantiates a new Space on the Checkers board.
     * Odd index spaces in even rows will be dark in color, and even index
     * spaces in odd rows will be dark in color. Every other space is light
     * in color. This constructor also places pieces on the closest 12 squares
     * to each player, one player receiving white pieces, the other red.
     *
     * @param cellIdx: The index of this Space in it's respective {@link Row}.
     * @param row: The {@link Row} this Space is in.
     */
    public Space(int cellIdx, Row row) {
        this.cellIdx = cellIdx;
        this.row = row;
        if (cellIdx % 2 == 1 && row.getIndex() % 2 == 0) {
            // dark spaces are in odd columns in the even rows
            spaceType = SpaceType.DARK;
            if (row.getIndex() >= 5) {
                piece = new Piece(Type.SINGLE, Color.RED);
            } else if (row.getIndex() <= 2) {
                piece = new Piece(Type.SINGLE, Color.WHITE);
            }
        } else if (cellIdx % 2 == 0 && row.getIndex() % 2 == 1) {
            // dark spaces are in even columns in the odd rows
            spaceType = SpaceType.DARK;
            if (row.getIndex() >= 5) {
                piece = new Piece(Type.SINGLE, Color.RED);
            } else if (row.getIndex() <= 2) {
                piece = new Piece(Type.SINGLE, Color.WHITE);
            }
        } else {
            spaceType = SpaceType.LIGHT;
        }
    }

    /**
     * Copy constructor to help create a deep copy of the board model.
     *
     * @param otherBoard: The model to make a deep copy of.
     * @param row: The {@link Row} this space is in.
     * @param cellIdx: The column this space is in.
     */
    public Space(BoardView otherBoard, Row row, int cellIdx) {
        this.cellIdx = cellIdx;
        this.row = row;
        if (cellIdx % 2 == 1 && row.getIndex() % 2 == 0) {
            spaceType = SpaceType.DARK;
        } else if (cellIdx % 2 == 0 && row.getIndex() % 2 == 1) {
            spaceType = SpaceType.DARK;
        } else {
            spaceType = SpaceType.LIGHT;
        }
        if (otherBoard.getPiece(row.getIndex(), cellIdx) != null) {
            Piece otherPiece = otherBoard.getPiece(row.getIndex(), cellIdx);
            this.piece = new Piece(otherPiece.getType(), otherPiece.getColor());
        }
    }

    /**
     * Accessor for the column this Space is in.
     *
     * @return an integer representing this Space's column.
     */
    public int getCellIdx() {
        return cellIdx;
    }

    /**
     * Checks if this Space is a valid place to put a Piece, by making sure the
     * space is Dark, that the Space isn't beyond the bounds of the board, and
     * that the Space doesn't already have a Piece on it.
     *
     * @return a boolean, true if this Space is dark, is inside the bounds of
     * the board, and doesn't have a Piece on it already, false otherwise.
     */
    public boolean isValid() {
        return this.spaceType == SpaceType.DARK && !outOfBounds()
                && !hasPiece();
    }

    /**
     * Checks if this space is off the Game Board.
     * @return true if this space is off the Game board, false otherwise.
     */
    public boolean outOfBounds() {
        return this.cellIdx < 0 || this.cellIdx > 7 || this.row.getIndex() < 0
                || this.row.getIndex() > 7;
    }

    /**
     * Checks if this Space has a {@link Piece} on it.
     *
     * @return a boolean, true if this Space contains a Piece, false otherwise.
     */
    public boolean hasPiece() {
        return this.piece != null;
    }

    /**
     * Accessor for the {@link Piece} on this Space.
     *
     * @return null if this Space does not contain a {@link Piece}, or the
     * Piece object if this Space contains one.
     */
    public Piece getPiece() {
        if (hasPiece()) {
            return this.piece;
        } else {
            return null;
        }
    }

    /**
     * Set the Piece on this Space.
     *
     * @param piece: The object to be placed on this Space.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
