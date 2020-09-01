package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.BoardView;
import com.webcheckers.model.Color;
import com.webcheckers.model.Player;
import com.webcheckers.model.ViewMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.HashMap;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static com.webcheckers.ui.GetHomeRoute.CURRENT_USER_ATTR;
import static com.webcheckers.ui.GetReplayGameRoute.NEW_BOARD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link GetReplayGameRoute} component.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
@Tag("UI-tier")
public class GetReplayGameRouteTest {

    private GetReplayGameRoute CuT;

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

    private GameCenter gameCenter;
    private GameLibrary gameLibrary;
    private BoardView newBoardState;

    /**
     * Set up two players, sessions for both Players, put them in a game,
     * and setup responses to calls for session attributes and queries.
     */
    @BeforeEach
    public void setup() {
        gson = new Gson();
        request = mock(Request.class);
        sessionOne = mock(Session.class);
        when(request.session()).thenReturn(sessionOne);
        sessionTwo = mock(Session.class);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);

        testPlayer1 = new Player(PLAYER_ONE_NAME, sessionOne);
        testPlayer2 = new Player(PLAYER_TWO_NAME, sessionTwo);

        CuT = new GetReplayGameRoute(engine, gson);

        HashMap<Integer, GameCenter> activeGameList = new HashMap<>();
        HashMap<Integer, GameCenter> endedGameList = new HashMap<>();
        gameLibrary = new GameLibrary(activeGameList, endedGameList);
        gameCenter = gameLibrary.createGame(testPlayer1, testPlayer2);
        newBoardState = new BoardView();

        when(sessionOne.attribute(CURRENT_USER_ATTR)).thenReturn(testPlayer1);
        when(request.queryParams(GAME_ID_ATTR)).thenReturn(String.valueOf(gameCenter.getGameID()));
        when(sessionOne.attribute(NEW_BOARD)).thenReturn(newBoardState);
    }

    /**
     * Make sure that the page loads correctly.
     */
    @Test
    public void load_replay_view() {
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
        testHelper.assertViewModelAttribute("viewMode", ViewMode.REPLAY);
        testHelper.assertViewModelAttribute("activeColor", Color.RED);
        testHelper.assertViewModelAttribute("board", newBoardState);
        // * test view name
        testHelper.assertViewName("game.ftl");
    }
}
