package com.webcheckers.ui;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetGameRoute.ACTIVE_COLOR;
import static spark.Spark.halt;

/**
 * The UI controller to clean up the session when leaving the Spectator view,
 * and returning to the Home page.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class GetSpectatorStopWatchingRoute implements Route {

    /**
     * Clean up the session attributes, and redirect to the Home page.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     * @return null
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session session = request.session();
        session.removeAttribute(ACTIVE_COLOR);
        response.redirect(WebServer.HOME_URL);
        halt();
        return null;
    }
}
