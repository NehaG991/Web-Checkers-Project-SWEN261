package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;
import com.webcheckers.model.Move;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.ArrayList;

import static com.webcheckers.ui.GetGameRoute.GAME_CENTER_ATTR;
import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;
import static com.webcheckers.ui.GetHomeRoute.CURRENT_USER_ATTR;
import static com.webcheckers.ui.PostValidateMoveRoute.CURRENT_MOVE_ATTR;

/**
 * A controller to take back moves on the checkers board that weren't meant
 * to be made.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PostBackupMoveRoute implements Route {

    private Gson gson;

    static final String MOVE_CANCELLED = "Cancelled previous move.";
    static final String MOVE_NOT_CANCELLED = "Could not cancel previous move.";

    public PostBackupMoveRoute(Gson gson) {
        this.gson = gson;
    }

    /**
     * Attempts to remove the current move from the session, and returns a
     * {@link Message} about the status of the removal.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     *
     * @return a JSON converted Message about the status of the removal of this
     * {@link Move}.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session session = request.session();
        Player currentUser = session.attribute(CURRENT_USER_ATTR);
        ArrayList<Move> moves = session.attribute(CURRENT_MOVE_ATTR);
        final int gameID = Integer.parseInt(request.queryParams(GAME_ID_ATTR));
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);

        Message cancelledMessage;
        if (moves.size() > 0) {
            gameCenter.revertMove(currentUser, moves.remove(moves.size() - 1));
            cancelledMessage = Message.info(MOVE_CANCELLED);
        } else {
            cancelledMessage = Message.error(MOVE_NOT_CANCELLED);
        }
        return gson.toJson(cancelledMessage);
    }
}
