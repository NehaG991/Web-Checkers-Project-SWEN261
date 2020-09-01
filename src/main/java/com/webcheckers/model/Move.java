package com.webcheckers.model;

/**
 * A class to represent a 'move' in a checkers game, i.e. moving a checkers
 * piece from one space to another.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class Move {

    private Position start;
    private Position end;

    /**
     * Instantiates a new move, with a starting {@link Position} and an
     * ending position.
     *
     * @param start: The Position the move came from.
     * @param end: The Position the move is going to.
     */
    public Move(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Accessor for the starting Position.
     *
     * @return the starting Position of this Move.
     */
    public Position getStart() {
        return this.start;
    }

    /**
     * Accessor for the end Position.
     *
     * @return the end Position of this Move.
     */
    public Position getEnd() {
        return this.end;
    }

    /**
     * Determines if this Move is a jump move.
     *
     * @return true if this move is a jump.
     */
    public boolean isJump() {
        return Math.abs(start.getRow() - end.getRow()) == 2;
    }

    /**
     * Used to see if two Moves are the same as one another.
     *
     * @param obj: The other object, preferably a {@link Move}
     * @return true if both Moves start and end in the same {@link Position},
     * false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }
        Move otherMove = (Move) obj;
        return this.getStart().getRow() == otherMove.getStart().getRow() &&
                this.getStart().getCell() == otherMove.getStart().getCell() &&
                this.getEnd().getRow() == otherMove.getEnd().getRow() &&
                this.getEnd().getCell() == otherMove.getEnd().getCell();
    }

    /**
     * Get a String showing the start and end coordinates of this move.
     *
     * @return A String representation of this move, indicating the start and
     * end coordinates of the move.
     */
    @Override
    public String toString() {
        return "Start: (" + start.getRow() + ", " + start.getCell() +
                ") End: (" + end.getRow() + ", " + end.getCell() + ")";
    }
}
