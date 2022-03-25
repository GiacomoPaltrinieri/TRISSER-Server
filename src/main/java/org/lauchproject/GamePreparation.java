package org.lauchproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GamePreparation {
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static Timer timer = new Timer();

    private static class MyTimeTask extends TimerTask {
        public void run() {
            new MQTTPubPrint(); // start game
            timer.cancel();
        }
    }

    public GamePreparation(){
        System.out.println("Current Time: " + df.format( new Date()));

        //Date and time at which you want to execute
        Date date = null;
        try {
            date = df.parse( GUI_CLI_Run.getData_start_game() + " " + GUI_CLI_Run.getTemp_start_game());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("date and time of the event: " + date);

        timer.schedule(new MyTimeTask(), date);
    }

}
