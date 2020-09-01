package com.webcheckers.ui;

import com.webcheckers.model.ViewMode;
import org.junit.jupiter.api.BeforeEach;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.webcheckers.ui.GetSignInRoute;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * The unit test suite for the {@link GetGameRoute} component.
 *
 * @author Neha Ghanta, ng8975@rit.edu
 */
@Tag("UI-tier")
class GetSignInRouteTest {

    /**
     * The component under test.
     */
    private GetSignInRoute CuT;
    private TemplateEngine engine;
    private Request request;
    private Response response;

    @BeforeEach
    void setUp() {
        engine = mock(TemplateEngine.class);
        CuT = new GetSignInRoute(engine);
        request = mock(Request.class);
        response = mock(Response.class);

    }

    /**
     * Test that the Sign-In view will render
     */
    @Test
    public void openSignIn(){

        // set up the TemplateEngineTester to verify that the rendered View is correct.
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);

        // Analyze the content passed into the render method
        //  * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        //   * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute("formType", "Sign-in");

        //  * test view name
        testHelper.assertViewName("signin.ftl");
    }
}