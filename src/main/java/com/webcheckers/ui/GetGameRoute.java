package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameCenter;
import com.webcheckers.application.GameLibrary;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.BoardView;
import com.webcheckers.model.Player;
import com.webcheckers.model.ViewMode;
import com.webcheckers.util.Message;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.application.GameLibrary.gameHasEnded;
import static spark.Spark.halt;

/**
 * The UI Controller to GET the Game page.
 *
 * @author Neha Ghanta, ng8975@rit.edu
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class GetGameRoute implements Route{
    private final TemplateEngine templateEngine;
    private PlayerLobby playerLobby;
    private Gson gson;

    static final String OPPONENT_NAME = "opponent";
    static final String CHALLENGER_ATTR = "challenger";
    static final String GAME_CENTER_ATTR = "gameCenter";
    static final String GAME_ID_ATTR = "gameID";
    static final String ACTIVE_COLOR = "activeColor";
    static final String RED_PLAYER = "redPlayer";
    static final String WHITE_PLAYER = "whitePlayer";
    static final String GAME_STATUS_MSG = "Game Status";
    static final String PLAYER_IN_GAME = "%s is already in a Checkers game.";
    static final String CAPTURED_ALL_PIECES = "%s has captured all of the pieces";
    static final String PIECES_BLOCKED = "%s's pieces are all blocked";

    /**
     * Constructor for the {@code GET /game} route handler.
     * @param templateEngine: The template engine used for rendering an HTML page.
     */
    public GetGameRoute(PlayerLobby playerLobby, final TemplateEngine templateEngine, Gson gson){
        this.templateEngine = templateEngine;
        this.playerLobby = playerLobby;
        this.gson = gson;
    }

    /**
     * If a {@link Player} clicks another Player's name in the list on the Home
     * page, this method will create a new Checkers game, and attach some info
     * about that game to their opponent's session. If a user has been invited
     * to a game, and they have some information about that game attached to
     * their session, this method will process that information and put the
     * user into that already created game.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     *
     * @return
     *   the rendered HTML for the Game page
     */
    @Override
    public Object handle(Request request, Response response){
        final Session session = request.session();
        Map<String, Object> vm;

        if (session.attribute(CHALLENGER_ATTR) != null) {
            vm = iHaveBeenChallenged(session);
        } else {
            // gets the opponent name that was clicked
            String opponentName = request.queryParams(OPPONENT_NAME);
            if (opponentName != null) {
                vm = iChallengeAnotherPlayer(request, response, session);
            } else {
                vm = loadGamePage(request, session);
            }
        }
        return templateEngine.render(new ModelAndView(vm, "game.ftl"));
    }

    /**
     * Build a view-model based on the fact that this current Player has been
     * challenged by another to a game of checkers.
     *
     * @param session: This Player's HTTP session.
     * @return a Map containing objects to be rendered in the Game View.
     */
    public Map<String, Object> iHaveBeenChallenged(Session session) {
        Map<String, Object> vm = new HashMap<>();

        Player currentUser = session.attribute(GetHomeRoute.CURRENT_USER_ATTR);
        // get the gameID from the session, retrieve the game board from the GameCenter,
        // and flip the board to have white at bottom.
        final int gameID = session.attribute(GAME_ID_ATTR);
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);

        Player opponent = gameCenter.getRedPlayer();
        session.attribute(OPPONENT_NAME, opponent);

        // build the view based on the information provided by the challenger.
        vm.put(GetHomeRoute.TITLE_ATTR, "Game Page");
        vm.put("gameID", gameID);
        vm.put(GetHomeRoute.CURRENT_USER_ATTR, currentUser);
        vm.put("viewMode", ViewMode.PLAY);
        vm.put(RED_PLAYER, gameCenter.getRedPlayer());
        vm.put(WHITE_PLAYER, gameCenter.getWhitePlayer());
        vm.put(ACTIVE_COLOR, gameCenter.getCurrentTurn());
        vm.put("board", gameCenter.getRotatedBoard());

        session.removeAttribute(CHALLENGER_ATTR);

        return vm;
    }

    /**
     * Build a view-model based on the fact that this Player has challenged
     * another player to a game of checkers.
     *
     * @param request: The HTTP request
     * @param response: The HTTP response
     * @param session: This Player's HTTP session
     * @return a Map containing objects to be rendered in the Game View.
     */
    public Map<String, Object> iChallengeAnotherPlayer(Request request, Response response, Session session) {
        Map<String, Object> vm = new HashMap<>();

        Player currentUser = session.attribute(GetHomeRoute.CURRENT_USER_ATTR);
        // gets the opponent name that was clicked
        final String opponentName = request.queryParams(OPPONENT_NAME);
        Player opponent = playerLobby.getPlayerByName(opponentName);

        // check if opponent is already in a game
        if (opponent.getInGameStatus()) {
            session.attribute(GAME_STATUS_MSG, Message.error(String.format(PLAYER_IN_GAME, opponentName)));
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }

        // setup a new GameCenter for this match, give the GameCenter both Player objects,
        // and retrieve the game board.
        GameCenter gameCenter = GameLibrary.createGame(currentUser, opponent);
        //GameCenter gameCenter = new GameCenter(currentUser, opponent);
        BoardView checkersBoard = gameCenter.getCheckersBoard();

        // save the opponent's player object to this session to access later
        session.attribute(OPPONENT_NAME, opponent);
        // Get opponent's session to give some game data to
        Session opponentSession = opponent.getSession();

        // give opponent's session the currentUser's name as their challenger
        opponentSession.attribute(CHALLENGER_ATTR, currentUser.getName());
        // give opponent's session this specific gameCenter gameID.
        opponentSession.attribute(GAME_ID_ATTR, gameCenter.getGameID());
        // give the currentUser's session this specific gameID.
        session.attribute(GAME_ID_ATTR, gameCenter.getGameID());

        // set the in-game status of both players
        currentUser.setInGame(true);
        opponent.setInGame(true);

        // build the view-model
        vm.put(GetHomeRoute.TITLE_ATTR, "Game Page");
        vm.put("gameID", gameCenter.getGameID());
        vm.put(GetHomeRoute.CURRENT_USER_ATTR, currentUser);
        vm.put(RED_PLAYER, gameCenter.getRedPlayer());
        vm.put(WHITE_PLAYER, gameCenter.getWhitePlayer());
        vm.put("viewMode", ViewMode.PLAY);
        vm.put(ACTIVE_COLOR, gameCenter.getCurrentTurn());
        vm.put("board", checkersBoard);

        return vm;
    }

    /**
     * Build a view-model to refresh the Game page with.
     *
     * @param session: This Player's HTTP session.
     * @return a Map containing objects to be rendered in the Game View.
     */
    public Map<String, Object> loadGamePage(Request request, Session session) {
        Map<String, Object> vm = new HashMap<>();

        Player currentUser = session.attribute(GetHomeRoute.CURRENT_USER_ATTR);

        final int gameID = Integer.parseInt(request.queryParams(GAME_ID_ATTR));
        GameCenter gameCenter = GameLibrary.getGameByID(gameID);
        BoardView checkersBoard = gameCenter.getCheckersBoard();

        // if there is a resignation message on the session, put the details
        // into the view-model.
        if (gameCenter.getResignStatus() != null) {
            final Map<String, Object> modeOptions = new HashMap<>(2);
            modeOptions.put("isGameOver", true);
            modeOptions.put("gameOverMessage", gameCenter.getResignStatus());
            vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
            gameHasEnded(gameID);
        }

        // checks if one of the players has captured all the pieces, putting the details
        // into the view-model
        if (gameCenter.capturedAllPieces()){
            final Map<String, Object> modeOptions = new HashMap<>(2);
            modeOptions.put("isGameOver", true);
            if (gameCenter.getRedPlayer().getCaptures() == 12){
                modeOptions.put("gameOverMessage", String.format(CAPTURED_ALL_PIECES,
                        gameCenter.getRedPlayer().getName()));
            }
            else {
                modeOptions.put("gameOverMessage", String.format(CAPTURED_ALL_PIECES,
                        gameCenter.getWhitePlayer().getName()));
            }
            vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
            currentUser.setInGame(false);
            gameHasEnded(gameID);
        }
        else {
            // checking if the red player's pieces are blocked, putting the details
            // into the view-model
            if (gameCenter.blockedPiecesRed()){
                final Map<String, Object> modeOptions = new HashMap<>(2);
                modeOptions.put("isGameOver", true);
                modeOptions.put("gameOverMessage", String.format(PIECES_BLOCKED, gameCenter.getRedPlayer().getName()));
                vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
                currentUser.setInGame(false);
                gameHasEnded(gameID);
            }

            // checking if the white player's pieces are blocked
            // into the view-model
            else if (gameCenter.blockedPiecesWhite()){
                final Map<String, Object> modeOptions = new HashMap<>(2);
                modeOptions.put("isGameOver", true);
                modeOptions.put("gameOverMessage", String.format(PIECES_BLOCKED, gameCenter.getWhitePlayer().getName()));
                vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
                currentUser.setInGame(false);
                gameHasEnded(gameID);
            }
        }

        // build the view-model
        vm.put(GetHomeRoute.TITLE_ATTR, "Game Page");
        vm.put(GAME_ID_ATTR, gameID);
        vm.put(GetHomeRoute.CURRENT_USER_ATTR, currentUser);
        vm.put(RED_PLAYER, gameCenter.getRedPlayer());
        vm.put(WHITE_PLAYER, gameCenter.getWhitePlayer());
        vm.put("viewMode", ViewMode.PLAY);
        vm.put(ACTIVE_COLOR, gameCenter.getCurrentTurn());
        if (currentUser.equals(gameCenter.getRedPlayer())) {
            vm.put("board", checkersBoard);
        } else {
            vm.put("board", gameCenter.getRotatedBoard());
        }

        return vm;
    }
}
