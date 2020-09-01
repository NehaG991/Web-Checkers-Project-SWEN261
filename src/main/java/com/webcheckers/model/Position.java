package com.webcheckers.model;

/**
 * A class to represent the position of a space on a checkers board.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class Position {

    private int row;
    private int cell;

    /**
     * Instantiates a new Position with a row and cell(column) on the checkers board.
     *
     * @param row: The row of the checkers board that this Position is in
     * @param cell: The column of the checkers board that this Position is in
     */
    public Position(int row, int cell) {
        this.row = row;
        this.cell = cell;
    }

    /**
     * Accessor for the row this Position is in.
     *
     * @return this Position's row.
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Accessor for the column this Position is in.
     *
     * @return this Position's column.
     */
    public int getCell() {
        return this.cell;
    }
}
