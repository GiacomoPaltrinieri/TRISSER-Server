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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class MQTTPubPrint {

    public static JSONObject onlineUsers = new JSONObject();
    public static void main(String[] args) {

        for (String s : Arrays.asList("TRISSER.server@gmail.com", "giaco.paltri@gmail.com", "abdullah.ali@einaudicorreggio.it")) {
            onlineUsers.put(s, false);
        }// list of users

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
                    JSONObject json = (JSONObject) parser.parse(msg);
                    String user;

                    if(!Objects.isNull(json.get("move"))){
                        System.out.println(json.get("move"));
                    }else if (!Objects.isNull(json.get("online")) && topic.contains("online/")){
                        user = topic.replace("online/", "");
                        onlineUsers.replace(user, true); //user is online
                        System.out.println(user + " True");
                    }
                }

                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            sampleClient.connect(connOpts);
            sampleClient.subscribe("online/#"); //Listen to online topics
            System.out.println("Connected");

            ArrayList<JSONObject> rules = getJSONfromFile("rules.txt");
            int time;
            time = Integer.parseInt(String.valueOf(rules.get(0).get("connection_time")));
            time = time*1000; // conversion in seconds

            int finalTime = time;
            startMethodAfterNMilliseconds(new Runnable() {
                @Override
                public void run() {
                    // myMethod(); // Your method goes here.
                    try {
                        sampleClient.unsubscribe("online/#");
                        System.out.println(finalTime);
                        checkForNotConnected(onlineUsers);
                        System.out.println(onlineUsers.toString());
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }, time);




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
