package org.lauchproject;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static org.lauchproject.gameInstance.*;

public class MQTTPubPrint {

    public JSONObject onlineUsers = new JSONObject();
    public static final int qos = 0;
    private ArrayList<String> topics = GameSettings.getTopics();
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

        for (int i = 0; i < topics.size(); i++) rooms.add(new gameInstance(topics.get(i), room_instance, time));
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

        //rules = getJSONfromFile("rules.txt");
        int connection_time;
        connection_time = Integer.parseInt(GUI_CLI_Run.getTemp_connessione());
        connection_time = connection_time*1000; // conversion in seconds

        int finalConnection_time = connection_time;
        startMethodAfterNMilliseconds(new Runnable() {
            @Override
            public void run() {
                // myMethod(); // Your method goes here.
                startGamePreparationPhase();
            }
        }, connection_time); // connection time is over
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
        startMethodAfterNMilliseconds(new Runnable() {
            @Override
            public void run() {
                gameOver();
            }
        }, time);
    }

    private void setClientCallBacks() {
        sampleClient.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {}

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = message.toString();
                if (IsJson.isJSONValid(msg)){
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(msg);
                    String user;
                    // controlla le topic, cambia online perchè devi riconoscere l'user
                    if(!Objects.isNull(json) && json.containsKey("move")){
                        if (topics.contains(subStringTopic(topic, "/", getTOPIC)));
                        {
                            for (int i = 0; i < topics.size(); i++){
                                if (subStringTopic(topic, "/", getTOPIC).equals(rooms.get(i).getTopic())){
                                    rooms.get(i).makeAMove(Integer.parseInt(subStringTopic(topic, "/", getINSTANCE)), subStringTopic(topic, "/", getUSER),Integer.parseInt((String) json.get("move")));
                                }
                            }
                        }
                    }else if (topic.contains("online/")){
                        user = topic.replace("online/", "");
                        onlineUsers.replace(user, true); //user is online
                        System.out.println(user + " -> True");
                    }
                }else{
                    System.out.println("ERRORE; MESSAGGIO NON IN FORMATO JSON");
                }

            }

            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
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

    /** Once gametime is over this function will generate the results and send them to the clients **/
    private void gameOver() {
        endNotOverGames(); // if some games are not over this will end them
        addPoints(); // add points to players who won games
        sendRanking(); // send an mqtt message cointaining the rankings
        GameSettings.stopBroker();
        mailResults(); // send game results via mail
        writeResults(); // write the results of the game to the file
    }

    private void sendRanking() {
        setRankingOrder();

        JSONObject obj = new JSONObject();
        for (int i = 0; i < playerWins.size(); i++)
            obj.put(i+1, playerWins.get(i).getPlayer());



        System.out.println(obj.toString());
        sendMessage("broadcast", obj.toString());
    }

    private void mailResults() {
        String total="";
        for (int i = 0; i < playerWins.size(); i++)
            total += playerWins.get(i).returnValue()+"\n";

        String finalTotal = total;
        this.onlineUsers.forEach((key, value) -> {
            SendMail.send(key.toString(), "Results", finalTotal);
        });
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
        for (int i = 0; i < playerWins.size(); i++){
            for (int j = 0; j < rooms.size(); j++){
                for (int k = 0; k < rooms.get(j).getSingle_rooms().size(); k++){
                    if (rooms.get(j).getSingle_rooms().get(k).getWinner().equals(playerWins.get(i).getPlayer()))
                        playerWins.get(i).addPoint();
                }
            }
        }
    }

    private void endNotOverGames() {
        for (int i = 0; i < rooms.size(); i++){
            for (int j = 0; j < rooms.get(i).getSingle_rooms().size(); j++){
                if (rooms.get(i).getSingle_rooms().get(j).getWinner().equals("StillPlaying"))
                    rooms.get(i).getSingle_rooms().get(j).setLoser();
            }
        }
    }

    private void writeResults() {
        File file = new File("Results.txt");
        try {
            if (!file.exists())
                file.createNewFile();
            else{
                Scanner sc = new Scanner(file);
                while(sc.hasNext())
                    oldResults.add(new PlayerPoints(sc.next(), "result"));

                for (int i = 0; i < playerWins.size(); i++){
                    for (int j = 0; j < oldResults.size(); j++){
                        if (playerWins.get(i).getPlayer().equals(oldResults.get(j).getPlayer())) {
                            playerWins.get(i).setWins(playerWins.get(i).getWins() + oldResults.get(j).getWins());
                            oldResults.remove(j);
                        }
                    }
                }
                if (oldResults.size() > 0)
                    for (int i=0; i < oldResults.size(); i++)
                        playerWins.add(oldResults.get(i));
            }

            FileWriter fw = new FileWriter(file);
            for (int i = 0; i < playerWins.size(); i++){
                fw.write(playerWins.get(i).returnValue()+"\n");
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
           System.out.println("userino : " + key);
           if (value.toString().equals("false")) notConnected(key.toString()); //value == false, client not connected
       });
    }

    /** This function sends a message containing informations about not connected users to every connected user  **/
    private static void notConnected(String user) {
        System.out.println("user" + user + " not connected");
        for (int i = 0; i < rooms.size(); i ++)
            if (rooms.get(i).isPlayedBy(user)){
                System.out.println(rooms.get(i).getTopic() + " -> " + user + " has lost");
                rooms.get(i).hasLost(user);
            }
        System.out.println("in teoria dovrebbe inviarlo sto messaggio");
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
        Timer timer = new Timer(milliSeconds, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                runnable.run();
            }
        });
        timer.setRepeats(false); // Only execute once
        timer.start();
    }
}
