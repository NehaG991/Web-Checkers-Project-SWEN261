package com.webcheckers.application;

import com.webcheckers.model.*;
import com.webcheckers.util.Message;

import java.util.LinkedList;

/**
 * A class to hold data for a game of Checkers. Things like the game board,
 * and turn info will be kept here.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 * @author Neha Ghanta, ng8975@rit.edu
 */
public class GameCenter {

    private final int gameID;

    private Color currentTurn;
    private BoardView checkersBoard;
    private BoardView rotatedBoard;
    private String resignStatus;

    private Player redPlayer;
    private Player whitePlayer;

    private MoveValidator simpleMoveValidator;
    private MoveValidator singleJumpMoveValidator;

    /** Used to keep track of all moves made during this game. */
    private LinkedList<BoardView> movesMade;

    static final String VALID_MOVE = "Valid move.";
    static final String INVALID_JUMP = "This isn't a valid jump move.";
    static final String INVALID_SIMPLE = "This isn't a valid simple move.";
    static final String JUMP_AVAILABLE = "There is a jump move available, you must make that move.";
    static final String BIG_JUMP = "You can't jump that far.";
    static final String NON_DIAGONAL = "You must move diagonally.";

    /**
     * Set up a new game board, and set the active player to RED.
     */
    public GameCenter(int gameID, Player redPlayer, Player whitePlayer) {
        this.gameID = gameID;
        this.checkersBoard = new BoardView();
        this.rotatedBoard = new BoardView();
        this.currentTurn = Color.RED;
        this.resignStatus = null;
        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        this.simpleMoveValidator = new SimpleMoveValidator(checkersBoard);
        this.singleJumpMoveValidator = new SingleJumpMoveValidator(checkersBoard);
        simpleMoveValidator.findValidMoves();
        singleJumpMoveValidator.findValidMoves();
        movesMade = new LinkedList<>();
        // add a new BoardView to act as the original state of the board
        // in replay mode.
        movesMade.add(new BoardView());
    }

    /**
     * Accessor to the simple move validator
     * Helped method for test classes
     * @return the Simple Move Validator
     */
    public MoveValidator getSimpleMoveValidator(){
        return this.simpleMoveValidator;
    }


    /**
     * Find out who's turn it is.
     *
     * @return the Color corresponding to which {@link Player}'s turn it is.
     */
    public Color getCurrentTurn() {
        return this.currentTurn;
    }

    /**
     * Get the game board for this specific game.
     *
     * @return a {@link BoardView} for this checkers game.
     */
    public BoardView getCheckersBoard() {
        return this.checkersBoard;
    }

