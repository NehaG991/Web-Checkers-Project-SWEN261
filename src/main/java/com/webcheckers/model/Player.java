package com.webcheckers.model;

import spark.Session;

/**
 * A class to represent a single Player.
 *
 * @author Andrew Frank, ajf8248@rit.edu
 */
public class Player {

    private final String name;
    private final Session session;
    private Color color;
    private boolean inGame;
    private int captures;

    /**
     * Instantiates a new player that has a user name.
     * @param name: The user name for this player.
     */
    public Player(String name, Session session) {
        this.name = name;
        this.session = session;
        this.color = Color.RED;
        this.inGame = false;
        this.captures = 0;
    }

    /**
     * Accessor for this {@link Player} color.
     * @return this {@link Player} color.
     * @author Neha Ghanta, ng8975@rit.edu
     */
    public Color getColor(){
        return this.color;
    }

    /**
     * Set this player's piece color when they enter a new game.
     *
     * @param color: The color of this player's pieces for this game.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Accessor for this {@link Player} username.
     * @return this {@link Player} username.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets this {@link Player}'s session.
     *
     * @return this specific Player's HTTP session.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * This method is used to set a Player's in-game status. If inGame is set
     * to true, that means this Player is in a game. If false, this Player is
     * not currently in a game.
     *
     * @param inGame: a boolean used to set the in-game status of this Player.
     */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    /**
     * Accessor for the in-game status of this Player.
     *
     * @return the game status of this Player. True if this Player is in a
     * game, false otherwise.
     */
    public boolean getInGameStatus() {
        return this.inGame;
    }

    /**
     * Accessor for the number of Piece captures this Player has done.
     *
     * @return the number of Piece captures this Player has during this game.
     */
    public int getCaptures() {
        return this.captures;
    }

    /**
     * Increment the capture attribute, called when this Player captures
     * another Player's piece.
     */
    public void capture() {
        captures++;
    }

    /**
     * Decrement the capture attribute, called when this Player reverts a move
     * containing a capture.
     */
    public void revertCapture() {
        captures--;
    }

    /**
     * Used to reset the number of captures this player has made, after a game
     * they participated in has ended.
     */
    public void resetCapturesAfterGameEnd() {
        captures = 0;
    }

    /**
     * Two {@link Player} are equal if their username are equal.
     * @param obj: Any object
     * @return true if two {@link Player} have the same username,
     *   false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player otherPlayer = (Player) obj;
        return otherPlayer.getName().equals(this.name);
    }
}
