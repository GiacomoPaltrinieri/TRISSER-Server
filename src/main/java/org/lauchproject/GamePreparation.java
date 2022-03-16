package org.lauchproject;

import java.io.*;
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
            System.out.println("enters here");
            timer.cancel();
        }
    }

    public static void main(String[] args) throws ParseException {

        System.out.println("Current Time: " + df.format( new Date()));

        //Date and time at which you want to execute
        Date date = df.parse(configData.getDate() + " " + configData.getStartTime());
        System.out.println("questo? " + date);

        timer.schedule(new MyTimeTask(), date);
    }

    public GamePreparation(){
        System.out.println("Current Time: " + df.format( new Date()));

        //Date and time at which you want to execute
        Date date = null;
        try {
            date = df.parse( configData.getDate() + " " + configData.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("questo qui? " + date);

        timer.schedule(new MyTimeTask(), date);
    }

}
