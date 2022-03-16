package org.lauchproject;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class configData {
    public static String fileName = "config.txt";
    public static String[] ruleLine = new String[2];
    private static String[] users;
    private static String time, bot_number, date, connection_time, startTime;
    private static JSONObject obj = new JSONObject();

    public static void main(String[] args) {
        File file = new File(fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("creo file");
        }
        else{
            try {
                Scanner sc = new Scanner(file);
                while(sc.hasNext()){
                    ruleLine = sc.next().split(":");
                    if (ruleLine[0].equals("users"))
                        users = ruleLine[1].split(",");
                    else if (ruleLine[0].equals("time"))
                        time = ruleLine[1];
                    else if (ruleLine[0].equals("connection_time"))
                        connection_time = ruleLine[1];
                    else if (ruleLine[0].equals("date"))
                        date = ruleLine[1];
                    else if (ruleLine[0].equals("bot_number"))
                        bot_number = ruleLine[1];
                    else if(ruleLine[0].equals("startTime"))
                        startTime = ruleLine[1];
                    obj = getJsonRules();
                }
                System.out.println("users" + users.toString());
                System.out.println("time" + time);
                System.out.println(connection_time + "connection_time");
                System.out.println("date" + date);
                System.out.println("bot-num" + bot_number);
                System.out.println("startTime" + startTime);

                new GameSettings(obj, getUsers());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public static ArrayList<String> getUsers() {
        return new ArrayList<>(Arrays.asList(users));
    }

    public static String getTime() {
        return time;
    }

    public static String getBot_number() {
        return bot_number;
    }

    public static String getDate() {
        return date;
    }

    public static String getConnection_time() {
        return connection_time;
    }

    public static String getStartTime() {
        return startTime.replace(".", ":");
    }

    public static JSONObject getJsonRules(){
        obj.clear();
        obj.put("time", time);
        obj.put("bot_number", bot_number);
        obj.put("connection_time", connection_time);
        obj.put("date", date + " " + startTime);
        return obj;
    }
}
