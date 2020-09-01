package com.webcheckers.ui;

import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import spark.*;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;

import static com.webcheckers.ui.GetHomeRoute.CURRENT_USER_ATTR;
import static com.webcheckers.ui.GetHomeRoute.TITLE_ATTR;
import static com.webcheckers.ui.PostSignInRoute.SIGN_IN_STATUS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The unit test suite for the {@link GetHomeRoute} model.
 *
 * @author Faizan Ahmed, fxa5716@rit.edu
 */

@Tag("UI-tier")
class GetHomeRouteTest {

    /**
      * The component under test.
      */
    private GetHomeRoute CuT;
    private GameLibrary gameLibrary;

    /**
      * The {@link PlayerLobby} (friendly entity) that the CuT depends on.
      */
    private PlayerLobby playerLobby;
    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Request request;
    private Session sessionOne;
    private Session sessionTwo;
    private Response response;
    private TemplateEngine engine;
    private Player testPlayer1;
    private Player testPlayer2;


    /** Setup new mock objects for each test. */
    @BeforeEach
    public void setup() {
        request = mock(Request.class);
        sessionOne = mock(Session.class);
        sessionTwo = mock(Session.class);
        when(request.session()).thenReturn(sessionOne);
        response = mock(Response.class);

        testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        playerLobby = new PlayerLobby(new HashMap<>());
        engine = mock(TemplateEngine.class);

        playerLobby.signIn(PLAYER_ONE_NAME, testPlayer1);
        playerLobby.signIn(PLAYER_TWO_NAME, testPlayer2);

        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        gameLibrary = new GameLibrary(activeGameList, endedGameList);

        CuT = new GetHomeRoute(playerLobby, engine);
}
    /**Test the {@link GetHomeRoute#handle(Request, Response)} ()}  method*/
    @Test
    void handle() {
        when(request.queryParams(GetGameRoute.OPPONENT_NAME)).thenReturn(PLAYER_TWO_NAME);
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);
        testHelper.assertViewModelExists();
        testHelper.assertViewModelAttribute(TITLE_ATTR, "Welcome!");
    }

    @Test
    public void test_current_user_signed_in() {
        // set up the objects to return upon request
        Message signInStatus = Message.info(PLAYER_ONE_NAME + " has signed in.");
        when(sessionOne.attribute(CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        when(sessionOne.attribute(SIGN_IN_STATUS)).thenReturn(signInStatus);

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // invoke the test
        CuT.handle(request, response);

        // Analyze the content passed into the render method
        //  * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        //  * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Welcome!");
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetHomeRoute.MESSAGE_ATTR, signInStatus);
        //  * test view name
        testHelper.assertViewName("home.ftl");
    }
}