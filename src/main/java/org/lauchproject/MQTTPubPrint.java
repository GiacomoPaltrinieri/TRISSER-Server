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

public class MQTTPubPrint {

    public JSONObject onlineUsers = new JSONObject();
    public static final int qos = 0;
    private final ArrayList<String> topics = GameSettings.getTopics();
    private static ArrayList<gameInstance> rooms = new ArrayList<>();
    private static MqttClient sampleClient;
    private ArrayList<PlayerPoints> playerWins = new ArrayList<>();
    private ArrayList<PlayerPoints> oldResults = new ArrayList<>();

    public MQTTPubPrint() {
        GameSettings.startBroker();
        for (String s : GUI_CLI_Run.getUsers()) {
            onlineUsers.put(s, false);
            playerWins.add(new PlayerPoints(s));
        }// list of users

        String time = GUI_CLI_Run.getTemp_gioco_bot();
        int room_instance = Integer.parseInt(GUI_CLI_Run.getBot_istance());

        for (String topic : topics) rooms.add(new gameInstance(topic, room_instance, time));
        connectToBroker();
        startConnectionPhase();
    }

    private void startConnectionPhase() {
        try {
            sampleClient.subscribe("online/#"); //Listen to online topics
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Connected");

        int connection_time;
        connection_time = Integer.parseInt(GUI_CLI_Run.getTemp_connessione());
        connection_time = connection_time*1000; // conversion in seconds

        startMethodAfterNMilliseconds(this::startGamePreparationPhase, connection_time); // connection time is over
    }

    private void startGamePreparationPhase() {
        try {
            sampleClient.subscribe("#"); // now moves can be sent
            sampleClient.unsubscribe("online/#");
            checkForNotConnected(onlineUsers);
            startGamePhase();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void startGamePhase() {
        sendMessage("broadcast", "{\"game\":\"start\"}");
        int time = Integer.parseInt(GUI_CLI_Run.getTemp_gioco_bot());
        time = time*1000;
        startMethodAfterNMilliseconds(this::gameOver, time);
    }

    private void setClientCallBacks() {
        sampleClient.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {}

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = message.toString();
                if (IsJson.isJSONValid(msg)){
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(msg);

                    if(!Objects.isNull(json) && json.containsKey("move")){
                        moveReceived(topic, json);
                    }else if (topic.contains("online/")){
                        onlineUsers.replace(topic.replace("online/", ""), true); //user is online
                        System.out.println(topic.replace("online/", "") + " -> True");
                    }
                }else{
                    System.out.println("ERROR; NOT A JSON MESSAGE");
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    private void moveReceived(String topic, JSONObject json) {
        if (topics.contains(subStringTopic(topic, "/", getTOPIC))){ //if the topic is valid (present in the list of topic)
            for (int i = 0; i < topics.size(); i++){
                if (subStringTopic(topic, "/", getTOPIC).equals(rooms.get(i).getTopic())){
                    rooms.get(i).makeAMove(Integer.parseInt(subStringTopic(topic, "/", getINSTANCE)), subStringTopic(topic, "/", getUSER),Integer.parseInt((String) json.get("move")));
                }
            }
        }
    }

    private void connectToBroker() {
        try {

            String broker = "tcp://localhost:1883";
            String PubId = "GiacominoPaneVino";

            MemoryPersistence persistence = new MemoryPersistence();
            sampleClient = new MqttClient(broker, PubId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(60);
            connOpts.setKeepAliveInterval(60);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            connOpts.setUserName("Admin");
            connOpts.setPassword("Password".toCharArray());
            System.out.println("Connecting to broker: " + broker);
            setClientCallBacks();
            sampleClient.connect(connOpts);

        }catch(MqttException me) {
            System.out.println("Reason :"+ me.getReasonCode());
            System.out.println("Message :"+ me.getMessage());
            System.out.println("Local :"+ me.getLocalizedMessage());
            System.out.println("Cause :"+ me.getCause());
            System.out.println("Exception :"+ me);
            me.printStackTrace();
        }
    }

    /** Once game time is over this function will generate the results and send them to the clients **/
    private void gameOver() {
        endNotOverGames(); // if some games are not over this will end them
        addPoints(); // add points to players who won games
        sendRanking(); // send a mqtt message containing the rankings
        GameSettings.stopBroker();
        mailResults(); // send game results via mail
        writeResults(); // write the results of the game to the file
    }

    private void sendRanking() {
        setRankingOrder();

        JSONObject obj = new JSONObject();
        for (int i = 0; i < playerWins.size(); i++)
            obj.put(i+1, playerWins.get(i).getPlayer());



        System.out.println(obj);
        sendMessage("broadcast", obj.toString());
    }

    private void mailResults() {
        StringBuilder total= new StringBuilder();
        for (PlayerPoints playerWin : playerWins) total.append(playerWin.returnValue()).append("\n");

        String finalTotal = total.toString();
        this.onlineUsers.forEach((key, value) -> SendMail.send(key.toString(), "Results", finalTotal));
    }

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

    private void addPoints() {
        for (PlayerPoints playerWin : playerWins) {
            for (gameInstance room : rooms) {
                for (int k = 0; k < room.getSingle_rooms().size(); k++) {
                    if (room.getSingle_rooms().get(k).getWinner().equals(playerWin.getPlayer()))
                        playerWin.addPoint();
                }
            }
        }
    }

    private void endNotOverGames() {
        for (gameInstance room : rooms) {
            for (int j = 0; j < room.getSingle_rooms().size(); j++) {
                if (room.getSingle_rooms().get(j).getWinner().equals("StillPlaying"))
                    room.getSingle_rooms().get(j).setLoser();
            }
        }
    }

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

    /** This function sends an MQTT message **/
    public static void sendMessage(String topic, String msg) {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        try {
            sampleClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /** This function checks whether a user is connected or not **/
    private static void checkForNotConnected(JSONObject onlineUsers) {
       onlineUsers.forEach((key, value) -> {
           if (value.toString().equals("false")) notConnected(key.toString()); //value == false, client not connected
       });
    }

    /** This function sends a message containing data about not connected users to every connected user  **/
    private static void notConnected(String user) {
        System.out.println("user" + user + " not connected");
        for (gameInstance room : rooms)
            if (room.isPlayedBy(user)) {
                System.out.println(room.getTopic() + " -> " + user + " has lost");
                room.hasLost(user);
            }
        JSONObject obj = new JSONObject();
        obj.put("not_connected", user);
        sendMessage("broadcast", obj.toString());
    }

    /** Given a topic name this functions removes the topic from the listened topics **/
    public static void removeTopic(String topic){
        try {
            sampleClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /** This function waits for a specific time to execute a specific function **/
    public static void startMethodAfterNMilliseconds(Runnable runnable, int milliSeconds) {
        Timer timer = new Timer(milliSeconds, e -> runnable.run());
        timer.setRepeats(false); // Only execute once
        timer.start();
    }
}
