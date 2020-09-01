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
 * A controller to submit a player's turn.
 *
 * @author Neha Ghanta, ng8975@rit.edu
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PostSubmitTurn implements Route {

    private Gson gson;

    public PostSubmitTurn(Gson gson){
        this.gson = gson;
    }

    /**
     * Makes a move in the {@link GameCenter}, and responds to the client the
     * status of the submission.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     * @return a JSON converted {@link Message} with information about the
     * submission.
     */
    @Override
    public Object handle(Request request, Response response){
        final Session session = request.session();
        final int gameID = Integer.parseInt(request.queryParams(GAME_ID_ATTR));
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);
        BoardView checkersBoard = gameCenter.getCheckersBoard();
        ArrayList<Move> moves = session.attribute(PostValidateMoveRoute.CURRENT_MOVE_ATTR);
        Player currentUser = session.attribute(GetHomeRoute.CURRENT_USER_ATTR);
        session.removeAttribute(PostValidateMoveRoute.CURRENT_MOVE_ATTR);

        Message turnSubmitted = Message.error("You haven't made any moves yet.");
        if (moves != null) {
            for (Move currentMove : moves) {
                // only update the model if the move is not a jump, since jumps update
                // in a different place.
                if (!currentMove.isJump()) {
                    gameCenter.updateModel(currentUser, currentMove, false);
                }
                // If a jump move is still available, the turn isn't over.
                if (!gameCenter.checkForJumpMove(currentUser, currentMove)) {
                    turnSubmitted = Message.info("Turn Submitted.");
                } else {
                    turnSubmitted = Message.error("A jump move still exists that you must make.");
                }
            }
        }
        // if the submission is successful, change whose turn it is.
        if (turnSubmitted.getType() == Message.Type.INFO) {
            gameCenter.makeMove();
        }
        PostValidateMoveRoute.clearList();
        checkersBoard.promotePiece(checkersBoard);
        return gson.toJson(turnSubmitted);
    }
}
