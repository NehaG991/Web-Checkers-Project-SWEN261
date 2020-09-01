package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.application.MoveController;
import com.webcheckers.model.BoardView;
import com.webcheckers.model.Player;
import com.webcheckers.model.ViewMode;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.ui.GetGameRoute.*;
import static com.webcheckers.ui.GetGameRoute.ACTIVE_COLOR;
import static com.webcheckers.ui.GetHomeRoute.CURRENT_USER_ATTR;
import static com.webcheckers.ui.GetHomeRoute.TITLE_ATTR;

/**
 * The UI controller to get the Game Page for the Replay feature.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class GetReplayGameRoute implements Route {

    private final TemplateEngine templateEngine;

    private Gson gson;

    static final String MOVE_CONTROLLER_ATTR = "moveController";
    static final String HAS_NEXT = "hasNext";
    static final String HAS_PREVIOUS = "hasPrevious";
    static final String NEW_BOARD = "newBoard";

    /**
     * Constructor for the {@code GET /replay/game} route handler.
     * @param templateEngine: The template engine used for rendering an HTML page.
     */
    public GetReplayGameRoute(final TemplateEngine templateEngine, Gson gson) {
        this.templateEngine = templateEngine;
        this.gson = gson;
    }

    /**
     * If a {@link Player} clicks on a game that has ended on the Home page, this
     * method will build the game view for that user, in Replay mode.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     * @return the rendered HTML for the Game Page.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session session = request.session();
        Map<String, Object> vm = new HashMap<>();

        Player currentUser = session.attribute(CURRENT_USER_ATTR);
        final int gameID = Integer.parseInt(request.queryParams(GAME_ID_ATTR));
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);
        // reset the game board for each client replaying the same game.

        MoveController moveController;
        // if the move controller hasn't been instantiated, create a new move controller,
        // and supply the view with information about the availability of moves.
        if (session.attribute(MOVE_CONTROLLER_ATTR) == null) {
            moveController = new MoveController(gameCenter.getMovesMade());
            // attach the move controller to this user's session.
            session.attribute(MOVE_CONTROLLER_ATTR, moveController);
        } else {
            moveController = session.attribute(MOVE_CONTROLLER_ATTR);
        }

        Map<String, Object> modeOptions = new HashMap<>(2);
        modeOptions.put(HAS_NEXT, moveController.hasNext());
        modeOptions.put(HAS_PREVIOUS, moveController.hasPrevious());
        vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));

        // build the view-model
        vm.put(TITLE_ATTR, "Game Page");
        vm.put(GAME_ID_ATTR, gameID);
        vm.put(CURRENT_USER_ATTR, currentUser);
        vm.put(RED_PLAYER, gameCenter.getRedPlayer());
        vm.put(WHITE_PLAYER, gameCenter.getWhitePlayer());
        vm.put("viewMode", ViewMode.REPLAY);
        vm.put(ACTIVE_COLOR, gameCenter.getCurrentTurn());
        if (session.attribute(NEW_BOARD) == null) {
            vm.put("board", new BoardView());
        } else {
            vm.put("board", session.attribute(NEW_BOARD));
        }

        return templateEngine.render(new ModelAndView(vm, "game.ftl"));
    }
}
