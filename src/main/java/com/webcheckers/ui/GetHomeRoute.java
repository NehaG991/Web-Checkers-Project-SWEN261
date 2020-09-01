package com.webcheckers.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import spark.*;

import com.webcheckers.util.Message;

import static com.webcheckers.application.GameLibrary.getActiveGameList;
import static com.webcheckers.application.GameLibrary.getEndedGameList;
import static spark.Spark.halt;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 */
public class GetHomeRoute implements Route {
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  private static final String WELCOME_MSG = "Welcome to the world of online Checkers. %d active users";

  static final String TITLE_ATTR = "title";
  static final String MESSAGE_ATTR = "message";
  static final String CURRENT_USER_ATTR = "currentUser";
  static final String PLAYER_LIST = "playerList";
  static final String ACTIVE_GAME_LIST = "activeGameList";
  static final String ENDED_GAME_LIST = "endedGameList";

  private final TemplateEngine templateEngine;
  private final PlayerLobby playerLobby;

  /**
   * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
   *
   * @param templateEngine
   *   the HTML template rendering engine
   */
  public GetHomeRoute(PlayerLobby playerLobby, final TemplateEngine templateEngine) {
    this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    this.playerLobby = Objects.requireNonNull(playerLobby, "playerLobby is required");
    //
    LOG.config("GetHomeRoute is initialized.");
  }

  /**
   * Render the WebCheckers Home page.
   *
   * @param request
   *   the HTTP request
   * @param response
   *   the HTTP response
   *
   * @return
   *   the rendered HTML for the Home page
   */
  @Override
  public Object handle(Request request, Response response) {
    LOG.finer("GetHomeRoute is invoked.");
    final Session session = request.session();
    //
    Map<String, Object> vm = new HashMap<>();
    vm.put(TITLE_ATTR, "Welcome!");

    // check if a user has been bound to the session.
    Player currentUser = session.attribute("currentUser");
    ArrayList<Player> players = playerLobby.getPlayerList();
    ArrayList<GameCenter> activeGames = getActiveGameList();
    ArrayList<GameCenter> endedGames = getEndedGameList();
    if (currentUser != null) {
      players.remove(currentUser);
      vm.put(CURRENT_USER_ATTR, currentUser);
      vm.put(PLAYER_LIST, players);
      vm.put(ACTIVE_GAME_LIST, activeGames);
      vm.put(ENDED_GAME_LIST, endedGames);
    }

    // display a user message in the Home page
    Message signInStatus = session.attribute(PostSignInRoute.SIGN_IN_STATUS);
    Message inGameStatus = session.attribute(GetGameRoute.GAME_STATUS_MSG);
    if (signInStatus != null) {
      vm.put(MESSAGE_ATTR, signInStatus);
      if (inGameStatus != null) {
        vm.remove(MESSAGE_ATTR);
        vm.put(MESSAGE_ATTR, inGameStatus);
        session.removeAttribute(GetGameRoute.GAME_STATUS_MSG);
      }
    } else {
      // displays the welcome message along with the number of active users.
      vm.put(MESSAGE_ATTR, Message.info(String.format(WELCOME_MSG, players.size())));
    }

    // check if the current user has been assigned a game.
    String challengerName = session.attribute("challenger");
    if (challengerName != null) {
      response.redirect(WebServer.GAME_URL);
      halt();
      return null;
    }

    // render the View
    return templateEngine.render(new ModelAndView(vm , "home.ftl"));
  }
}
