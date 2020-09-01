package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Color;
import com.webcheckers.model.Player;

import com.webcheckers.model.ViewMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import spark.*;

import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The unit test suite for the {@link GetGameRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class GetGameRouteTest {

    /**
     * The component under test.
     */
    private GetGameRoute CuT;
    private GameLibrary dependant;

    /**
     * The {@link PlayerLobby} (friendly entity) that the CuT depends on.
     */
    private PlayerLobby playerLobby;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";

    private Gson gson;
    private Request request;
    private Session sessionOne;
    private Session sessionTwo;
    private Response response;
    private TemplateEngine engine;
    private Player testPlayer1;
    private Player testPlayer2;

    @BeforeEach
    public void setup() {
        gson = new Gson();
        request = mock(Request.class);
        sessionOne = mock(Session.class);
        sessionTwo = mock(Session.class);
        when(request.session()).thenReturn(sessionOne);
        response = mock(Response.class);

        // set up the Player objects needed to start a game.
        testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        // set up the CuT dependencies
        playerLobby = new PlayerLobby(new HashMap<>());
        engine = mock(TemplateEngine.class);

        // sign-in the players
        playerLobby.signIn(PLAYER_ONE_NAME, testPlayer1);
        playerLobby.signIn(PLAYER_TWO_NAME, testPlayer2);


        CuT = new GetGameRoute(playerLobby, engine, gson);

        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        dependant = new GameLibrary(activeGameList, endedGameList);
    }

    /**
     * Test the scenario where one active player challenges another player to
     * a game of checkers.
     */
    @Test
    public void player_challenges_another() {
        // arrange the scenario: testPlayer1 will be set as the current user of the session.
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);

        // testPlayer1 challenges testPlayer2 to a match.
        when(request.queryParams(GetGameRoute.OPPONENT_NAME)).thenReturn(PLAYER_TWO_NAME);

        GameCenter gameCenter = dependant.createGame(testPlayer2, testPlayer1);


        // set up the TemplateEngineTester to verify that the rendered View is correct.
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);

        // Analyze the content passed into the render method
        //  * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        //  * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR,gameCenter.getGameID() + 1);
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        //  * test view name
        testHelper.assertViewName("game.ftl");

    }

    /**
     * Test the scenario where a player challenges another player who is already in a game.
     */
    @Test
    public void player_already_in_game() {
        // arrange the scenario: testPlayer1 will be set as the current user of the session.
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);

        // testPlayer1 challenges testPlayer2 to a match.
        when(request.queryParams(GetGameRoute.OPPONENT_NAME)).thenReturn(PLAYER_TWO_NAME);

        // simulate testPlayer2 already being in a game.
        testPlayer2.setInGame(true);

        try {
            CuT.handle(request, response);
            fail("Redirects invoke halt exceptions.");
        } catch (HaltException he) {
            // expected
        }

        // Analyze the results
        //  * redirect to the Home Page.
        verify(response).redirect(WebServer.HOME_URL);
    }

    /**
     * Test the scenario where one active player is challenged to a game of
     * checkers by another player.
     */
    @Test
    public void player_has_been_challenged() {
        // arrange the scenario: testPlayer1 will be set as the current user of the session.
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        // the checkersBoard both players will use to play a game.
        GameCenter gameCenter = dependant.createGame(testPlayer2, testPlayer1);

        // testPlayer1 is challenged to a match by testPlayer2.
        // attaching information provided by testPlayer2 to testPlayer1's session.
        when(sessionOne.attribute(GetGameRoute.CHALLENGER_ATTR)).thenReturn(PLAYER_TWO_NAME);
        when(sessionOne.attribute(GAME_ID_ATTR)).thenReturn(gameCenter.getGameID());

        // set up the TemplateEngineTester to verify that the rendered View is correct.
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);

        // Analyze the content passed into the render method
        //  * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        //  * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR, gameCenter.getGameID());
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getRotatedBoard());
        //  * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Make sure when the Game View is refreshed, the View-Model is built correctly.
     */
    @Test
    public void page_refresh() {
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        GameCenter gameCenter = dependant.createGame(testPlayer1, testPlayer2);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        // Analyze the content passed into the render method
        // * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        // * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR, gameCenter.getGameID());
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        // * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Test that the view model is build correctly if a player resigns
     */
    @Test
    public void resignation_test(){
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        GameCenter gameCenter = dependant.createGame(testPlayer1, testPlayer2);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));
        gameCenter.setResignStatus(PLAYER_ONE_NAME + " has resigned.");

        final Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", PLAYER_ONE_NAME + " has resigned.");

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        // Analyze the content passed into the render method
        // * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        // * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR, gameCenter.getGameID());
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Test that the view model is build correctly if the red player captures all pieces
     */
    @Test
    public void red_capture(){
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        GameCenter gameCenter = dependant.createGame(testPlayer1, testPlayer2);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        for (int i = 0; i < 12; i++){
            testPlayer1.capture();
        }

        final Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Player Two has captured all of the pieces");

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        // Analyze the content passed into the render method
        // * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        // * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR, gameCenter.getGameID());
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Test that the view model is build correctly if the white player captures all pieces
     */
    @Test
    public void white_capture(){
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        GameCenter gameCenter = dependant.createGame(testPlayer1, testPlayer2);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        for (int i = 0; i < 12; i++){
            testPlayer2.capture();
        }

        final Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Player Two has captured all of the pieces");

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        // Analyze the content passed into the render method
        // * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        // * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR, gameCenter.getGameID());
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Test that the view model is build correctly if the red pieces are all blocked
     */
    @Test
    public void red_blocked(){
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        GameCenter gameCenter = dependant.createGame(testPlayer1, testPlayer2);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        gameCenter.getSimpleMoveValidator().clearRedList();

        final Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Player One's pieces are all blocked");

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        // Analyze the content passed into the render method
        // * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        // * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR, gameCenter.getGameID());
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");

    }

    /**
     * Test that the view model is build correctly if the red pieces are all blocked
     */
    @Test
    public void white_blocked(){
        when(sessionOne.attribute(GetHomeRoute.CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        GameCenter gameCenter = dependant.createGame(testPlayer1, testPlayer2);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));

        gameCenter.getSimpleMoveValidator().clearWhiteList();

        final Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Player Two's pieces are all blocked");

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        // Analyze the content passed into the render method
        // * model is a non-null map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();
        // * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, "Game Page");
        testHelper.assertViewModelAttribute(GAME_ID_ATTR, gameCenter.getGameID());
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.PLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");

    }

}
