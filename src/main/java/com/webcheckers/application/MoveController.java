package com.webcheckers.application;

import com.webcheckers.model.BoardView;

import java.util.LinkedList;

/**
 * A MoveController allows traversal through the doubly-linked list of moves
 * recorded during a Checkers game. It will also provide information about
 * whether another move is available.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class MoveController {

    /** A Linked List to store all moves made during a game into. */
    private LinkedList<BoardView> moves;

    /** Used to traverse the linked list in both directions. */
    private int linkedListIndex;

    /**
     * Instantiates the controller, depending on a LinkedList being provided.
     * Also sets up the index to index 0, the start of the LinkedList.
     *
     * @param moveList: The LinkedList containing all of the recorded moves
     *                from a Checkers game.
     */
    public MoveController(LinkedList<BoardView> moveList) {
        this.moves = moveList;
        linkedListIndex = 0;
    }

    /**
     * Check if there is another move available.
     *
     * @return true if there are more moves after this one in the list.
     * false if the current move is at the end of the list.
     */
    public boolean hasNext() {
        return linkedListIndex < moves.size() - 1;
    }

    /**
     * Check if there is a move that came before this one.
     *
     * @return true if there are moves before this one in the list.
     * false if the current move is at the beginning of the list.
     */
    public boolean hasPrevious() {
        return linkedListIndex > 0;
    }

    /**
     * Get the next move from the list of recorded moves, and modify the model
     * to reflect this move.
     */
    public BoardView getNext() {
        return moves.get(++linkedListIndex);
    }

    /**
     * Get the previous move from the list of recorded moves, and revert the
     * model back to it's previous state.
     */
    public BoardView getPrevious() {
        return moves.get(--linkedListIndex);
    }
}
