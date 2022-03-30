package org.lauchproject;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static org.lauchproject.gameInstance.*;

/**
 * This Class has the task to manage all the games, all the players and everything related to the game.
 *
 * @see GamePreparation
 * @see gameInstance
 * @author Giacomino
 */
public class MQTTPubPrint {
    /** JSON Object that contains the list of players participating in the tournament and a boolean value that defines whether a player is connected or not. **/
    public JSONObject onlinePlayers = new JSONObject();
    /** qos defines the quality of service that will be used to send messages to the broker. **/
    public static final int qos = 0;
    /** Contains every topic that will be used to manage all the games. es: Mail1_Mail2 **/
    private final ArrayList<String> topics = GameSettings.getTopics();
    /** rooms contains the list of rooms -> Mail1_Mail2.
     * @see gameInstance **/
    private static ArrayList<gameInstance> rooms = new ArrayList<>();
    /** Contains the mqttClient that will be used to send and receive MQTT messages from the broker.
     * @see MqttClient **/
    private static MqttClient mqttClient;
    /** Contains the list of the players and the points of the tournament. **/
    private ArrayList<PlayerPoints> playerWins = new ArrayList<>();
    /** Contains the previous score of the players. **/
    private ArrayList<PlayerPoints> oldResults = new ArrayList<>();

    /**
     * Constructor.
     * Tasks:
     * <ul>
     *     <li>Set all the player to offline.</li>
     *     <li>Initialize rooms.</li>
     *     <li>Start the broker</li>
     *     <li>Connect to the broker.</li>
     *     <li>Enable connection of the players.</li>
     * </ul>
     */
    public MQTTPubPrint() {
        GameSettings.startBroker();
        for (String s : GUI_CLI_Run.getPlayers()) {
            onlinePlayers.put(s, false);
            playerWins.add(new PlayerPoints(s));
        }// list of players

        String time = GUI_CLI_Run.getTemp_gioco_bot();
        int room_instance = Integer.parseInt(GUI_CLI_Run.getBot_instance());

        for (String topic : topics) rooms.add(new gameInstance(topic, room_instance, time));
        connectToBroker();
        startConnectionPhase();
    }