    /**
     * Get the game board for this specific game, rotated 180 degrees.
     *
     * @return a {@link BoardView} rotated 180 degrees for this checkers game.
     */
    public BoardView getRotatedBoard() {
        BoardView rotatedDeepCopy = rotatedBoard;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // start bottom right corner of checkersBoard, working backwards.
                // copy to top left corner of rotatedDeepCopy, working forwards.
                Space currentSpace = checkersBoard.getRow(7 - i).getSpace(7 - j);
                rotatedDeepCopy.getRow(i).setSpace(j, currentSpace);
            }
        }
        return rotatedDeepCopy;
    }

    /**
     * Accessor for the Red Player's {@link Player} object.
     *
     * @return the Red Player.
     */
    public Player getRedPlayer() {
        return this.redPlayer;
    }

    /**
     * Accessor for the White Player's {@link Player} object.
     *
     * @return the White Player.
     */
    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    /**
     * Accessor for this GameCenter's unique gameID.
     *
     * @return this game's unique gameID.
     */
    public int getGameID() {
        return this.gameID;
    }

    /**
     * Set the Resignation Status of the game.
     *
     * @param status: a String containing information about the resignation
     *              from the game.
     */
    public void setResignStatus(String status) {
        this.resignStatus = status;
        redPlayer.setInGame(false);
        whitePlayer.setInGame(false);
    }

    /**
     * Get the Resignation status of this game. **NEED TO CHECK FOR NULL WHEN
     * CALLING THIS METHOD**
     *
     * @return null if not set, a String containing the status of the
     * resignation if it is set.
     */
    public String getResignStatus() {
        return this.resignStatus;
    }

    /**
     * Accessor for the record of all moves made during this game.
     *
     * @return A LinkedList containing BoardView objects containing different
     * states of the game board to represent moves that were made.
     */
    public LinkedList<BoardView> getMovesMade() {
        return this.movesMade;
    }

    /**
     * Checks if the game end condition is met. This happens when one player
     * has captured all of another player's pieces.
     *
     * @return true if either player has captured all of the pieces belonging
     * to the other player, false otherwise.
     */
    public boolean capturedAllPieces() {
        if (redPlayer.getCaptures() == 12 || whitePlayer.getCaptures() == 12) {
            resetPlayerCaptures();
            return true;
        }
        return false;
    }

    /**
     * Checks if the game end condition is met. This happens when one player
     * has no more valid moves to make
     *
     * @return true if the red's moveList is equal to 0
     * false otherwise.
     */
    public boolean blockedPiecesRed(){
        if (!simpleMoveValidator.doesValidMoveExist(Color.RED) &&
                !singleJumpMoveValidator.doesValidMoveExist(Color.RED)){
            return true;
        }
        return false;
    }

    /**
     * Checks if the game end condition is met. This happens when one player
     * has no more valid moves to make
     *
     * @return true if the white's moveList is equal to 0
     * false otherwise.
     */
    public boolean blockedPiecesWhite(){
        if (!simpleMoveValidator.doesValidMoveExist(Color.WHITE) &&
                !singleJumpMoveValidator.doesValidMoveExist(Color.WHITE)){
            return true;
        }
        return false;
    }

    /**
     * Resets the capture counter for both players when a game-end condition is met.
     */
    public void resetPlayerCaptures() {
        redPlayer.resetCapturesAfterGameEnd();
        whitePlayer.resetCapturesAfterGameEnd();
    }

    /**
     * Update the currentTurn attribute to reflect who's turn it is.
     * This method works like a 'flip-flop' method, where each time it's called
     * it should switch who's turn it is.
     */
    public void makeMove() {
        if (currentTurn == Color.RED) {
            this.currentTurn = Color.WHITE;
        } else {
            this.currentTurn = Color.RED;
        }
    }

    /**
     * Clear the move lists for all {@link MoveValidator}s, and tell validators
     * to find any new valid moves on the board.
     */
    public void refreshMoveValidators() {
        simpleMoveValidator.clearMoveLists();
        singleJumpMoveValidator.clearMoveLists();
        simpleMoveValidator.findValidMoves();
        singleJumpMoveValidator.findValidMoves();
    }

    /**
     * Check if the current Player has a jump move available.
     *
     * @param player: The Player making their Move.
     * @param lastMove: The previous move made by the Player.
     * @return true if this Player has a jump move they can make,
     * false otherwise.
     */
    public boolean checkForJumpMove(Player player, Move lastMove) {
        boolean result = false;
        if (player.equals(redPlayer)) {
            String lastMoveEndSpace = lastMove.toString().substring(19);
            for (String key: singleJumpMoveValidator.redMoveList.keySet()) {
                if (key.contains(lastMoveEndSpace)) {
                    result = true;
                }
            }
        } else {
            Move adjustedLastMove = getAdjustedMove(lastMove);
            String lastMoveEndSpace = adjustedLastMove.toString().substring(19);
            for (String key: singleJumpMoveValidator.whiteMoveList.keySet()) {
                if (key.contains(lastMoveEndSpace)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Uses the appropriate {@link MoveValidator} to validate the move provided.
     *
     * @param player: The Player submitting this move.
     * @param move: The Move this Player submitted.
     * @return a {@link Message} containing information about whether the move
     * was valid or not. If the move is not valid, the message contains
     * information as to why it's not valid.
     */
    public Message validateMove(Player player, Move move) {
        Message moveStatus;
        refreshMoveValidators();
        if (player.equals(whitePlayer)) {
            move = getAdjustedMove(move);
        }
        // get the start and end coordinates of this move
        int startRow = move.getStart().getRow();
        int startCell = move.getStart().getCell();
        int endRow = move.getEnd().getRow();
        int endCell = move.getEnd().getCell();

        // check for non-diagonal move
        if (startRow == endRow || startCell == endCell) {
            moveStatus = Message.error(NON_DIAGONAL);
        }

        // check for validity of a jump move
        else if (Math.abs(startRow - endRow) == 2 || Math.abs(startCell - endCell) == 2) {
            if (singleJumpMoveValidator.isMoveValid(move)) {
                moveStatus = Message.info(VALID_MOVE);
                updateModel(player, move, true);
                refreshMoveValidators();
            } else {
                moveStatus = Message.error(INVALID_JUMP);
            }
        }

        // this is a jump-move over more than one space, and is false by default.
        else if (Math.abs(startRow - endRow) > 2 || Math.abs(startCell - endCell) > 2) {
            moveStatus = Message.error(BIG_JUMP);
        }

        // this is a simple move
        else {
            if (player.equals(whitePlayer)) {
                // check if a jump is available
                if (singleJumpMoveValidator.doesValidMoveExist(Color.WHITE)) {
                    moveStatus = Message.error(JUMP_AVAILABLE);
                } else {
                    moveStatus = simpleMoveValidator.isMoveValid(move) ?
                            Message.info(VALID_MOVE) : Message.error(INVALID_SIMPLE);
                }
            } else {
                // check if a jump is available
                if (singleJumpMoveValidator.doesValidMoveExist(Color.RED)) {
                    moveStatus = Message.error(JUMP_AVAILABLE);
                } else {
                    moveStatus = simpleMoveValidator.isMoveValid(move) ?
                            Message.info(VALID_MOVE) : Message.error(INVALID_SIMPLE);
                }
            }
        }
        return moveStatus;
    }

    /**
     * Update the board model to reflect the move that was made.
     *
     * @param player: The {@link Player} making the move.
     * @param move: The {@link Move}(s) that was made.
     * @param adjustedMove: A boolean flag determining if this move has already
     *                    been adjusted for the white player.
     */
    public void updateModel(Player player, Move move, boolean adjustedMove) {
        // make necessary adjustments to positions if the player is the white player.
        if (!adjustedMove && player.equals(whitePlayer)) {
            move = getAdjustedMove(move);
        }

        // Get the coordinates of the startPosition to use on the board model.
        Position startPosition = move.getStart();
        int startRow = startPosition.getRow();
        int startCell = startPosition.getCell();
        // Get the coordinates of the endPosition to use on the board model.
        Position endPosition = move.getEnd();
        int endRow = endPosition.getRow();
        int endCell = endPosition.getCell();

        // get the Space the move started and ended on to move the piece.
        Space startSpace = checkersBoard.getRow(startRow).getSpace(startCell);
        Space endSpace = checkersBoard.getRow(endRow).getSpace(endCell);

        // move the piece from the start space to the end space,
        // then make the piece null on the start space.
        endSpace.setPiece(startSpace.getPiece());
        startSpace.setPiece(null);

        int middleRow = (startRow + endRow) / 2;
        int middleCell = (startCell + endCell) / 2;
        Space middleSpace = checkersBoard.getRow(middleRow).getSpace(middleCell);

        // check that the middleSpace is valid, then
        // if there is a piece on the middle space, capture it.
        if (middleRow != startRow && middleRow != endRow &&
                middleCell != startCell && middleCell != endCell) {
            if (middleSpace.getPiece().getColor() == Color.RED) {
                // count this capture for the White player
                whitePlayer.capture();
            } else {
                // count this capture for the Red player
                redPlayer.capture();
            }
            middleSpace.setPiece(null);
        }
        // when the model is updated, the move is valid, so we need
        // to add this move to the linked list.
        movesMade.add(new BoardView(checkersBoard));
    }

    /**
     * Adjust the start and end rows sent by the client to match the acutal
     * positions on the board model.
     *
     * @param move: The Move sent by the client
     * @return a new Move adjusted to match the model.
     */
    private Move getAdjustedMove(Move move) {
        int adjustedStartRow = Math.abs(move.getStart().getRow() - 7);
        int adjustedEndRow = Math.abs(move.getEnd().getRow()- 7);
        move = new Move(new Position(adjustedStartRow,
                move.getStart().getCell()), new Position(adjustedEndRow,
                move.getEnd().getCell()));
        return move;
    }

    /**
     * Reverse the changes to the model regarding the last move.
     *
     * @param player: The player reverting their move.
     * @param move: The move to be reverted.
     */
    public void revertMove(Player player, Move move) {
        if (player.equals(whitePlayer)) {
            move = getAdjustedMove(move);
        }

        Position startPosition = move.getStart();
        int startRow = startPosition.getRow();
        int startCell = startPosition.getCell();
        Position endPosition = move.getEnd();
        int endRow = endPosition.getRow();
        int endCell = endPosition.getCell();

        // get the Spaces the move started and ended on.
        Space startSpace = checkersBoard.getRow(startRow).getSpace(startCell);
        Space endSpace = checkersBoard.getRow(endRow).getSpace(endCell);

        // if the move is a jump, move the piece from the end space back to the
        // start space
        if (move.isJump()) {
            startSpace.setPiece(endSpace.getPiece());
            endSpace.setPiece(null);
        }

        int middleRow = (startRow + endRow) / 2;
        int middleCell = (startCell + endCell) / 2;
        Space middleSpace = checkersBoard.getRow(middleRow).getSpace(middleCell);

        // check that the middleSpace is a valid space, and replace the piece
        // captured, and revert the captures performed by the players.
        if (middleRow != startRow && middleRow != endRow &&
                middleCell != startCell && middleCell != endCell) {
            if (startSpace.getPiece().getColor() == Color.RED) {
                middleSpace.setPiece(new Piece(Type.SINGLE, Color.WHITE));
                redPlayer.revertCapture();
            } else {
                middleSpace.setPiece(new Piece(Type.SINGLE, Color.RED));
                whitePlayer.revertCapture();
            }
        }
        // if the move is reverted, remove that move from the end of the
        // linked list.
        if (move.isJump()) {
            movesMade.removeLast();
        }
    }
}
