package com.webcheckers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit test suite for the {@link Move} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Model-tier")
public class MoveTest {

    private Move CuT;
    private Move identicalMove;
    private Move notEqualMove;
    private Move jumpMove;

    private Position start;
    private Position end;

    /**
     * Setup a start and end {@link Position}, and create a new Move using
     * those positions for each test.
     */
    @BeforeEach
    public void setup() {
        this.start = new Position(5, 4);
        this.end = new Position(4, 5);
        Position jumpEnd = new Position(3, 2);

        CuT = new Move(start, end);
        identicalMove = new Move(start, end);
        notEqualMove = new Move(end, start);
        jumpMove = new Move(start, jumpEnd);
    }

    /**
     * Make sure the getStart method returns the expected value.
     */
    @Test
    public void verify_start() {
        assertEquals(this.start, CuT.getStart());
    }

    /**
     * Make sure the getEnd method returns the expected value.
     */
    @Test
    public void verify_end() {
        assertEquals(this.end, CuT.getEnd());
    }

    /**
     * Test the {@link Move#equals(Object)} method to make sure that two moves
     * with identical start and end positions are in fact equal, and that two
     * moves with different start and end positions are *not* equal.
     */
    @Test
    public void verify_equals_method() {
        assertTrue(CuT.equals(identicalMove));
        assertFalse(CuT.equals(notEqualMove));
    }

    /**
     * Test the {@link Move#isJump()} method to make sure that it recognizes
     * that a jump move is in fact a jump move, and that a move that isn't
     * a jump, is in fact not.
     */
    @Test
    public void verify_jump_move() {
        assertTrue(jumpMove.isJump());
        assertFalse(CuT.isJump());
    }
}
