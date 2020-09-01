package com.webcheckers.ui;

import com.webcheckers.application.GameLibrary;
import spark.Route;
import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.model.*;
import com.webcheckers.util.Message;
import spark.Request;
import spark.Response;
import spark.Session;

import static com.webcheckers.ui.GetGameRoute.GAME_ID_ATTR;

/**
 *As a Player I want to forfeit a game so that the game can end early.
 *
 * @author Faizan Ahmed, fxa5716@rit.edu
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PostResignGameRoute implements Route {

        private Gson gson;

    /**
     * Constructor for the {@code POST /signin} route handler.
     * @param gson: an object to convert {@link Message}s to Json for HTTP
     *          responses.
     */
    public PostResignGameRoute(Gson gson) {
        this.gson = gson;
    }

    /**
     * Create the `resignGame` spark Route (this is a POST method) and
     * return a `Message` object telling the server that the `Player` is
     * resigning the game.
     * Notify the Player in the game that didn't resign,
     * that their opponent has resigned, and return them to the Home page.
     *
     * @param request: The HTTP request.
     * @param response: The HTTP response.
     * @return a message about the resignation, in JSON form.
     */
    public Object handle(Request request, Response response){
        final Session session = request.session();
        final int gameID = Integer.parseInt(request.queryParams(GAME_ID_ATTR));
        Player currentUser = session.attribute(GetHomeRoute.CURRENT_USER_ATTR);
        String username = currentUser.getName();
        String resignString = username + " has resigned.";
        Message resignMessage = Message.info(resignString);

        // get the gameCenter from the GameLibrary, change whose move it is to refresh
        // the opponent's game view.
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);
        Color currentTurn = gameCenter.getCurrentTurn();
        if (currentUser.getColor().equals(currentTurn)) {
            gameCenter.makeMove();
        }
        gameCenter.setResignStatus(resignString);
        return gson.toJson(resignMessage);
    }
}