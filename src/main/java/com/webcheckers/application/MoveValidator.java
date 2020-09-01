package com.webcheckers.application;

import com.webcheckers.model.*;

import java.util.HashMap;

/**
 * An interface to define the behavior of the generic MoveValidator. A
 * MoveValidator should be able to validate only one 'type' of checkers move.
 * A MoveValidator should create a list of all valid moves that could be made
 * by one player, and update that list after each turn.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public abstract class MoveValidator {

    protected HashMap<String, Move> redMoveList;
    protected HashMap<String, Move> whiteMoveList;
    protected BoardView boardModel;

    public MoveValidator(BoardView boardModel) {
        this.boardModel = boardModel;
        redMoveList = new HashMap<>();
        whiteMoveList = new HashMap<>();
    }

    /**
     * Scan the model to find valid moves that a {@link Player} can make, and
     * if not already stored in the list of valid moves, store them.
     * Iterates over the {@link Space}s on the model to find pieces on the
     * board. For every piece found, determine what Simple Moves can be made,
     * and add them to the respective Player's move list if the move is not
     * already in the list.
     */
    public void findValidMoves() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (boardModel.getRow(row).getSpace(col).hasPiece()) {
                    applyRules(boardModel.getPiece(row, col), row, col);
                }
            }
        }
    }

    /**
     * Check if a move waiting to be validated is in the list of available,
     * valid moves that a Player can make.
     *
     * @param move: The {@link Move} waiting to be validated.
     * @return a boolean, true if the current move is in the list of available
     * moves to be made.
     */
    public boolean isMoveValid(Move move) {
        if (redMoveList.containsKey(move.toString())) {
            return true;
        } else if (whiteMoveList.containsKey(move.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clears out the move lists for this validator. Typically used to clear
     * out old moves that aren't possible anymore.
     */
    public void clearMoveLists() {
        redMoveList.clear();
        whiteMoveList.clear();
    }

    /**
     * Clears out the red move lists for this validator.
     * Used for a helper method for test class
     */
    public void clearRedList(){
        redMoveList.clear();
    }

    /**
     * Clears out the white move lists for this validator.
     * Used for a helper method for test class
     */
    public void clearWhiteList(){
        whiteMoveList.clear();
    }

    /**
     * Checks if a move of this type is available to be made.
     *
     * @param color: The color of the Player whose list needs to be checked.
     * @return a boolean, true if the size of the list of available moves is
     * not 0, false if there aren't any moves in the list.
     */
    public boolean doesValidMoveExist(Color color) {
        if (color == Color.RED) {
            return redMoveList.size() != 0;
        } else {
            return whiteMoveList.size() != 0;
        }
    }

    /**
     * Apply the American rules for checkers to this type of move.
     *
     * @param piece: The piece making the move.
     * @param row: The row of the start position of this move.
     * @param col: The column of the start position of this move.
     */
    protected abstract void applyRules(Piece piece, int row, int col);

    /**
     * Check if a move of this type, forward and to the right, is valid.
     * If it is, place the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    protected abstract void checkFrontRightMove(Color color, int row, int col);

    /**
     * Check if a move of this type, forward and to the left, is valid.
     * If it is, place the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    protected abstract void checkFrontLeftMove(Color color, int row, int col);

    /**
     * Check if a move of this type, backward and to the right, is valid.
     * If it is, place the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    protected abstract void checkRearRightMove(Color color, int row, int col);

    /**
     * Check if a move of this type, backward and to the left, is valid.
     * If it is, place the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    protected abstract void checkRearLeftMove(Color color, int row, int col);

    /**
     * Get the space above and to the right of the current space from the model.
     *
     * @param row: This space's row
     * @param col: This space's column
     * @return the space above and to the right of this space.
     */
    protected Space getFrontRightSpace(int row, int col) {
        return boardModel.getRow(row - 1).getSpace(col + 1);
    }

    /**
     * Get the space above and to the left of the current space from the model.
     *
     * @param row: This space's row
     * @param col: This space's column
     * @return the space above and to the left of this space.
     */
    protected Space getFrontLeftSpace(int row, int col) {
        return boardModel.getRow(row - 1).getSpace(col - 1);
    }

    /**
     * Get the space below and to the right of the current space from the model.
     *
     * @param row: This space's row
     * @param col: This space's column
     * @return the space below and to the right of this space.
     */
    protected Space getRearRightSpace(int row, int col) {
        return boardModel.getRow(row + 1).getSpace(col + 1);
    }

    /**
     * Get the space below and to the left of the current space from the model.
     *
     * @param row: This space's row
     * @param col: This space's column
     * @return the space below and to the left of this space.
     */
    protected Space getRearLeftSpace(int row, int col) {
        return boardModel.getRow(row + 1).getSpace(col - 1);
    }
}
