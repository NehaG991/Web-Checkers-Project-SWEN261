package com.webcheckers.application;

import com.webcheckers.model.*;

/**
 * The SingleJumpMoveValidator will find any available Single Jump Moves on the
 * current board, and keep all available Single Jump Moves in a list for quick
 * access.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class SingleJumpMoveValidator extends MoveValidator {

    /**
     * Instantiates the SingleJumpMoveValidator with new HashMaps to store
     * available moves into, and gives access to the current state of the model
     * of the game board.
     *
     * @param boardModel: the model of the game board.
     */
    public SingleJumpMoveValidator(BoardView boardModel) {
        super(boardModel);
    }

    /**
     * Applies the American rules for checkers to a Single-Jump Move.
     *
     * @param piece: The piece making the move.
     * @param row: The row of the start position of this move.
     * @param col: The column of the start position of this move.
     */
    @Override
    public void applyRules(Piece piece, int row, int col) {
        Color pieceColor = piece.getColor();
        if (pieceColor == Color.RED) {
            if (row <= 1) {
                if (piece.getType() == Type.KING) {
                    if (col > 1) {
                        checkRearLeftMove(pieceColor, row, col);
                    }
                    if (col < 6) {
                        checkRearRightMove(pieceColor, row, col);
                    }
                }
            } else if (row >= 6) {
                if (col > 1) {
                    checkFrontLeftMove(pieceColor, row, col);
                }
                if (col < 6) {
                    checkFrontRightMove(pieceColor, row, col);
                }
            } else {
                if (col > 1) {
                    checkFrontLeftMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkRearLeftMove(pieceColor, row, col);
                    }
                }
                if (col < 6) {
                    checkFrontRightMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkRearRightMove(pieceColor, row, col);
                    }
                }
            }
        } else {
            if (row >= 6) {
                if (piece.getType() == Type.KING) {
                    if (col > 1) {
                        checkFrontLeftMove(pieceColor, row, col);
                    }
                    if (col < 6) {
                        checkFrontRightMove(pieceColor, row, col);
                    }
                }
            } else if (row <= 1) {
                if (col > 1) {
                    checkRearLeftMove(pieceColor, row, col);
                }
                if (col < 6) {
                    checkRearRightMove(pieceColor, row, col);
                }
            } else {
                if (col > 1) {
                    checkRearLeftMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkFrontLeftMove(pieceColor, row, col);
                    }
                }
                if (col < 6) {
                    checkRearRightMove(pieceColor, row, col);
                    if (piece.getType() == Type.KING) {
                        checkFrontRightMove(pieceColor, row, col);
                    }
                }
            }
        }
    }

    /**
     * Check if a jump move forward and to the right is valid. If it is, place
     * the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    protected void checkFrontRightMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space forwardRight = getFrontRightSpace(row, col);
        if (forwardRight.hasPiece()) {
            if (forwardRight.getPiece().getColor() != color) {
                Space nextForwardRight = getFrontRightSpace(row - 1, col + 1);
                if (!nextForwardRight.hasPiece()) {
                    Move forwardRightJump = new Move(startPosition,
                            new Position(row - 2, col + 2));
                    if (color == Color.RED) {
                        if (!redMoveList.containsKey(forwardRightJump.toString())) {
                            redMoveList.put(forwardRightJump.toString(), forwardRightJump);
                        }
                    } else {
                        if (!whiteMoveList.containsKey(forwardRightJump.toString())) {
                            whiteMoveList.put(forwardRightJump.toString(), forwardRightJump);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if a jump move forward and to the left is valid. If it is, place
     * the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    protected void checkFrontLeftMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space forwardLeft = getFrontLeftSpace(row, col);
        if (forwardLeft.hasPiece()) {
            if (forwardLeft.getPiece().getColor() != color) {
                Space nextForwardLeft = getFrontLeftSpace(row - 1, col - 1);
                if (!nextForwardLeft.hasPiece()) {
                    Move forwardLeftJump = new Move(startPosition,
                            new Position(row - 2, col - 2));
                    if (color == Color.RED) {
                        if (!redMoveList.containsKey(forwardLeftJump.toString())) {
                            redMoveList.put(forwardLeftJump.toString(), forwardLeftJump);
                        }
                    } else {
                        if (!whiteMoveList.containsKey(forwardLeftJump.toString())) {
                            whiteMoveList.put(forwardLeftJump.toString(), forwardLeftJump);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if a jump move backward and to the right is valid. If it is, place
     * the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    protected void checkRearRightMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space backwardRight = getRearRightSpace(row, col);
        if (backwardRight.hasPiece()) {
            if (backwardRight.getPiece().getColor() != color) {
                Space nextBackwardRight = getRearRightSpace(row + 1, col + 1);
                if (!nextBackwardRight.hasPiece()) {
                    Move backwardRightJump = new Move(startPosition,
                            new Position(row + 2, col + 2));
                    if (color == Color.RED) {
                        if (!redMoveList.containsKey(backwardRightJump.toString())) {
                            redMoveList.put(backwardRightJump.toString(), backwardRightJump);
                        }
                    } else {
                        if (!whiteMoveList.containsKey(backwardRightJump.toString())) {
                            whiteMoveList.put(backwardRightJump.toString(), backwardRightJump);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if a jump move backward and to the left is valid. If it is, place
     * the move in the respective moveList.
     *
     * @param color: The color of the piece being moved.
     * @param row: The row of the starting position of this move.
     * @param col: The column of the starting position of this move.
     */
    @Override
    protected void checkRearLeftMove(Color color, int row, int col) {
        Position startPosition = new Position(row, col);
        Space backwardLeft = getRearLeftSpace(row, col);
        if (backwardLeft.hasPiece()) {
            if (backwardLeft.getPiece().getColor() != color) {
                Space nextBackwardLeft = getRearLeftSpace(row + 1, col - 1);
                if (!nextBackwardLeft.hasPiece()) {
                    Move backwardLeftJump = new Move(startPosition,
                            new Position(row + 2, col - 2));
                    if (color == Color.RED) {
                        if (!redMoveList.containsKey(backwardLeftJump.toString())) {
                            redMoveList.put(backwardLeftJump.toString(), backwardLeftJump);
                        }
                    } else {
                        if (!whiteMoveList.containsKey(backwardLeftJump.toString())) {
                            whiteMoveList.put(backwardLeftJump.toString(), backwardLeftJump);
                        }
                    }
                }
            }
        }
    }
}
