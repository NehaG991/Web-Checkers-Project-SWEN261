package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.MoveController;
import com.webcheckers.model.BoardView;
import com.webcheckers.util.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetReplayGameRoute.MOVE_CONTROLLER_ATTR;
import static com.webcheckers.ui.GetReplayGameRoute.NEW_BOARD;
import static com.webcheckers.util.Message.info;

/**
 * The UI controller used to tell the {@link MoveController} to
 * update the model when the user wants to display the previous turn.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PostReplayPreviousTurnRoute implements Route {

    private Gson gson;

    public PostReplayPreviousTurnRoute(Gson gson) {
        this.gson = gson;
    }

    /**
     * When a user clicks the 'Previous' button on the client, the MoveController
     * will update the model to the previous move available that was made during
     * the game.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     * @return A JSON converted {@link Message} with information about the
     *   status of the move.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session session = request.session();
        MoveController moveController = session.attribute(MOVE_CONTROLLER_ATTR);
        BoardView oldBoard = moveController.getPrevious();
        session.attribute(NEW_BOARD, oldBoard);
        Message previousMoveMessage = info("true");
        return gson.toJson(previousMoveMessage);
    }
}
