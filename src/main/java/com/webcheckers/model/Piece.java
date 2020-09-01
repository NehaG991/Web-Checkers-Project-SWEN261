package com.webcheckers.model;

/**
 * A class to represent a Checkers Piece.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 * @author Thomas Daley, ted5363@rit.edu
 */
public class Piece {
    private Type type;
    private Color color;

    /**
     * Instantiates a a new Piece.
     *
     * @param type:  The type of Piece this is meant to be.
     * @param color: The color of this Piece.
     */
    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    /**
     * This method is used to find out if this Piece is a single piece,
     * or a King.
     *
     * @return this Piece's type.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * This method is used to find out if this Piece is red in color,
     * or white.
     *
     * @return this Piece's color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * setKing changes the piece type to KING enum type.
     */
    public void setKing(){
        this.type = Type.KING;
    }

}
