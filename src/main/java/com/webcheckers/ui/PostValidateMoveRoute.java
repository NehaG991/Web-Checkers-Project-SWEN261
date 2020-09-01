package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.*;
import com.webcheckers.util.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.ArrayList;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;

/**
 * A controller to validate moves on a checkers board.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PostValidateMoveRoute implements Route {

    private Gson gson;

    static ArrayList<Move> consecutiveMoves;

    static final String CURRENT_MOVE_ATTR = "currentMove";
    static final String VALID_MOVE = "Valid move.";
    static final String ACTION_DATA_ATTR = "actionData";


    public PostValidateMoveRoute(Gson gson) {
        this.gson = gson;
        consecutiveMoves = new ArrayList<>();
    }

    /**
     * Processes the move made by the user, and gives a JSON response with
     * information about the validity of the move.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     *
     * @return a JSON converted {@link Message} about the validity of the move.
     */
    @Override
    public Object handle(Request request, Response response) {
        // Use the GameCenter from this session to get the game board.
        final Session session = request.session();
        final int gameID = Integer.parseInt(request.queryParams(GAME_ID_ATTR));
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);

        // get the current user to check if we need an offset for checking the model.
        Player currentUser = session.attribute(GetHomeRoute.CURRENT_USER_ATTR);

        String currentMoveJson = request.queryParams(ACTION_DATA_ATTR);
        Move currentMove = gson.fromJson(currentMoveJson, Move.class);

        consecutiveMoves.add(currentMove);

        Message moveResponse = Message.error("You haven't made a move yet.");
        for (Move move: consecutiveMoves) {
            moveResponse = gameCenter.validateMove(currentUser, move);
            if (moveResponse.getType() == Message.Type.INFO) {
                session.attribute(CURRENT_MOVE_ATTR, consecutiveMoves);
            }
        }
        // if the move isn't valid, we need to clear the list of moves to avoid
        // incorrect modification of the model.
        if (moveResponse.getType() == Message.Type.ERROR) {
            clearList();
        }
        return gson.toJson(moveResponse);
    }

    public static void clearList() {
        consecutiveMoves.clear();
    }
}
