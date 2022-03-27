package org.lauchproject;

/**
 * This class is used to define a player and the points he managed to score after the tournament.
 *
 * @author Giacomino
 * @see MQTTPubPrint
 * **/
public class PlayerPoints{
    /** Contains the player name of a player participating in the tournament **/
    private String player;
    /** Contains the number of points the player has scored **/
    private int wins;

    /**
     * This constructor is used to instantiate a PlayerPoints Object before the tournament begins.
     * The starting win number will automatically be set to 0.
     *
     * @param player Takes the player name to uniquely identify every user.
     */
    public PlayerPoints(String player) {
        this.player = player;
        this.wins=0;
    }

    /**
     * This constructor is used to create a PlayerPoints Object with wins value >= 0.
     *
     * @param player Takes a String value containing playerName:NumberOfPointsScored.
     * @param result Takes a String value just to differentiate this constructor from the other.
     */
    public PlayerPoints(String player, String result) {
        String[] player_score;
        player_score = player.split(":");
        this.player = player_score[0];
        this.wins = Integer.parseInt(player_score[1]);
    }

    /**
     * This Setter is used to set a given number as the number of wins.
     *
     * @param wins Number of wins you want to set.
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     * Getter used to return the String value of the player that defines the PlayerPoints Object.
     * @return Returns the name of the player.
     */
    public String getPlayer() {
        return player;
    }
    /**
     * Getter used to return the Integer value of the points scored by the player.
     * @return Returns the name of the player.
     */
    public int getWins() {
        return wins;
    }
    /**
     * When called, this function adds a point win attribute.
     */
    public void addPoint() {
        wins++;
    }

    /**
     * This function returns the player and the points he scored
     * @return returns the playerName:wins --> TRISSER.server@gmail.com:150.
     */
    public String returnValue(){
        return player + ":" + wins;
    }
}
