package org.lauchproject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Scanner;

/**
 * GameSettings is used to:
 * <ul>
 *     <li>Set password for every player</li>
 *     <li>Set ACL'S to manage access to the broker</li>
 *     <li>Set topics in which the games are going to be played</li>
 *     <li>Generate and send mails containing necessary informations to connect to the server and play their games</li>
 *     <li>Start the broker</li>
 *     <li>Stop the broker</li>
 * </ul>
 *
 * @author Giacomino
 * @see GUI_CLI_Run
 * @see GamePreparation
 */
public class GameSettings {
    /** Contains every topic that will be used to manage all the games. es: Mail1_Mail2 **/
    private static ArrayList<String> topics = new ArrayList<>();
    /** Contains the username that the server will use to connect to the broker **/
    private static final String admin = "admin";
    /** Contains the password that the server will use to connect to the broker **/
    private static final String adminPWD = "Password";

    /**
     * This function executes a command or a set of commands in a CMD terminal.
     *
     * @param command Takes in input the command/commands to execute in a CMD terminal
     * @return Returns the output of the executed commands.
     */
    public static String executeCommand(String command) {
        String line;
        StringBuilder result = new StringBuilder();
        try {
            ProcessBuilder builder;

            builder = new ProcessBuilder("cmd.exe", "/c", command);

            builder.redirectErrorStream(true);
            Process p = builder.start();
            p.waitFor();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Exception = " + e.getMessage());
            System.out.println("an error occurred while reading the lines (r.readline)");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("an error occurred while starting the builder(builder.start)");
            //waitFor error
        }
        return result.toString();
    }

