package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.model.Color;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetGameRoute.ACTIVE_COLOR;
import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;

/**
 * Use this class to process checkTurn calls from the client.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PostCheckTurnRoute implements Route {

    private Gson gson;

    /**
     * Instantiates the gson object to be used for HTTP responses.
     *
     * @param gson: an object to convert {@link Message}s to Json for HTTP
     *            responses.
     */
    public PostCheckTurnRoute(Gson gson) {
        this.gson = gson;
    }

    /**
     * Retrieves the GameCenter from this User's session, as well as the User's
     * {@link Player} object, and uses that information to determine if it's
     * this Player's turn or not.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     *
     * @return A JSON converted {@link Message} with information about who's
     * turn it is.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session session = request.session();
        final int gameID = Integer.parseInt(request.queryParams(GAME_ID_ATTR));
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);
        Player currentUser = session.attribute(GetHomeRoute.CURRENT_USER_ATTR);
        Message checkTurnMessage;
        if (checkPlayerTurn(currentUser, gameCenter, session)) {
            checkTurnMessage = Message.info("true");
        } else {
            checkTurnMessage = Message.info("false");
        }
        return gson.toJson(checkTurnMessage);
    }

    /**
     * Used to check if it's the currentUser's turn.
     *
     * @param player: The currentUser of the web session.
     * @param gameCenter: the GameCenter containing all information about the
     *                  checkers game being played.
     * @param session: This Player's HTTP session.
     * @return a boolean, true if it is this player's turn, false otherwise.
     */
    public boolean checkPlayerTurn(Player player, GameCenter gameCenter, Session session) {
        switch (gameCenter.getCurrentTurn()) {
            case RED:
                if (!gameCenter.getRedPlayer().equals(player) && !gameCenter.getWhitePlayer().equals(player)) {
                    // check if the spectator needs to be refreshed
                    if (session.attribute(ACTIVE_COLOR) == Color.WHITE) {
                        session.attribute(ACTIVE_COLOR, Color.RED);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return gameCenter.getRedPlayer().equals(player);
                }
            case WHITE:
                if (!gameCenter.getRedPlayer().equals(player) && !gameCenter.getWhitePlayer().equals(player)) {
                    if (session.attribute(ACTIVE_COLOR) == Color.RED) {
                        session.attribute(ACTIVE_COLOR, Color.WHITE);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return gameCenter.getWhitePlayer().equals(player);
                }
            default:
                return false;
        }
    }
}
