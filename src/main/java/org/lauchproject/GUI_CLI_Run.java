package org.lauchproject;

import org.json.simple.JSONObject;

import java.util.ArrayList;

public class GUI_CLI_Run {
    private static ArrayList<String> players=new ArrayList<>();
    private static JSONObject rules = new JSONObject();
    private static String temp_gioco_bot,temp_connessione,data_start_game,temp_start_game,bot_istance;

    public GUI_CLI_Run(String GUIorCLI){
        if (GUIorCLI.equals("GUI")){
            bot_istance = My_servlet.getBot_instance();
            temp_gioco_bot = My_servlet.getTemp_gioco_bot();
            temp_connessione = My_servlet.getTemp_connessione();
            data_start_game = My_servlet.getData_start_game();
            temp_start_game = My_servlet.getTemp_start_game();
            rules = My_servlet.getRules();
            players = My_servlet.getPlayers();
        }else if (GUIorCLI.equals("CLI")){
            bot_istance = configData.getBot_instance();
            temp_gioco_bot = configData.getTemp_gioco_bot();
            temp_connessione = configData.getTemp_connessione();
            data_start_game = configData.getData_start_game();
            temp_start_game = configData.getTemp_start_game();
            rules = configData.getJsonRules();
            players = configData.getPlayers();
        }

        new GameSettings(rules, players);
    }

    public static ArrayList<String> getPlayers() {
        return players;
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

    public static String getBot_instance() {
        return bot_istance;
    }
}
