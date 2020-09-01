package com.webcheckers.ui;

import static com.webcheckers.ui.GetReplayGameRoute.MOVE_CONTROLLER_ATTR;
import static spark.Spark.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

import com.google.gson.Gson;

import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import spark.TemplateEngine;


/**
 * The server that initializes the set of HTTP request handlers.
 * This defines the <em>web application interface</em> for this
 * WebCheckers application.
 *
 * <p>
 * There are multiple ways in which you can have the client issue a
 * request and the application generate responses to requests. If your team is
 * not careful when designing your approach, you can quickly create a mess
 * where no one can remember how a particular request is issued or the response
 * gets generated. Aim for consistency in your approach for similar
 * activities or requests.
 * </p>
 *
 * <p>Design choices for how the client makes a request include:
 * <ul>
 *     <li>Request URL</li>
 *     <li>HTTP verb for request (GET, POST, PUT, DELETE and so on)</li>
 *     <li><em>Optional:</em> Inclusion of request parameters</li>
 * </ul>
 * </p>
 *
 * <p>Design choices for generating a response to a request include:
 * <ul>
 *     <li>View templates with conditional elements</li>
 *     <li>Use different view templates based on results of executing the client request</li>
 *     <li>Redirecting to a different application URL</li>
 * </ul>
 * </p>
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 */
public class WebServer {
  private static final Logger LOG = Logger.getLogger(WebServer.class.getName());

  //
  // Constants
  //

  /**
   * The URL pattern to request the Home page.
   */
  public static final String HOME_URL = "/";

  /**
   * The URL pattern to request the Sign-in interface.
   */
  public static final String SIGN_IN_URL = "/signin";

  /**
   * The URL pattern to request the Game page.
   */
  public static final String GAME_URL = "/game";

  /**
   * The URL pattern to request the Sign-Out page.
   */
  public static final String SIGN_OUT_URL = "/signout";

  /**
   * The URL pattern to request to see if a player has submitted their turn.
   */
  public static final String CHECK_TURN_URL = "/checkTurn";

  /**
   * The URL pattern to request validation of a move.
   */
  public static final String VALIDATE_MOVE_URL = "/validateMove";

  /**
   * The URL pattern to request to submit turn
   */
  public static final String SUBMIT_TURN_URL = "/submitTurn";

  /**
   * The URL pattern to resign from a game.
   */
  public static final String RESIGN_GAME_URL = "/resignGame";

  /**
   * The URL pattern to backup a move.
   */
  public static final String BACKUP_MOVE_URL = "/backupMove";

  /**
   * The URL pattern for spectators to use.
   */
  public static final String SPECTATOR_URL = "/spectator";

  /**
   * The URL pattern used for the replay feature.
   */
  public static final String REPLAY_URL = "/replay";

  /**
   * The URL pattern for spectators to exit a game that they are viewing.
   */
  public static final String STOP_WATCHING_URL = "/stopWatching";

  /**
   * The URL pattern for getting the next turn in replay mode.
   */
  public static final String NEXT_TURN_URL = "/nextTurn";

  /**
   * The URL pattern for getting the next turn in replay mode.
   */
  public static final String PREVIOUS_TURN_URL = "/previousTurn";

  //
  // Attributes
  //

  private final TemplateEngine templateEngine;
  private final Gson gson;
  private final PlayerLobby playerLobby;
  private final GameLibrary gameLibrary;

  //
  // Constructor
  //

  /**
   * The constructor for the Web Server.
   *
   * @param templateEngine
   *    The default {@link TemplateEngine} to render page-level HTML views.
   * @param gson
   *    The Google JSON parser object used to render Ajax responses.
   *
   * @throws NullPointerException
   *    If any of the parameters are {@code null}.
   */
  public WebServer(final TemplateEngine templateEngine, final Gson gson) {
    // validation
    Objects.requireNonNull(templateEngine, "templateEngine must not be null");
    Objects.requireNonNull(gson, "gson must not be null");
    //
    this.templateEngine = templateEngine;
    this.gson = gson;

    HashMap<String, Player> playerList = new HashMap<>();
    this.playerLobby = new PlayerLobby(playerList);

    HashMap<Integer, GameCenter> activeGameMap = new HashMap<>();
    HashMap<Integer, GameCenter> endedGameMap = new HashMap<>();
    this.gameLibrary = new GameLibrary(activeGameMap, endedGameMap);
  }

