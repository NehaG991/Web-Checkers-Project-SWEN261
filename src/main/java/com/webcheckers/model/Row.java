package com.webcheckers.model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A class to represent a row on a Checkers board.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 * @author Neha Ghanta, ng8975@rit.edu
 */
public class Row implements Iterable<Space> {

    private int index;
    private Space[] spaces;

    /**
     * Instantiates a new Row on the Checkers board, with 8 spaces in each row.
     *
     * @param index: The index of the current Row, with index 0 being at the
     *             top of the board.
     */
    public Row(int index) {
        this.index = index;
        this.spaces = new Space[8];
        for (int i = 0; i < 8; i++) {
            spaces[i] = new Space(i, this);
        }
    }

    /**
     * Copy constructor used to help create a deep copy of the board model.
     *
     * @param otherBoard: The model to make a deep copy of.
     * @param index: This row's index on the board.
     */
    public Row(BoardView otherBoard, int index) {
        this.index = index;
        this.spaces = new Space[8];
        for (int col = 0; col < 8; col++) {
            spaces[col] = new Space(otherBoard, this, col);
        }
    }

    /**
     * Get a {@link Space} by it's index.
     *
     * @param index: the index of the Space
     * @return the Space at index 'index' in this Row.
     */
    public Space getSpace(int index) {
        return this.spaces[index];
    }

    /**
     * Set a Space by it's index.
     *
     * @param index: the index of the Space to be set.
     * @param space: The new Space to be put in place of the old.
     */
    public void setSpace(int index, Space space) {
        this.spaces[index] = space;
    }

    /**
     * Accessor for the index of the current Row.
     *
     * @return the index of this Row.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Creates an iterator for the spaces object of the Checkers board, and
     * returns that iterator to the caller.
     *
     * @return an iterator for the {@link Space}s object of the Checkers board.
     */
    @Override
    public Iterator<Space> iterator() {
       return Arrays.asList(spaces).iterator();
    }
}
