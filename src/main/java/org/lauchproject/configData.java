package org.lauchproject;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * configData is the class used to <strong>read and interpret the configuration file</strong> used by the server to determine which settings the host chose.
 *
 * The configuration file must be named <strong>"config.txt"</strong>.
 * In the file there must be some fields that will then define the tournament settings:
 *
 * <ul>
 *     <li>users:mail_1,mail_2,mail_3....,mail_n  <-- The list of users that will participate to the event</li>
 *     <li>time:time_in_seconds <-- Game time that will be used in the game</li>
 *     <li>connection_time:time_in_seconds <-- Time given to the users to connect to connect to the broker and verify their connection</li>
 *     <li>date:yyyy-mm-gg <-- Tournament date</li>
 *     <li>startTimes:hh.mm.ss <-- Tournament start time</li>
 *     <li>bot_number:number_of_games <-- This field defines the number of games to be disputed</li>
 * </ul>
 *
 * <h4>Note that the different fields defined in the file can be written in any order the user wishes</h4>
 *
 * @author Giacomino
 * @see GUI_CLI_Run
 */
public class configData {
    /** <strong>fileName</strong> defines the file name of the configuration file used by the server **/
    public static String fileName = "config.txt";
    /** <strong>ruleLine</strong> is used to split the single line of the configuration file.
     * In the first element the field name will be written (time).
     * In the second element the field value will be written (20).
     * **/
    public static String[] ruleLine = new String[2];
    /** <strong>players</strong> contains every player that participate in the tournament **/
    private static String[] players;
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
    /** <strong>obj</strong> temporary Object used by the <strong>getJsonRules</strong> method to return the JSONObject Object**/
    private static JSONObject obj = new JSONObject();

    /**
     * Main method
     * One of the two options given to start the server.
     * By running the main method the game settings will be defined by the <strong>configuration file</strong>.
     *
     * After reading the config file the preparation phase will start (GUI_CLI_Run).
     *
     * @see GUI_CLI_Run
     * @param args
     */
    public static void main(String[] args) {
        File file = new File(chooseLocation());
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("creating file...");
        }
        else{
            try {
                Scanner sc = new Scanner(file);
                while(sc.hasNext()){
                    ruleLine = sc.next().split(":");
                    if (ruleLine[0].equals("users"))
                        players = ruleLine[1].split(",");
                    else if (ruleLine[0].equals("time"))
                        temp_gioco_bot = ruleLine[1];
                    else if (ruleLine[0].equals("connection_time"))
                        temp_connessione = ruleLine[1];
                    else if (ruleLine[0].equals("date"))
                        data_start_game = ruleLine[1];
                    else if (ruleLine[0].equals("bot_number"))
                        bot_instance = ruleLine[1];
                    else if(ruleLine[0].equals("startTime"))
                        temp_start_game = ruleLine[1];
                    obj = getJsonRules();
                }
                System.out.println("users" + players.toString());
                System.out.println("time" + temp_gioco_bot);
                System.out.println(temp_connessione + "connection_time");
                System.out.println("date" + data_start_game);
                System.out.println("startTime" + temp_start_game);

                new GUI_CLI_Run("CLI");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * The user has to write the configuration file path.
     *
     * The specified path can:
     * <ul>
     *     <li>include the filename (only if is called config.txt) --> C://users/giaco/config.txt.</li>
     *     <li>be equal to "/" --> The file location is the same as the project folder.</li>
     *     <li>end in with the "/" or without --> C://users/giaco == C://users/giaco/.</li>
     * </ul>
     *
     * @return returns the configuration file path used by the <strong>main method</strong> to determine the game rules.
     */
    private static String chooseLocation() {
        Scanner input = new Scanner(System.in);
        System.out.print("seleziona percorso file del file di configurazione (digita \"/\" per selezionare la directory attuale) : ");
        String path = input.nextLine();
        if (path.equals("/"))
            path=fileName;
        else if (path.contains(fileName))
            return path;
        else if (path.charAt(path.length()-1)=='/')
            path=path+fileName;
        else
            path=path+"/"+fileName;
        return path;
    }

    /** ---------------------------- Getters ---------------------------- **/

    /**
     * Getter method used to return the list of players participating in the tournament.
     * @return Returns the list of different players in an ArrayList of Strings.
     */
    public static ArrayList<String> getPlayers() {
        ArrayList<String> playersList = new ArrayList<>();
        Collections.addAll(playersList, players);
        return playersList;
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
        return temp_start_game.replace(".", ":");
    }
    /**
     * Getter method used to return the number of games that will be played between two different bots.
     * @return Returns the amount (a number) of games that two users have to play as a String Object.
     */
    public static String getBot_instance() {
        return bot_instance;
    }
    /**
     * Getter method used to return the <strong>set of rules</strong> as a Json.
     * @return Returns a set of rules necessary for the mail generation.
     */
    public static JSONObject getJsonRules(){
        obj.clear();
        obj.put("time", temp_gioco_bot);
        obj.put("bot_number", bot_instance);
        obj.put("connection_time", temp_connessione);
        obj.put("date", data_start_game + " " + temp_start_game);
        return obj;
    }
}
