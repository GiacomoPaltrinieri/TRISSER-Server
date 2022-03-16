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
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static org.lauchproject.gameInstance.*;

public class MQTTPubPrint {

    public JSONObject onlineUsers = new JSONObject();
    public static final int qos = 0;
    private ArrayList<String> topics = GameSettings.getTopics();
    private static ArrayList<gameInstance> rooms = new ArrayList<>();
    private JSONObject rules = configData.getJsonRules();
    private static MqttClient sampleClient;
    private ArrayList<PlayerPoints> playerWins = new ArrayList<>();

    public MQTTPubPrint() {
        GameSettings.startBroker();
        for (String s : configData.getUsers()) {
            onlineUsers.put(s, false);
            playerWins.add(new PlayerPoints(s));
        }// list of users

//        GameSettings.startBroker();
//        ArrayList<String> ssss = new ArrayList<>();
//        ssss.add("TRISSER.bot3@gmail.com");
//        ssss.add("trisser.bot2@gmail.com");
//        for (String s : ssss) {
//            onlineUsers.put(s, false);
//            playerWins.add(new PlayerPoints(s));
//        }// list of users

        for (int i = 0; i < topics.size(); i++){
            System.out.println(topics.get(i));
        }


        String time = configData.getTime();
        int room_instance = Integer.parseInt(configData.getBot_number());

        for (int i = 0; i < topics.size(); i++) rooms.add(new gameInstance(topics.get(i), room_instance, time));

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

            sampleClient.connect(connOpts);
            sampleClient.subscribe("online/#"); //Listen to online topics
            System.out.println("Connected");

            //rules = getJSONfromFile("rules.txt");
            int connection_time;
            connection_time = Integer.parseInt(configData.getConnection_time());
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
                        int time = Integer.parseInt(configData.getTime());
                        time = time*1000;
                        startMethodAfterNMilliseconds(new Runnable() {
                            @Override
                            public void run() {
                                gameOver();
                            }
                        }, time);
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
        ArrayList<String> results = new ArrayList<>();

        for (int i = 0; i < rooms.size(); i++){
            for (int j = 0; j < rooms.get(i).getSingle_rooms().size(); j++){
                if (rooms.get(i).getSingle_rooms().get(j).getWinner().equals("StillPlaying"))
                    rooms.get(i).getSingle_rooms().get(j).setLoser();
            }
        } // if some games are not over this will end them

        for (int i = 0; i < playerWins.size(); i++){
            for (int j = 0; j < rooms.size(); j++){
                for (int k = 0; k < rooms.get(j).getSingle_rooms().size(); k++){
                    if (rooms.get(j).getSingle_rooms().get(k).getWinner().equals(playerWins.get(i).getPlayer()))
                        playerWins.get(i).addPoint();
                }
            }
        }

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

        for (int i = 0; i < playerWins.size(); i++){
            System.out.println(playerWins.get(i).getPlayer() + " : " + playerWins.get(i).getWins());
            results.add(playerWins.get(i).getPlayer()+" : "+playerWins.get(i).getWins());
        }

        JSONObject obj = new JSONObject();
        for (int i = 0; i < playerWins.size(); i++)
            obj.put(i+1, playerWins.get(i).getPlayer());

        String total="";
        for (int i = 0; i < results.size(); i++)
            total += results.get(i);

        for(int i = 0; i < this.onlineUsers.size(); i++) {
            this.onlineUsers.forEach((key, value) -> {
                SendMail.send(key.toString(), "Results", results.toString());
            });
        }

        for (int i = 0; i < onlineUsers.size(); i++){
            onlineUsers.forEach((key, value) -> {
                SendMail.send(key.toString(), "Results", results.toString());
                });
        }

        System.out.println(obj.toString());
        sendMessage("broadcast", obj.toString());
        GameSettings.stopBroker();
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
                System.out.println();
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