    /**
     * This function is used to enable the Player connection to the server.
     * For a defined amount of time (GUI_CLI_Run.connection_time) the server will subscribe to the online topic.
     * After the connection time is over the Server will unsubscribe to the online topic and further connections are rejected.
     */
    private void startConnectionPhase() {
        try {
            mqttClient.subscribe("online/#"); //Listen to online topics
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Connected");

        int connection_time;
        connection_time = Integer.parseInt(GUI_CLI_Run.getTemp_connessione());
        connection_time = connection_time*1000; // conversion in seconds

        startMethodAfterNMilliseconds(this::startGamePreparationPhase, connection_time); // connection time is over
    }

    /**
     *
     * After the startConnectionPhase is over the StartGamePreparationPhase will:
     * <ul>
     *     <li>Check for <strong>not connected</strong> players</li>
     *     <li>make the mqttClient subscribe to every topic and start listening to incoming moves.</li>
     * </ul>
     */
    private void startGamePreparationPhase() {
        try {
            mqttClient.subscribe("#"); // now moves can be sent
            mqttClient.unsubscribe("online/#");
            checkForNotConnected(onlinePlayers);
            startGamePhase();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * The game will now start.
     * To start the game the server will send an mqtt message telling all the players that they can start sending their moves.
     * After a determined amount of time (GUI_CLI_Run.temp_gioco_bot) the server will stop accepting further moves.
     */
    private void startGamePhase() {
        sendMessage("broadcast", "{\"game\":\"start\"}");
        int time = Integer.parseInt(GUI_CLI_Run.getTemp_gioco_bot());
        time = time*1000;
        startMethodAfterNMilliseconds(this::gameOver, time);
    }
    /**
     * This function sets all the callbacks used by the mqttClient.
     * All the message received will be analyzed in this function and functions will be called depending on the messages received.
     * **/
    private void setClientCallBacks() {
        mqttClient.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {}
            //this function gets called every time a message arrives.
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = message.toString();
                System.out.println("entra in message arrived.");
                if (IsJson.isJSONValid(msg)){
                    System.out.println("messaggio valido");
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(msg);

                    if(!Objects.isNull(json) && json.containsKey("move")){
                        System.out.println("messaggio ricevuto = move");
                        moveReceived(topic, json);
                    }
                    else if (topic.contains("online/"))
                       onlinePlayerMessage(topic.replace("online/", ""));
                }else{ // not a valid message
                    System.out.println("ERROR; NOT A JSON MESSAGE");
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    /**
     * Given a player name this function sets the player as connected to the server.
     * @param player player name that has sent an online message
     */
    private void onlinePlayerMessage(String player) {
        onlinePlayers.replace(player, true); //player is online
    }

    /**
     * When a move is sent by a player this function determines the room and the specific game at which it belongs.
     * @param topic Topic at which the player has sent the message.
     * @param json JSON Object containing the move done by the player.
     */
    private void moveReceived(String topic, JSONObject json) {
        System.out.println("entra in moveReceived");
        if (topics.contains(subStringTopic(topic, "/", getTOPIC))){ //if the topic is valid (present in the list of topic)
            for (int i = 0; i < topics.size(); i++){
                if (subStringTopic(topic, "/", getTOPIC).equals(rooms.get(i).getTopic())){
                    System.out.println("trovata la substring");
                    rooms.get(i).makeAMove(Integer.parseInt(subStringTopic(topic, "/", getINSTANCE)), subStringTopic(topic, "/", getPLAYER),Integer.parseInt((String) json.get("move")));
                }
            }
        }
    }

    /**
     * This function is used to connect the Server to the broker.
     */
    private void connectToBroker() {
        try {

            String broker = "tcp://localhost:1883";
            String PubId = "GiacominoPaneVino";

            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, PubId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(60);
            connOpts.setKeepAliveInterval(60);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            connOpts.setUserName("Admin");
            connOpts.setPassword("Password".toCharArray());
            System.out.println("Connecting to broker: " + broker);
            setClientCallBacks();
            mqttClient.connect(connOpts);

        }catch(MqttException me) {
            System.out.println("Reason :"+ me.getReasonCode());
            System.out.println("Message :"+ me.getMessage());
            System.out.println("Local :"+ me.getLocalizedMessage());
            System.out.println("Cause :"+ me.getCause());
            System.out.println("Exception :"+ me);
            me.printStackTrace();
        }
    }

    /** Once game time is over this function will:
     * <ul>
     *     <li>End all games still going.</li>
     *     <li>Add points to every winner for every game.</li>
     *     <li>Send an MQTT message containing the results.</li>
     *     <li>Stop the broker.</li>
     *     <li>Mail the players the results of the tournament.</li>
     *     <li>Write the results in the result file.</li>
     * </ul>
     * **/
    private void gameOver() {
        endNotOverGames(); // if some games are not over this will end them
        addPoints(); // add points to players who won games
        sendRanking(); // send a mqtt message containing the rankings
        GameSettings.stopBroker();
        mailResults(); // send game results via mail
        writeResults(); // write the results of the game to the file
    }

    /**
     * This function has the task to reorder the results and send them in an MQTT message to every client.
     */
    private void sendRanking() {
        setRankingOrder();

        JSONObject obj = new JSONObject();
        for (int i = 0; i < playerWins.size(); i++)
            obj.put(i+1, playerWins.get(i).returnValue());

        System.out.println("RESULTS : " + obj);
        sendMessage("broadcast", obj.toString());
    }

    /**
     * This function is used to generate the mail content that will be sent to every player after the end of the tournament.
     */
    private void mailResults() {
        StringBuilder total= new StringBuilder();
        for (PlayerPoints playerWin : playerWins) total.append(playerWin.returnValue()).append("\n");

        String finalTotal = total.toString();
        this.onlinePlayers.forEach((key, value) -> SendMail.send(key.toString(), "Results", finalTotal));
    }
    /** This function reorders the list of players based on the points they managed to score in the tournament. **/
    private void setRankingOrder() {
        PlayerPoints temp;
        for (int i = 0; i < playerWins.size(); i++){
            for (int j = 1; j < playerWins.size(); j++){
                if (playerWins.get(j).getWins() > playerWins.get(j-1).getWins()){
                    temp = playerWins.get(j);
                    playerWins.set(j, playerWins.get(j-1));
                    playerWins.set(j-1, temp);
                }
            }
        }
    }
    /** This function checks every single game and adds the point to the winner. **/
    private void addPoints() {
        for (PlayerPoints playerWin : playerWins) {
            for (gameInstance room : rooms) {
                for (int k = 0; k < room.getSingle_games().size(); k++) {
                    if (room.getSingle_games().get(k).getWinner().equals(playerWin.getPlayer()))
                        playerWin.addPoint();
                }
            }
        }
    }
    /** If some games are still not over after the game is completed this function ends them, and sets the loser as the one who had to make the move (playerToMove) **/
    private void endNotOverGames() {
        for (gameInstance room : rooms) {
            for (int j = 0; j < room.getSingle_games().size(); j++) {
                if (room.getSingle_games().get(j).getWinner().equals("StillPlaying"))
                    room.getSingle_games().get(j).setLoser();
            }
        }
    }

    /**
     * This function checks if previous tournaments were saved on the "Results.txt" file.
     * If the file does not exist it gets created and the results get saved in this format -> player:points\n
     * If a file with records is present, previous results get added in the current file and the final result is saved.
     */
    private void writeResults() {
        File file = new File("Results.txt");
        try {
            if (!file.exists())
                System.out.println(file.createNewFile());
            else{
                Scanner sc = new Scanner(file);
                while(sc.hasNext())
                    oldResults.add(new PlayerPoints(sc.next(), "result"));

                for (PlayerPoints playerWin : playerWins) {
                    for (int j = 0; j < oldResults.size(); j++) {
                        if (playerWin.getPlayer().equals(oldResults.get(j).getPlayer())) {
                            playerWin.setWins(playerWin.getWins() + oldResults.get(j).getWins());
                            oldResults.remove(j);
                        }
                    }
                }
                if (oldResults.size() > 0)
                    playerWins.addAll(oldResults);
            }

            FileWriter fw = new FileWriter(file);
            for (PlayerPoints playerWin : playerWins) {
                fw.write(playerWin.returnValue() + "\n");
            }
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function publish an MQTT message.
     *
     * @param topic Takes the topic of the message that will be published.
     * @param msg Takes the message that will be published.
     */
    public static void sendMessage(String topic, String msg) {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function checks for every non-connected player and calls a function (notConnected)
     *
     * @param onlinePlayers Takes the JSON Object containing the list of player and their status: True=online, False=offline.
     */
    private static void checkForNotConnected(JSONObject onlinePlayers) {
        onlinePlayers.forEach((key, value) -> {
           if (value.toString().equals("false")) notConnected(key.toString()); //value == false, client not connected
       });
    }

    /**
     * This function sends a broadcast message (topic : broadcast) telling all the players that a specific user hasn't connected
     *
     * @param player Takes the name of the offline player
     */
    private static void notConnected(String player) {
        System.out.println("user" + player + " not connected");
        for (gameInstance room : rooms)
            if (room.isPlayedBy(player)) {
                System.out.println(room.getTopic() + " -> " + player + " has lost");
                room.hasLost(player);
            }
        JSONObject obj = new JSONObject();
        obj.put("not_connected", player);
        sendMessage("broadcast", obj.toString());
    }

    /**
     * This function removes a given topic from the topic list.
     *
     * @param topic Takes the topic name that will be removed from the topic list.
     */
    public static void removeTopic(String topic){
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function executes a function after a given time.
     *
     * @param runnable Takes the name of the function that has to be executed.
     * @param milliSeconds Takes the time after which the function has to be executed.
     */
    public static void startMethodAfterNMilliseconds(Runnable runnable, int milliSeconds) {
        Timer timer = new Timer(milliSeconds, e -> runnable.run());
        timer.setRepeats(false); // Only execute once
        timer.start();
    }
}
