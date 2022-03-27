package org.lauchproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The GamePreparation Class is a class specifically made to execute a certain snippet (in this case MqttPubPrint Class) at a certain time and a certain date.
 * @author Giacomino
 * @see MQTTPubPrint
 */
public class GamePreparation {
    /** <strong>df</strong> is used to define the format that has to be respected when calling the constructor.**/
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /** <strong>timer</strong> Timer that expires and starts the game once time has come.**/
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
