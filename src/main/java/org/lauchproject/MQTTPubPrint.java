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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static org.lauchproject.gameInstance.*;

public class MQTTPubPrint {

    public JSONObject onlineUsers = new JSONObject();
    ArrayList<gameInstance> instances = new ArrayList(); // list of every topic
    private ArrayList<String> topics = new ArrayList<>();
    private ArrayList<gameInstance> rooms = new ArrayList<>();
    private ArrayList<JSONObject> rules = getJSONfromFile("rules.txt");
    private String time;
    public MQTTPubPrint() {

        for (String s : Arrays.asList("TRISSER.server@gmail.com", "giaco.paltri@gmail.com", "abdullah.ali@einaudicorreggio.it")) {
            onlineUsers.put(s, false);
        }// list of users

        topics = getLinesFromFile("topics.txt");
        String[] players = new String[2];
        int bot_number; // attento, il numero deve essere diviso per il numero di bot
        String time = String.valueOf(rules.get(0).get("bot_number"));
        bot_number = Integer.parseInt(String.valueOf(rules.get(0).get("bot_number")));
        for (int i = 0; i < topics.size(); i++) rooms.add(new gameInstance(topics.get(i), bot_number, time));

        try {
            int qos = 1;
            String broker = "tcp://localhost:1883";
            String PubId = "127.0.0.1";

            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient sampleClient = new MqttClient(broker, PubId, persistence);
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

                    //System.out.println(topic + " says: \n" + message.toString());
                    String msg = message.toString();
                    JSONParser parser = new JSONParser();
                    if (IsJson.isJSONValid(msg)){
                        JSONObject json = (JSONObject) parser.parse(msg);
                        String user;
                        // controlla le topic, cambia online perchè devi riconoscere l'user
                        if(!Objects.isNull(json.containsKey("move"))){
                            if (topics.contains(subStringTopic(topic, "/", getTOPIC)));
                            {
                                for (int i = 0; i < topics.size(); i++){
                                    if (subStringTopic(topic, "/", getTOPIC).equals(rooms.get(i).getTopic())){
                                        rooms.get(i).makeAMove(Integer.parseInt(subStringTopic(topic, "/", getINSTANCE)), subStringTopic(topic, "/", getUSER),Integer.parseInt((String) json.get("move")));
                                    }
                                }
                            }
                        }else if (!Objects.isNull(json.get("online")) && topic.contains("online/")){
                            user = topic.replace("online/", "");
                            onlineUsers.replace(user, true); //user is online
                            System.out.println(user + " True");
                        }
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
                        String topic = "broadcast";
                        String msg = "{\"game\":\"start\"}";
                        MqttMessage message = new MqttMessage(msg.getBytes());
                        message.setQos(qos);
                        sampleClient.publish(topic, message);
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

    private static void checkForNotConnected(JSONObject onlineUsers) {
       onlineUsers.forEach((key, value) -> {
           if (value.toString().equals("false")) notConnected(key.toString()); //value == false, client not connected
       });
    }

    private static void notConnected(String toString) {

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

    JSONArray userInfo = GameSettings.fileToJsonArray("userInfo"); // fetch all informations about users and rules

   JSONObject user = (JSONObject) userInfo.get(0);

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