  //
  // Public methods
  //

  /**
   * Initialize all of the HTTP routes that make up this web application.
   *
   * <p>
   * Initialization of the web server includes defining the location for static
   * files, and defining all routes for processing client requests. The method
   * returns after the web server finishes its initialization.
   * </p>
   */
  public void initialize() {

    // Configuration to serve static files
    staticFileLocation("/public");

    //// Setting any route (or filter) in Spark triggers initialization of the
    //// embedded Jetty web server.

    //// A route is set for a request verb by specifying the path for the
    //// request, and the function callback (request, response) -> {} to
    //// process the request. The order that the routes are defined is
    //// important. The first route (request-path combination) that matches
    //// is the one which is invoked. Additional documentation is at
    //// http://sparkjava.com/documentation.html and in Spark tutorials.

    //// Each route (processing function) will check if the request is valid
    //// from the client that made the request. If it is valid, the route
    //// will extract the relevant data from the request and pass it to the
    //// application object delegated with executing the request. When the
    //// delegate completes execution of the request, the route will create
    //// the parameter map that the response template needs. The data will
    //// either be in the value the delegate returns to the route after
    //// executing the request, or the route will query other application
    //// objects for the data needed.

    //// FreeMarker defines the HTML response using templates. Additional
    //// documentation is at
    //// http://freemarker.org/docs/dgui_quickstart_template.html.
    //// The Spark FreeMarkerEngine lets you pass variable values to the
    //// template via a map. Additional information is in online
    //// tutorials such as
    //// http://benjamindparrish.azurewebsites.net/adding-freemarker-to-java-spark/.

    //// These route definitions are examples. You will define the routes
    //// that are appropriate for the HTTP client interface that you define.
    //// Create separate Route classes to handle each route; this keeps your
    //// code clean; using small classes.

    // Shows the Checkers game Home page.
    get(HOME_URL, new GetHomeRoute(playerLobby, templateEngine));

    // Shows the Checkers game Sign-in interface.
    get(SIGN_IN_URL, new GetSignInRoute(templateEngine));

    // Post sign-in information.
    post(SIGN_IN_URL, new PostSignInRoute(playerLobby));

    // Shows the Checkers game Game page.
    get(GAME_URL, new GetGameRoute(playerLobby, templateEngine, gson));

    // Post sign-out information
    post(SIGN_OUT_URL, new PostSignOutRoute(playerLobby));

    // Allows the client to check if a player has submitted their turn.
    post(CHECK_TURN_URL, new PostCheckTurnRoute(gson));

    // Allows the client to validate a move
    post(VALIDATE_MOVE_URL, new PostValidateMoveRoute(gson));

    // Allows the client to submit a move
    post(SUBMIT_TURN_URL, new PostSubmitTurn(gson));

    // Allows the client to resign from a game
    post(RESIGN_GAME_URL, new PostResignGameRoute(gson));

    // Allows the client to backup a move that was made before submitting
    post(BACKUP_MOVE_URL, new PostBackupMoveRoute(gson));

    // Allows a user to spectate an ongoing game.
    get(SPECTATOR_URL + GAME_URL, new GetSpectatorGameRoute(templateEngine, gson));

    // Allows the spectator client to check if a player has submitted a turn since the last refresh.
    post(SPECTATOR_URL + CHECK_TURN_URL, new PostCheckTurnRoute(gson));

    // Allows the spectator to return to the home page.
    get(SPECTATOR_URL + STOP_WATCHING_URL, new GetSpectatorStopWatchingRoute());

    // Allows the client to load a game into the replay view.
    get(REPLAY_URL + GAME_URL, new GetReplayGameRoute(templateEngine, gson));

    // Allows the client to return to the home page from the replay view.
    get(REPLAY_URL + STOP_WATCHING_URL, (request, response) -> {
      request.session().removeAttribute(MOVE_CONTROLLER_ATTR);
      response.redirect(HOME_URL);
      halt();
      return null;
    });

    // Allows the client to click the 'next' button in replay mode.
    post(REPLAY_URL + NEXT_TURN_URL, new PostReplayNextTurnRoute(gson));

    // Allows the client to click the 'previous' button in replay mode.
    post(REPLAY_URL + PREVIOUS_TURN_URL, new PostReplayPreviousTurnRoute(gson));

    //
    LOG.config("WebServer is initialized.");
  }

}