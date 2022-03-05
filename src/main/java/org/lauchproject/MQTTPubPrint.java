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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static org.lauchproject.gameInstance.*;

public class MQTTPubPrint {

    public JSONObject onlineUsers = new JSONObject();
    public static final int qos = 1;
    private ArrayList<String> topics = new ArrayList<>();
    private static ArrayList<gameInstance> rooms = new ArrayList<>();
    private ArrayList<JSONObject> rules = getJSONfromFile("rules.txt");
    private static MqttClient sampleClient;
    private ArrayList<PlayerPoints> playerWins = new ArrayList<>();

    public MQTTPubPrint() {
        GameSettings.startBroker();
        for (String s : My_servlet.getUsers()) {
            onlineUsers.put(s, false);
            playerWins.add(new PlayerPoints(s));
        }// list of users

        topics = getLinesFromFile("topics.txt");
        String[] players = new String[2];
        int bot_number; // attento, il numero deve essere diviso per il numero di bot
        String time = String.valueOf(rules.get(0).get("bot_number"));
        bot_number = Integer.parseInt(String.valueOf(rules.get(0).get("bot_number")));
        for (int i = 0; i < topics.size(); i++) rooms.add(new gameInstance(topics.get(i), bot_number, time));

        try {

            String broker = "tcp://localhost:1883";
            String PubId = "127.0.0.1";

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

            sampleClient.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {}

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(topic + " says: \n" + message.toString());
                    String msg = message.toString();
                    if (IsJson.isJSONValid(msg)){
                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(msg);
                        String user;
                        // controlla le topic, cambia online perchè devi riconoscere l'user
                        if(!Objects.isNull(json) && json.containsKey("move")){
                            System.out.println("ma boh");
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
                            System.out.println("ciaoooooo");
                            onlineUsers.replace(user, true); //user is online
                            System.out.println(user + " True");
                        }
                    }else{
                        System.out.println("ERRORE; MESSAGGIO NON IN FORMATO JSON");
                    }

                }

                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            sampleClient.connect(connOpts);
            sampleClient.subscribe("online/#"); //Listen to online topics
            System.out.println("Connected");

            rules = getJSONfromFile("rules.txt");
            int connection_time;
            connection_time = Integer.parseInt(String.valueOf(rules.get(0).get("connection_time")));
            connection_time = connection_time*1000; // conversion in seconds

            int finalTime = connection_time;
            startMethodAfterNMilliseconds(new Runnable() {
                @Override
                public void run() {
                    // myMethod(); // Your method goes here.
                    try {
                        sampleClient.unsubscribe("online/#");
                        sampleClient.subscribe("#"); // now moves can be sent
                        System.out.println(finalTime);
                        checkForNotConnected(onlineUsers);
                        sendMessage("broadcast", "{\"game\":\"start\"}");
                        int time = Integer.parseInt(String.valueOf(rules.get(0).get("time")));
                        time = time*1000;
                        startMethodAfterNMilliseconds(new Runnable() {
                            @Override
                            public void run() {
                                gameOver();
                            }
                        }, time);
                        System.out.println(onlineUsers.toString());
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }, connection_time); // connection time is over

//            String topic = "online/dalterio.dario@einaudicorreggio.it";
//            String msg = "True";
//            MqttMessage message = new MqttMessage(msg.getBytes());
//            message.setQos(qos);
//            sampleClient.publish(topic, message);
//            System.out.println("Message published");

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
        for (int i = 0; i < rooms.size(); i++){ // scorro ogni topic
            for (int j = 0; j < rooms.get(i).getSingle_rooms().size(); j++){
                if (j < rooms.get(i).getSingle_rooms().size())
                    addPoint(rooms.get(i).getSingle_rooms().get(i).getWinner());
            }
        }

        PlayerPoints temp;
        for (int i = 0; i < playerWins.size(); i++){
            temp = playerWins.get(i);
            for (int j = i; j < playerWins.size(); j++){
                if (playerWins.get(j).getWins() > temp.getWins() && j < playerWins.size())
                    playerWins.set(i, playerWins.get(j));
                    playerWins.set(j, temp);
                    temp = playerWins.get(i);
            }
        }
        JSONObject obj = new JSONObject();
        for (int i = 0; i < playerWins.size(); i++)
            obj.put(i+1, playerWins.get(i).getPlayer());
        sendMessage("broadcast", obj.toString());
    }

    /** This function when called adds a point to a bot **/
    private void addPoint(String user){
        for (int i = 0; i < playerWins.size(); i++)
            if (playerWins.get(i).getPlayer().equals(user))
                playerWins.get(i).addPoint();
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

    private ArrayList<String> getLinesFromFile(String path) {
        File file = new File(path);
        ArrayList<String> list = new ArrayList<>();
        try {
            Scanner line = new Scanner(file);
            while (line.hasNext())
                list.add(line.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
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
        System.out.println("ciao");
        for (int i = 0; i < rooms.size(); i ++)
            if (rooms.get(i).isPlayedBy(user)){
                rooms.get(i).hasLost(user);
                JSONObject obj = new JSONObject();
                obj.put("not_connected", user);
                sendMessage("broadcast", obj.toString());
            }

    }

    /** Given a topic name this functions removes the topic from the listened topics **/
    public static void removeTopic(String topic){
        try {
            sampleClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /** Returns an ArrayList containing JsonObjects given a specific file **/
    private static ArrayList<JSONObject> getJSONfromFile(String filePath) {
        File file = new File(filePath);
        Scanner line = null;
        try {
            line = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();
        ArrayList<JSONObject> json = new JSONArray();

        while (line.hasNext()){
            try {
                json.add((JSONObject) parser.parse(line.nextLine()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println(json);
        return json;
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

    public static void main(String[] args) {
        new MQTTPubPrint();
    }
}
