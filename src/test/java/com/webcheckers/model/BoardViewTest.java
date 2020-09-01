package com.webcheckers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit test suite for the {@link BoardView} component.
 *
 * @author Thomas Daley, ted5363@rit.edu
 */
@Tag("Model-tier")
public class BoardViewTest {

    private BoardView CuT;
    private Piece redPiece;
    private Piece whitePiece;

    @BeforeEach
    public void setUp() {
        CuT = new BoardView();
        redPiece = new Piece(Type.SINGLE, Color.RED);
        whitePiece = new Piece(Type.SINGLE, Color.WHITE);

    }

    /**
     * Test the {@link BoardView#iterator()} method
     */
    @Test
    public void testIterator() {
        assertNotNull(CuT.iterator(), "Row list is not received");
    }

    /**
     * Test the {@link BoardView#promotePiece(BoardView) method}
     */
    @Test 
    public void testPromotePiece(){
        Space space = CuT.getRow(0).getSpace(1);
        space.setPiece(redPiece);
        Space space2 = CuT.getRow(7).getSpace(6);
        space2.setPiece(whitePiece);
        CuT.promotePiece(CuT);
        assertSame(Type.KING, redPiece.getType());
        assertSame(Type.KING, whitePiece.getType());
    }
    
}
