package com.webcheckers.application;

import com.webcheckers.model.Color;
import com.webcheckers.model.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A place used to store Checkers Games to be accessed while they're being
 * played, or for access after they've ended.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class GameLibrary {
    /** A static integer to increment each time a game is started, this is a
     * unique identifier for each game.
     */
    private static int ID = 0;

    /** A static list to keep all active games stored in. */
    private static HashMap<Integer, GameCenter> activeGameList;

    /** A static list to keep all ended games stored in. */
    private static HashMap<Integer, GameCenter> endedGameList;

    public GameLibrary(HashMap<Integer, GameCenter> activeGameMap,
                       HashMap<Integer, GameCenter> endedGameMap) {
        activeGameList = activeGameMap;
        endedGameList = endedGameMap;
    }

    /**
     * Creates a new game for two players to participate in, and assigns it a
     * unique gameID.
     *
     * @param playerOne: The player to be set to RED.
     * @param playerTwo: The player to be set to WHITE.
     * @return a new GameCenter, for both players to use to carry out a game
     * of checkers.
     */
    public static GameCenter createGame(Player playerOne, Player playerTwo) {
        GameCenter newGame = new GameCenter(ID, playerOne, playerTwo);
        playerOne.setColor(Color.RED);
        playerTwo.setColor(Color.WHITE);
        activeGameList.put(ID, newGame);
        ID++;
        return newGame;
    }

    /**
     * A lookup method for games by using their unique ID.
     *
     * @param ID: The unique gameID for the game being searched for.
     * @return the GameCenter with the uniqueID provided.
     */
    public static GameCenter getGameByID(int ID) {
        if (activeGameList.containsKey(ID)) {
            return activeGameList.get(ID);
        } else {
            return endedGameList.get(ID);
        }
    }

    /**
     * Used to move a game that has just ended from the list of active games,
     * to the list of games that have ended.
     *
     * @param ID: The gameID of this Game.
     */
    public static void gameHasEnded(int ID) {
        if (activeGameList.containsKey(ID)) {
            GameCenter endedGame = activeGameList.get(ID);
            activeGameList.remove(ID);
            endedGameList.put(ID, endedGame);
        }
    }

    /**
     * Accessor for a list of all games in progress.
     *
     * @return An ArrayList containing all active games.
     */
    public static ArrayList<GameCenter> getActiveGameList() {
        return new ArrayList<>(activeGameList.values());
    }

    /**
     * Accessor for a list of all games that have ended.
     *
     * @return An ArrayList containing all games that have already ended.
     */
    public static ArrayList<GameCenter> getEndedGameList() {
        return new ArrayList<>(endedGameList.values());
    }
}
