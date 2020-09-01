package com.webcheckers.model;

import com.webcheckers.ui.TemplateEngineTester;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.ModelAndView;
import spark.TemplateEngine;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link Piece} component.
 *
 * @author Thomas Daley, ted5363@rit.edu
 */
@Tag("Model-tier")
public class PieceTest {

    private Piece CuT;
    private BoardView board;
    private TemplateEngine engine;

    @BeforeEach
    public void setUp() {
        this.board = mock(BoardView.class);
        this.CuT = new Piece(Type.SINGLE, Color.RED);
        this.engine = mock(TemplateEngine.class);
    }

    /**
     * Test the {@link Piece#getType()} method
     */
    @Test
    public void testGetType() {
        assertNotNull(CuT.getType(), "Type attribute is null");
    }

    /**
     * Test the {@link Piece#getColor()} method
     */
    @Test
    public void testGetColor() {
        assertNotNull(CuT.getColor(), "Color attribute is null");
    }

    /**
     * Test the {@link Piece#setKing()} method
     */
    @Test
    public void testSetKing() {
        CuT.setKing();
        assertEquals(Type.KING, CuT.getType());
    }

}