    /**
     * This function generates a random password that will be used for every player.
     *
     * @param len Takes in input the length of the password that will be generated.
     * @return Returns the password in a String format.
     */
    public static String generateRandomPassword(int len) {
        // ASCII range – alphanumeric (0-9, a-z, A-Z)
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // each iteration of the loop randomly chooses a character from the given
        // ASCII range and appends it to the `StringBuilder` instance

        for (int i = 0; i < len; i++)
        {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    /**
     * This function sets the generated passwords to every user.
     * It also writes in a configuration file the list of users and passwords (hashed).
     *
     * @param players Takes in input the list of players that will participate in a tournament
     * @return Returns the list of passwords that have been generated
     */
    private static ArrayList<String> setPassword(ArrayList<String> players) {
        ArrayList<String> players_pwd = new ArrayList<>();
        ArrayList<String> passwords = new ArrayList<>();
        String file_name = "pwfile.txt";
        String separator = System.getProperty("file.separator");
        String path = "C:" + separator + "Program Files" + separator + "mosquitto" + separator + file_name;
        for (String player : players) {
            String pwd = generateRandomPassword(8);

            players_pwd.add(player + ":" + pwd);
            passwords.add(pwd);
        }
        players_pwd.add(admin + ":" + adminPWD);
        writeToFile(path,players_pwd, false);
        executeCommand("cd " + path.replace(file_name, "") + " && mosquitto_passwd -U pwfile.txt"); // hashes the password file
        return passwords;
    }

    /**
     * This function writes a list of lines in a given file.
     *
     * @param path Takes in input the path where the file will be generated/overwritten.
     * @param lines Takes an ArrayList containing the lines that will be written in the file. (line = ArrayList element).
     * @param append Takes a boolean parameter. If append = True -> the lines (ArrayList lines) will be added to the file (path). If the append parameter = False -> The file, if present, will be overwritten.
     */
    public static void writeToFile(String path, ArrayList<String> lines, boolean append) {
        File file = new File(path); // Creates File object with the specified path. The path must include the filename
        if (!file.exists()) {
            try {
                System.out.println(file.createNewFile());// generates a new file, in case it's not present
                System.out.println("creating file");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("something went wrong while creating the file");
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile(), append);
        } catch (IOException e) {
            System.out.println("something went wrong while starting the FileWriter");
        }

        ListIterator<String> line = lines.listIterator();
        while (line.hasNext()) {
            try {
                Objects.requireNonNull(fw).write(line.next() + "\n"); // writes single line
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("something went wrong while writing on the file");
            }
        }
        try {
            Objects.requireNonNull(fw).flush();
            fw.close(); // closes the FileWriter
        } catch (IOException e) {
            System.out.println("something went wrong while closing the file");
        }
    }

    /**
     * This function generates the different topics that will be used to define the list of games that will be played.
     *
     * @param players Takes in input the list of players that participate in the tournament
     * @return returns the generated topics -> Mail1_Mail2.
     */
    private static ArrayList<String> setACLs(ArrayList<String> players) {
        boolean existing_topic = false;
        ArrayList<String> topics = new ArrayList<>();
        for (int i = 0; i < players.size() - 1; i++)
        {
            for (String player : players) {
                if (!players.get(i).equals(player)) // the 2 players can't be equal
                {
                    if (topics.size() != 0)
                    {
                        for (String topic : topics) {
                            if (topic.contains(players.get(i)) && topic.contains(player)) {
                                existing_topic = true;
                                break;
                            }
                        }
                        if (!existing_topic)
                            topics.add(players.get(i) + "_" + player);
                        existing_topic = false;
                    } else
                        topics.add(players.get(i) + "_" + player);
                }
            }
        }
        //for (String topic : topics) System.out.println(topic);
        return topics;
    }

    /**
     * This function generate a mail that will be then sent to every player.
     * The mail will contain:
     * <ul>
     *     <li>Username</li>
     *     <li>Password</li>
     *     <li>Rules:
     *     <ul>
     *         <li>Connection time</li>
     *         <li>Game time</li>
     *         <li>Connection date</li>
     *         <li>Instance number</li>
     *     </ul>
     *     </li>
     *     <li>Accessed topics</li>
     * </ul>
     *
     * @param players Takes in input the list of players that will participate in the tournament.
     * @param topics Takes in input the list of topics that will define.
     * @param passwords Takes in input the list of password that will be sent to every bot to access the broker.
     * @param rules Takes in input the set of rules that the players will need to connect to every game.
     */
    private static void generateMailContent(ArrayList<String> players, ArrayList<String> topics, ArrayList<String> passwords, JSONObject rules) {
        JSONArray roomList;

        JSONObject singleMail = new JSONObject();
        for (int i = 0; i < players.size(); i++){
            singleMail.put("user", players.get(i));
            singleMail.put("pwd", passwords.get(i));
            singleMail.put("rules", rules);
            roomList = getTopicAccess(topics, players.get(i));
            singleMail.put("rooms", roomList);
            singleMail.put("room_instance", Integer.parseInt(GUI_CLI_Run.getBot_instance()));

            SendMail.send(players.get(i), "GAME", singleMail.toString().replace("\\",""));
            singleMail.clear();
        }
    }

    /**
     * This function writes on a mosquitto configuration file all the ACL that will allow a player to join and publish messages only on certain topics.
     *
     * @param players Takes the list of players in the tournament.
     * @param topics Takes all the topics generated for the tournament.
     * @param subRoomList Takes the number of games that compose a single room(topic).
     */
    private static void writeACLS(ArrayList<String> players, ArrayList<String> topics, int subRoomList) {
        JSONArray accessedTopics;
        String separator = System.getProperty("file.separator");
        String path = "C:" + separator + "Program Files" + separator + "mosquitto" + separator + "aclfile.txt";
        ArrayList<String> toWrite = new ArrayList<>();

        for (String player:players){ // for every player
            toWrite.add("user " + player);
            accessedTopics = getTopicAccess(topics,player);
            for (Object accessedTopic : accessedTopics) {
                for (int j = 0; j < subRoomList; j++) {
                    toWrite.add("topic readwrite " + accessedTopic + "/" + j + "/" + player);
                    toWrite.add("topic read " + accessedTopic + "/" + j + "/#");
                }
            }
        }
        toWrite.add("user " + admin);
        toWrite.add("topic readwrite #");

        writeToFile(path, toWrite, false);
    }

    /**
     * Getter used to return the name of every topic that will be used to play the tournament.
     * @return Returns the list of topics.
     */
    public static ArrayList<String> getTopics(){
        return topics;
    }

    /**
     * This function is used to get all the accessed topic of a single user.
     *
     * @param topics Takes all the topics generated for the tournament.
     * @param player Takes the name of the player whose topics you want to view.
     * @return Returns the list of topics the player has access to.
     */
    private static JSONArray getTopicAccess(ArrayList<String> topics, String player) {
        JSONArray permittedTopics = new JSONArray();
        for (String topic : topics)
            if (topic.contains(player))
                permittedTopics.add(topic);
        return permittedTopics;
    }

    /**
     * The constructor gets called once the tournament settings are defined.
     * The tasks of the constructor are:
     * <ul>
     *     <li>To generate the list of topics that will identify every single game.</li>
     *     <li>To create credentials to enable the players to access to the broker.</li>
     *     <li>To send an email with the tournament settings to every client involved.</li>
     * </ul>
     *
     * @param rules Set of rules in a JSON format.
     * @param players List of player part of the tournament.
     */
    public GameSettings(JSONObject rules, ArrayList<String> players) {
        topics = setACLs(players);
        ArrayList<String> passwords = setPassword(players);
        writeACLS(players, topics, Integer.parseInt(GUI_CLI_Run.getBot_instance()));
        generateMailContent(players, topics, passwords, rules);
        new GamePreparation(GUI_CLI_Run.getData_start_game(), GUI_CLI_Run.getTemp_start_game());
    }
    /** This function starts the MQTT broker **/
    public static void startBroker(){
    System.out.println(executeCommand("cd C:\\Program Files\\mosquitto\\ && Net start Mosquitto")); // Starts the mosquitto broker
}
    /** This function stops the MQTT broker **/
    public static void stopBroker(){
        System.out.println(executeCommand("Taskkill /IM \"mosquitto.exe\" /F")); // Closes the mosquitto broker
    }
}