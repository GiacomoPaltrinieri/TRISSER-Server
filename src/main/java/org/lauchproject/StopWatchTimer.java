package org.lauchproject;

/**
 * This class defines a timer for every player.
 * A timer can be set, modified, started and stopped.
 * Once time is over the player will automatically lose the game.
 *
 * @author Giacomino
 * @see SingleRoom
 */

public class StopWatchTimer {
    /** <strong>player</strong> contains the player name **/
    String player;
    private long begin, end, time;
    public static final long SECONDS=1000;
    public static final long MINUTES=60000;
    public static final long MILLISECONDS=1;
/** CONSTRUCTOR
 *
 * The constructor takes in input the player name and the total time that will be granted to that user.
 *
 * @param player --> Contains the player name (bot mail).
 * @param time --> Contains the time that the bot will have to play its games.
 * **/
    public StopWatchTimer(int time, String player){
        this.time = time * 1000L;
        this.player = player;
    }

    /***
     * This function start the timer.
     */
    public void start(){
        begin = System.currentTimeMillis();
    }
    /***
     * This function stops the timer.
     * @return if time left is less than 0 the function will return false, else way it will return true.
     */
    public boolean stop(){
        end = System.currentTimeMillis();
        time = time - (end-begin);
        return time >= 0;// return true if there is still some time left
    }

    /** ---------------------------- Getters ---------------------------- **/

    /**
     * This function returns the player name of a specific StopWatchTimer Object
     * @return player name in a String format
     * **/
    public String getPlayer() {
        return player;
    }
    /**
     * This function returns the player name of a specific StopWatchTimer Object
     * @param measureUnit --> Time left can be returned in different formats (SECONDS, MINUTES, MILLISECONDS).
     * @return time left on the clock.
     * **/
    public int getTime(long measureUnit) {
        return (int) (time/measureUnit);
    }

}
