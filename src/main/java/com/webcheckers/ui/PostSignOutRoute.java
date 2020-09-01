package com.webcheckers.ui;

import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;
import spark.*;

import java.util.Objects;

import static spark.Spark.halt;
/**
 * The {@code POST /signout} route handler.
 *
 * @author Neha Ghanta
 */

public class PostSignOutRoute implements Route{


    // Attributes
    private final PlayerLobby playerLobby;

    /**
     * Constructor for the {@code POST /signout} route handler.
     * @param playerLobby: The {@link PlayerLobby} used to store active players.
     */
    public PostSignOutRoute(PlayerLobby playerLobby){
        this.playerLobby = playerLobby;
    }

    /**
     * Handles sign-out functionality, removing the current user from the
     * {@link PlayerLobby}
     * The {@link Player} bound the session becomes null.
     * session.
     *
     * The player will be redirected to the Home page after this method is
     * finished executing.
     *
     * @param request: The HTTP request.
     * @param response: The HTTP response.
     * @return null
     */
    public Object handle(Request request, Response response){
        final Session session = request.session();
        Player currentUser = session.attribute("currentUser");
        String username = currentUser.getName();
        playerLobby.signOut(username, currentUser);
        session.attribute("currentUser", null);
        session.attribute(PostSignInRoute.SIGN_IN_STATUS, null);

        response.redirect(WebServer.HOME_URL);
        return null;
    }

}
