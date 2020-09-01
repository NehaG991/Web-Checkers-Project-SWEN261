package com.webcheckers.model;

import com.webcheckers.model.Space;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Session;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * The unit test suite for the {@link Space} model.
 *
 * @author Faizan Ahmed, fxa5716@rit.edu
 * @author Andrew Frank, ajf8248@rit.edu
 */

@Tag("Model-tier")
class SpaceTest {

    private Space lightSpace;
    private Space darkSpace;
    private Space emptyDarkSpace;
    private Space outofboundSpace;
    private Row oddRow;
    private Row evenRow;

    private static final int ODD_COLUMN = 5;
    private static final int EVEN_COLUMN = 4;
    private static final int EVEN_ROW = 4;

    private static final int OUT_OF_BOUND = 8;

/** Setup new mock objects for each test. */

    @BeforeEach
    void setUp() {
        oddRow = new Row(ODD_COLUMN);
        evenRow = new Row(EVEN_ROW);
        // lightSpace is position (5, 5) on the board
        lightSpace = new Space(ODD_COLUMN, oddRow);
        // darkSpace is position (5, 4) on the board.
        darkSpace = new Space(EVEN_COLUMN, oddRow);
        // emptyDarkSpace is position (4, 5) on the board.
        emptyDarkSpace = new Space(ODD_COLUMN, evenRow);
        outofboundSpace = new Space(OUT_OF_BOUND, oddRow);
    }

    /** Test the {@link Space#getCellIdx()}  method    */

    @Test
    void getCellIdx() {
    //Testing if we are getting an integer representing the space column
    assertEquals(lightSpace.getCellIdx(), 5);
    }


    /**Test the {@link Space#isValid()}  method*/

    @Test
    void isValid() {
        //Testing if space is valid spot
        assertEquals(lightSpace.isValid(), false);

    }

    /**
     * Test the {@link Space#isValid()} method to make sure than an occupied
     * dark space on the checkers board is not a valid place to put a piece.
     */
    @Test
    public void isValidDark() {
        assertFalse(darkSpace.isValid());
    }

    /**
     * Test the {@link Space#isValid()} method to make sure that an empty
     * dark space on the checkers board is a valid place to put a piece.
     */
    @Test public void isValidEmptyDark() {
        assertTrue(emptyDarkSpace.isValid());
    }

    /**Test the {@link Space#outOfBounds()} ()} method */
    @Test
    void outOfBounds() {
        //Testing if we are outside of bounds
        assertEquals(lightSpace.outOfBounds(), false);

        }

    @Test
    void insideOfBounds() {
        //Testing if we are inside of bounds
        assertEquals(outofboundSpace.outOfBounds(), true);
    }


    /**Test the {@link Space#hasPiece()} ()} ()} method*/
    @Test
    void hasPiece() {
        //Testing if space contains piece
        assertEquals(lightSpace.hasPiece(), false);
        assertTrue(darkSpace.hasPiece());
        assertFalse(emptyDarkSpace.hasPiece());
    }


    /** Test the {@link Space#getPiece()} ()} method*/
    @Test
    void getPiece() {
        //Testing if space contains nothing
        assertEquals(lightSpace.getPiece(), null);
        assertNotNull(darkSpace.getPiece());
        assertNull(emptyDarkSpace.getPiece());
    }
}