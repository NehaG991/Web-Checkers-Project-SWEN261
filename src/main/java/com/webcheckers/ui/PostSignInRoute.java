package com.webcheckers.ui;

import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;
import spark.*;


import static spark.Spark.halt;

/**
 * The {@code POST /signin} route handler.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PostSignInRoute implements Route {

    // Constants
    static final String USERNAME_PARAM = "myUsername";

    static final String SIGN_IN_STATUS = "Sign-in Status";
    static final String BAD_SIGN_IN_MSG = "%s is already in use. Try another username.";
    static final String SIGNED_IN_MSG = "%s has signed in.";


    // Attributes
    private final PlayerLobby playerLobby;

    /**
     * Constructor for the {@code POST /signin} route handler.
     * @param playerLobby: The {@link PlayerLobby} used to store active players.
     */
    public PostSignInRoute(PlayerLobby playerLobby) {
        this.playerLobby = playerLobby;
    }

    /**
     * Handles sign-in functionality, only letting new players into the
     * {@link PlayerLobby} if the desired username is not already in use.
     * If a {@link Player} is signed in, that user will be bound to the
     * session. A sign-in status message will always be bound to the session
     * to indicate to the user whether a sign-in was successful or not.
     * The player will be redirected to the Home page after this method is
     * finished executing.
     *
     * @param request: The HTTP request.
     * @param response: The HTTP response.
     * @return null
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session session = request.session();
        final String username = request.queryParams(USERNAME_PARAM);
        Message signInStatus;
        if (playerLobby.userNameInUse(username)) {
            signInStatus = Message.error(String.format(BAD_SIGN_IN_MSG, username));
        } else {
            Player currentUser = new Player(username, session);
            playerLobby.signIn(username, currentUser);
            signInStatus = Message.info(String.format(SIGNED_IN_MSG, username));
            session.attribute("currentUser", currentUser);
        }
        session.attribute(SIGN_IN_STATUS, signInStatus);
        response.redirect(WebServer.HOME_URL);
        halt();
        return null;
    }
}
