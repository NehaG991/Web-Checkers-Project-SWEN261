package com.webcheckers.application;

import com.webcheckers.model.BoardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit test suite for the {@link MoveController} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Application-tier")
public class MoveControllerTest {

    private MoveController CuT;

    private LinkedList<BoardView> moves;

    private BoardView originalBoardState;
    private BoardView nextBoardState;

    /**
     * Before each test, setup a linked list to store board states in.
     * Set up at least two board states, and instantiate the CuT.
     */
    @BeforeEach
    public void setup() {
        moves = new LinkedList<>();
        originalBoardState = new BoardView();
        nextBoardState = new BoardView();

        CuT = new MoveController(moves);
    }

    /**
     * Make sure the {@link MoveController#hasNext()} method is working properly.
     */
    @Test
    public void verify_list_has_next_move() {
        moves.add(originalBoardState);
        // since there is only one state in the list, there shouldn't be a
        // 'next' state.
        assertFalse(CuT.hasNext());

        moves.add(nextBoardState);
        // since there are now two states, there should be a 'next' state.
        assertTrue(CuT.hasNext());
    }

    /**
     * Make sure the {@link MoveController#hasPrevious()} method is working
     * properly.
     */
    @Test
    public void verify_list_has_previous_move() {
        moves.add(originalBoardState);
        moves.add(nextBoardState);
        // since there are two states in the list, but we are still pointed to
        // the beginning of that list, we shouldn't have a 'previous' move yet.
        assertFalse(CuT.hasPrevious());

        // simulate moving to the nextBoardState, giving us a 'previous' move
        // that is available.
        CuT.getNext();

        assertTrue(CuT.hasPrevious());
    }

    /**
     * Make sure that we do in fact get the 'next' move from the linked list
     * when calling the {@link MoveController#hasNext()} method.
     */
    @Test
    public void verify_get_next_move() {
        moves.add(originalBoardState);
        moves.add(nextBoardState);

        // we start on the original board state, so we expect to get the
        // next Board state.
        assertEquals(nextBoardState, CuT.getNext());
    }

    /**
     * Make sure that we do in fact get the 'previous' move from the linked list
     * when calling the {@link MoveController#hasPrevious()} method.
     */
    @Test
    public void verify_get_previous_move() {
        moves.add(originalBoardState);
        moves.add(nextBoardState);

        // simulate pointing the linked list to the next board state,
        // so that we have a previous move available, and can get that.
        CuT.getNext();

        assertEquals(originalBoardState, CuT.getPrevious());
    }
}
