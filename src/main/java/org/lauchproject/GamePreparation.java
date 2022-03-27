package org.lauchproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used to wait a specific time and date to run an MqttPubPrint Object.
 *
 * @author Giacomino
 * @see MQTTPubPrint
 * @see GameSettings
 */
public class GamePreparation {
    /** Formatter, defines the format of the time and date that has to be passed as a parameter. **/
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static Timer timer = new Timer();

    /**
     * Starts the game when the date and time set in the constructor coincide with the current date and time
     */
    private static class MyTimeTask extends TimerTask {
        public void run() {
            new MQTTPubPrint(); // start game
            timer.cancel();
        }
    }

    /**
     * The Constructor is used to start the game at a specific time and a specific date.
     *
     * @param data Date at which the game has to start
     * @param time Time at which the game has to start
     */
    public GamePreparation(String data, String time){
        System.out.println("Current Time: " + df.format( new Date()));

        //Date and time at which you want to execute
        Date date = null;
        try {
            date = df.parse( data + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("date and time of the event: " + date);

        timer.schedule(new MyTimeTask(), date);
    }

}
