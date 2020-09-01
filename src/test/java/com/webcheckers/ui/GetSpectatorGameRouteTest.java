package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.Color;
import com.webcheckers.model.Player;
import com.webcheckers.model.ViewMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.ui.GetGameRoute.*;
import static com.webcheckers.ui.GetHomeRoute.CURRENT_USER_ATTR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link GetSpectatorGameRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class GetSpectatorGameRouteTest {

    private GetSpectatorGameRoute CuT;

    private static final String PLAYER_ONE_NAME = "Player One";
    private static final String PLAYER_TWO_NAME = "Player Two";
    private static final String SPECTATOR_NAME = "Spectator";

    private Gson gson;
    private Request request;
    private Session sessionOne;
    private Session sessionTwo;
    private Session spectatorSession;
    private Response response;
    private TemplateEngine engine;
    private Player testPlayer1;
    private Player testPlayer2;
    private Player spectator;

    private GameCenter gameCenter;
    private GameLibrary gameLibrary;

    /**
     * Set up two players, put them in a game, setup a spectator, sessions for all three
     * Players, and setup responses to calls for session attributes and queries.
     */
    @BeforeEach
    public void setup() {
        gson = new Gson();
        request = mock(Request.class);
        sessionOne = mock(Session.class);
        sessionTwo = mock(Session.class);
        spectatorSession = mock(Session.class);
        when(request.session()).thenReturn(spectatorSession);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);

        // set up the Player objects needed to start a game.
        testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);
        spectator = new Player(SPECTATOR_NAME, spectatorSession);

        CuT = new GetSpectatorGameRoute(engine, gson);

        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        gameLibrary = new GameLibrary(activeGameList, endedGameList);
        gameCenter = gameLibrary.createGame(testPlayer1, testPlayer2);

        when(spectatorSession.attribute(CURRENT_USER_ATTR)).thenReturn(spectator);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));
    }

    /**
     * Make sure that the page loads correctly when the game is in progress.
     */
    @Test
    public void load_spectator_view() {
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
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, spectator);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.SPECTATOR);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        // * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Make sure the spectator is notified of a resignation when one happens.
     */
    @Test
    public void spectator_view_player_resigned() {
        // simulate player resigning
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
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, spectator);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.SPECTATOR);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Make sure that when a player wins by capturing all of the other player's pieces,
     * the spectator sees that message.
     */
    @Test
    public void spectator_view_player_won_by_captures() {
        // simulate player winning by captures.
        for (int i = 0; i < 12; i++) {
            testPlayer1.capture();
        }

        final Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", String.format(CAPTURED_ALL_PIECES, PLAYER_TWO_NAME));

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
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, spectator);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.SPECTATOR);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");
    }

    /**
     * Make sure that when a player has all of their pieces blocked, the spectator can see the
     * message stating that this is the case.
     */
    @Test
    public void spectator_view_player_won_by_blocking() {
        // simulate a player's pieces being blocked.
        gameCenter.getSimpleMoveValidator().clearWhiteList();

        final Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", String.format(PIECES_BLOCKED, PLAYER_TWO_NAME));

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
        testHelper.assertViewModelAttribute(GetHomeRoute.CURRENT_USER_ATTR, spectator);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, testPlayer1);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, testPlayer2);
        testHelper.assertViewModelAttribute("viewMode", ViewMode.SPECTATOR);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", gameCenter.getCheckersBoard());
        testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
        // * test view name
        testHelper.assertViewName("game.ftl");
    }
}
