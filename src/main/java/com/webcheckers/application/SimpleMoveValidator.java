package com.webcheckers.application;

import com.webcheckers.model.*;

/**
 * The SimpleMoveValidator will find any available Simple Moves on the current
 * board, and keep all available Simple Moves in a list for quick access.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class SimpleMoveValidator extends MoveValidator {

    /**
     * Instantiates the SimpleMoveValidator with new HashMaps to store
     * available moves into, and gives access to the current state of the model
     * of the game board.
     *
     * @param boardModel: the model of the game board.
     */
    public SimpleMoveValidator(BoardView boardModel) {
        super(boardModel);
    }

    /**
     * Applies the American rules for checkers to a Simple Move.
     *
     * @param piece: The piece making the move.
     * @param row: The row of the start position of this move.
     * @param col: The column of the start position of this move.
     */
    @Override
    public void applyRules(Piece piece, int row, int col) {
        Color pieceColor = piece.getColor();
        if (pieceColor == Color.RED) {
            // if starting in row 0, we need a King to make a move.
            if (row == 0) {
                if (col != 0) {
                    // only check left if not in the leftmost column (0)
                    checkRearLeftMove(pieceColor, row, col);
                }
                if (col != 7) {
                    // only check right if not in the rightmost column (7)
                    checkRearRightMove(pieceColor, row, col);
                }
            } else if (row == 7) {
                if (col != 0) {
                    checkFrontLeftMove(pieceColor, row, col);
                }
                if (col != 7) {
                    checkFrontRightMove(pieceColor, row, col);
                }
            } else {
                // we only get here when the piece is in a 'middle' row
                if (col != 0) {
                    checkFrontLeftMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkRearLeftMove(pieceColor, row, col);
                    }
                }
                if (col != 7) {
                    checkFrontRightMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkRearRightMove(pieceColor, row, col);
                    }
                }
            }
        } else {
            // if starting in row 7, we need a King to make a move.
            if (row == 7) {
                if (col != 0) {
                    checkFrontLeftMove(pieceColor, row, col);
                }
                if (col != 7) {
                    checkFrontRightMove(pieceColor, row, col);
                }
            } else if (row == 0) {
                if (col != 0) {
                    checkRearLeftMove(pieceColor, row, col);
                }
                if (col != 7) {
                    checkRearRightMove(pieceColor, row, col);
                }
            } else {
                // we only get here when the piece is in a 'middle' row
                if (col != 0) {
                    checkRearLeftMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkFrontLeftMove(pieceColor, row, col);
                    }
                }
                if (col != 7) {
                    checkRearRightMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkFrontRightMove(pieceColor, row, col);
                    }
                }
            }
        }
    }

    /**
     * Check if a move forward and to the right is valid. If it is, place the
     * move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    public void checkFrontRightMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space forwardRight = getFrontRightSpace(row, col);
        if (!forwardRight.hasPiece()) {
            Move forwardRightMove = new Move(startPosition,
                    new Position(row - 1, col + 1));
            if (color == Color.RED) {
                if (!redMoveList.containsKey(forwardRightMove.toString())) {
                    redMoveList.put(forwardRightMove.toString(), forwardRightMove);
                }
            } else {
                if (!whiteMoveList.containsKey(forwardRightMove.toString())) {
                    whiteMoveList.put(forwardRightMove.toString(), forwardRightMove);
                }
            }
        }
    }

    /**
     * Check if a move forward and to the left is valid. If it is, place the
     * move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    public void checkFrontLeftMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space forwardLeft = getFrontLeftSpace(row, col);
        if (!forwardLeft.hasPiece())  {
            Move forwardLeftMove = new Move(startPosition,
                    new Position(row - 1, col - 1));
            if (color == Color.RED) {
                if (!redMoveList.containsKey(forwardLeftMove.toString())) {
                    redMoveList.put(forwardLeftMove.toString(), forwardLeftMove);
                }
            } else {
                if (!whiteMoveList.containsKey(forwardLeftMove.toString())) {
                    whiteMoveList.put(forwardLeftMove.toString(), forwardLeftMove);
                }
            }
        }
    }

    /**
     * Check if a move backward and to the right is valid. If it is, place the
     * move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    public void checkRearRightMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space backwardRight = getRearRightSpace(row, col);
        if (!backwardRight.hasPiece()) {
            Move backwardRightMove = new Move(startPosition,
                    new Position(row + 1, col + 1));
            if (color == Color.RED) {
                if (!redMoveList.containsKey(backwardRightMove.toString())) {
                    redMoveList.put(backwardRightMove.toString(), backwardRightMove);
                }
            } else {
                if (!whiteMoveList.containsKey(backwardRightMove.toString())) {
                    whiteMoveList.put(backwardRightMove.toString(), backwardRightMove);
                }
            }
        }
    }

    /**
     * Check if a move backward and to the left is valid. If it is, place the
     * move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    public void checkRearLeftMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space backwardLeft = getRearLeftSpace(row, col);
        if (!backwardLeft.hasPiece()) {
            Move backwardLeftMove = new Move(startPosition,
                    new Position(row + 1, col - 1));
            if (color == Color.RED) {
                if (!redMoveList.containsKey(backwardLeftMove.toString())) {
                    redMoveList.put(backwardLeftMove.toString(), backwardLeftMove);
                }
            } else {
                if (!whiteMoveList.containsKey(backwardLeftMove.toString())) {
                    whiteMoveList.put(backwardLeftMove.toString(), backwardLeftMove);
                }
            }
        }
    }
}
