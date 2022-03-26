package org.lauchproject;

/**
 * This class is used to define a player and the points he managed to score after the game
 * **/

public class PlayerPoints{
    private String player;
    private int wins;

    public PlayerPoints(String player) {
        this.player = player;
        this.wins=0;
    }

    public PlayerPoints(String player, String result) {
        String[] player_score;
        player_score = player.split(":");
        this.player = player_score[0];
        this.wins = Integer.parseInt(player_score[1]);
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public String getPlayer() {
        return player;
    }

    public int getWins() {
        return wins;
    }

    public void addPoint() {
        wins++;
    }

    public String returnValue(){
        return player + ":" + wins;
    }
}
