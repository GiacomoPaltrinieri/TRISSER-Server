package org.lauchproject;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * The user that decides to host a tournament can choose to set the rules in two different methods:
 * <ul>
 *     <li>CLI --> Modifying the configuration file (configData)</li>
 *     <li>GUI --> Using a web-based interface to set every rule and parameters needed to start the server (My_Servlet)</li>
 * </ul>
 * Independently of the used method the server needs the data inserted by the user.
 *
 * <strong>GUI_CLI_Run</strong> has the task to get the parameters from <strong>My_Servlet</strong> or <strong>configData</strong> depending on the selection made to start the server.
 * Those parameters will then be stored and used afterwards by the server.
 */
public class GUI_CLI_Run {
    /** <strong>players</strong> Contains every player that participate in the tournament **/
    private static ArrayList<String> players=new ArrayList<>();
    /** <strong>rules</strong> Contains a set of rules expressed in a JSON format **/
    private static JSONObject rules = new JSONObject();
    /** <strong>temp_gioco_bot</strong> contains the available time that will be given to every user to play **/
    private static String temp_gioco_bot;
    /** <strong>temp_connessione</strong> contains the available time that will be given to every user to connect to the broker and send an online message **/
    private static String temp_connessione;
    /** <strong>data_start_game</strong> contains the date at which the tournament will start **/
    private static String data_start_game;
    /** <strong>temp_start_game</strong> contains the time at which the tournament will start **/
    private static String temp_start_game;
    /** <strong>bot_instance</strong> contains the number of games a bot will have to play with every other bot **/
    private static String bot_instance;

    /**
     * Depending on the parameter value the constructor will save necessary data onto it's attributes
     *
     * @param GUIorCLI --> it's necessary to specify whether the user has chosen the GUI (GUIorCLI = "GUI" --> the selection has been done using the web-app)configuration or the CLI (GUIorCLI = "CLI" --> the selection has been done using the configuration file)configuration
     * @see GameSettings
     * @see My_servlet
     * @see configData
     */
    public GUI_CLI_Run(String GUIorCLI){
        if (GUIorCLI.equals("GUI")){
            bot_instance = My_servlet.getBot_instance();
            temp_gioco_bot = My_servlet.getTemp_gioco_bot();
            temp_connessione = My_servlet.getTemp_connessione();
            data_start_game = My_servlet.getData_start_game();
            temp_start_game = My_servlet.getTemp_start_game();
            rules = My_servlet.getRules();
            players = My_servlet.getPlayers();
        }else if (GUIorCLI.equals("CLI")){
            bot_instance = configData.getBot_instance();
            temp_gioco_bot = configData.getTemp_gioco_bot();
            temp_connessione = configData.getTemp_connessione();
            data_start_game = configData.getData_start_game();
            temp_start_game = configData.getTemp_start_game();
            rules = configData.getJsonRules();
            players = configData.getPlayers();
        }

        // Start GameSettings
        new GameSettings(rules, players);
    }

    /** ---------------------------- Getters ---------------------------- **/

    /**
     * Getter method used to return the list of players playing participating in the tournament.
     * @return Returns the list of different players in an ArrayList of Strings.
     */
    public static ArrayList<String> getPlayers() {
        return players;
    }
    /**
     * Getter method used to return the game time of a tournament.
     * @return Returns the amount of time in seconds as a String Object.
     */
    public static String getTemp_gioco_bot() {
        return temp_gioco_bot;
    }
    /**
     * Getter method used to return the connection time granted to the different players in a tournament.
     * @return Returns the amount of time in seconds as a String Object.
     */
    public static String getTemp_connessione() {
        return temp_connessione;
    }
    /**
     * Getter method used to return the date of the tournament.
     * @return Returns the date (yyyy-mm-dd) of the tournament as a String Object.
     */
    public static String getData_start_game() {
        return data_start_game;
    }
    /**
     * Getter method used to return the time of the tournament.
     * @return Returns the time (hh:mm:ss) of the tournament as a String Object.
     */
    public static String getTemp_start_game() {
        return temp_start_game;
    }
    /**
     * Getter method used to return the number of games that will be played between two different bots.
     * @return Returns the amount (a number) of games that two users have to play as a String Object.
     */
    public static String getBot_instance() {
        return bot_instance;
    }
}
