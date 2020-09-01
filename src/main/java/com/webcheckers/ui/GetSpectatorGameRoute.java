package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.BoardView;
import com.webcheckers.model.Player;
import com.webcheckers.model.ViewMode;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.ui.GetGameRoute.*;
import static com.webcheckers.ui.GetHomeRoute.CURRENT_USER_ATTR;
import static com.webcheckers.ui.GetHomeRoute.TITLE_ATTR;

/**
 * The UI controller to get the Game Page for a Spectator.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class GetSpectatorGameRoute implements Route {

    private final TemplateEngine templateEngine;
    private Gson gson;

    /**
     * Constructor for the {@code GET /spectator/game} route handler.
     * @param templateEngine: The template engine used for rendering an HTML page.
     */
    public GetSpectatorGameRoute(final TemplateEngine templateEngine, Gson gson) {
        this.templateEngine = templateEngine;
        this.gson = gson;
    }

    /**
     * If a {@link Player} clicks on an active game on the Home page, this
     * method will build the game view for that user, in Spectator mode.
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
        BoardView checkersBoard = gameCenter.getCheckersBoard();

        // if no active color is attached to the session, attach it.
        if (session.attribute(ACTIVE_COLOR) == null) {
            session.attribute(ACTIVE_COLOR, gameCenter.getCurrentTurn());
        }

        Map<String, Object> modeOptions = checkForGameEnd(gameCenter);
        if (modeOptions.containsKey("isGameOver")) {
            vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
        }

        // build the view-model
        vm.put(TITLE_ATTR, "Game Page");
        vm.put(GAME_ID_ATTR, gameID);
        vm.put(CURRENT_USER_ATTR, currentUser);
        vm.put(RED_PLAYER, gameCenter.getRedPlayer());
        vm.put(WHITE_PLAYER, gameCenter.getWhitePlayer());
        vm.put("viewMode", ViewMode.SPECTATOR);
        vm.put(ACTIVE_COLOR, gameCenter.getCurrentTurn());
        vm.put("board", checkersBoard);

        return templateEngine.render(new ModelAndView(vm, "game.ftl"));
    }

    private Map<String, Object> checkForGameEnd(GameCenter gameCenter) {
        final Map<String, Object> modeOptions = new HashMap<>(2);
        if (gameCenter.capturedAllPieces()) {
            modeOptions.put("isGameOver", true);
            if (gameCenter.getRedPlayer().getCaptures() == 12) {
                modeOptions.put("gameOverMessage", String.format(CAPTURED_ALL_PIECES,
                        gameCenter.getRedPlayer().getName()));
            } else {
                modeOptions.put("gameOverMessage", String.format(CAPTURED_ALL_PIECES,
                        gameCenter.getWhitePlayer().getName()));
            }
        } else if (gameCenter.blockedPiecesRed()) {
            modeOptions.put("isGameOver", true);
            modeOptions.put("gameOverMessage", String.format(PIECES_BLOCKED,
                    gameCenter.getRedPlayer().getName()));
        } else if (gameCenter.blockedPiecesWhite()) {
            modeOptions.put("isGameOver", true);
            modeOptions.put("gameOverMessage", String.format(PIECES_BLOCKED,
                    gameCenter.getWhitePlayer().getName()));
        } else if (gameCenter.getResignStatus() != null) {
            modeOptions.put("isGameOver", true);
            modeOptions.put("gameOverMessage", gameCenter.getResignStatus());
        }
        return modeOptions;
    }
}
