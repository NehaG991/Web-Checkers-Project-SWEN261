package com.webcheckers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The unit test suite for the {@link Position} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("Model-tier")
public class PositionTest {

    private Position CuT;

    private int row;
    private int cell;

    /**
     * Set up a new Position to be used for each test.
     */
    @BeforeEach
    public void setup() {
        this.row = 5;
        this.cell = 4;

        CuT = new Position(row, cell);
    }

    /**
     * Make sure the getRow method returns the expected value.
     */
    @Test
    public void verify_row() {
        assertEquals(this.row, CuT.getRow());
    }

    /**
     * Make sure the getCell method returns the expected value.
     */
    @Test
    public void verify_cell() {
        assertEquals(this.cell, CuT.getCell());
    }
}
