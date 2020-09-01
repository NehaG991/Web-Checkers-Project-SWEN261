package com.webcheckers.application;

import com.webcheckers.model.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * The lobby where players will be placed when they sign-in.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class PlayerLobby {

    /** A list to keep track of signed in players, only allowing new players in
     * if their userName is unique.
     */
    private Map<String, Player> playerList;

    /**
     * Instantiates a new PlayerLobby.
     *
     * @param playerList: a Map containing key, value pairs of player names
     *                  along with their respective {@link Player} objects.
     */
    public PlayerLobby(Map<String, Player> playerList) {
        this.playerList = playerList;
    }

    /**
     * Checks if the username provided is already in the list of active
     * players. If the username is already in use, this function will return
     * true. If not, this function will return false.
     * @param userName: The username to be checked for availability.
     * @return true if the username is already being used, false otherwise.
     */
    public boolean userNameInUse(String userName) {
        return playerList.containsKey(userName);
    }

    /**
     * Add a new {@link Player} to the list of active players.
     * @param userName: The username of the new {@link Player}.
     * @param player: The new {@link Player} being added to the list of active
     *              players.
     */
    public void signIn(String userName, Player player) {
        playerList.put(userName, player);
    }

    /**
     * Accessor for a list of all active players, given in an arrayList.
     *
     * @return an ArrayList containing all active {@link Player}s.
     */
    public ArrayList<Player> getPlayerList() {
        return new ArrayList<>(playerList.values());
    }

    /**
     * Get a specific {@link Player} object based on their username.
     *
     * @param name: The username of the Player being searched for.
     * @return the Player associated with the provided username.
     */
    public Player getPlayerByName(String name) {
        return playerList.get(name);
    }

    /**
     * Remove a specific {@link Player} object based on their username.
     *
     * @param userName: The username of the current {@link Player}
     * @param player: The {@link Player} object being removed
     * @author Neha Ghanta, ng8975@rit.edu
     */
    public void signOut(String userName, Player player){
        playerList.remove(userName, player);
    }
}
