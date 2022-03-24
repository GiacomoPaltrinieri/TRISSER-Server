package org.lauchproject;

import org.json.simple.JSONObject;

import java.util.ArrayList;

public class GUI_CLI_Run {
    private static ArrayList<String> users=new ArrayList<>();
    private static JSONObject rules = new JSONObject();
    private static String temp_gioco_bot,temp_connessione,data_start_game,temp_start_game,bot_istance;

    public GUI_CLI_Run(String GUIorCLI){
        if (GUIorCLI.equals("GUI")){
            bot_istance = My_servlet.getBot_istance();
            temp_gioco_bot = My_servlet.getTemp_gioco_bot();
            temp_connessione = My_servlet.getTemp_connessione();
            data_start_game = My_servlet.getData_start_game();
            temp_start_game = My_servlet.getTemp_start_game();
            rules = My_servlet.getRules();
            users = My_servlet.getUsers();
        }else if (GUIorCLI.equals("CLI")){
            bot_istance = configData.getRoom_instance();
            temp_gioco_bot = configData.getTime();
            temp_connessione = configData.getConnection_time();
            data_start_game = configData.getDate();
            temp_start_game = configData.getStartTime();
            rules = configData.getJsonRules();
            users = configData.getUsers();
        }

        new GameSettings(rules, users);
    }

    public static ArrayList<String> getUsers() {
        return users;
    }

    public static JSONObject getRules() {
        return rules;
    }

    public static String getTemp_gioco_bot() {
        return temp_gioco_bot;
    }

    public static String getTemp_connessione() {
        return temp_connessione;
    }

    public static String getData_start_game() {
        return data_start_game;
    }

    public static String getTemp_start_game() {
        return temp_start_game;
    }

    public static String getBot_istance() {
        return bot_istance;
    }
}
