package org.lauchproject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.io.File.separator;
/** This class describes every single room, it's players and everything related to all the games that have to played in that  **/
public class gameInstance {
    final int getTOPIC=0;
    final int getINSTANCE=1;
    final int getUSER=2;
    /** Attributes **/
    private String topic;
    private int room_number;
    private String[] players = new String[2]; // players involved in this room
    private ArrayList<SingleRoom>  rooms = new ArrayList<>(); // all the rooms (room_number, ArrayList moves, userMove)

    /** Methods **/

    /** Constructor **/
    public gameInstance(String[] players, JSONArray rooms, String topic, int room_number, String time) {
        this.players = players;
        this.rooms = rooms;
        this.topic = subStringTopic("/", getTOPIC);

        players[0] = subStringTopic("_", 0);
        players[1] = subStringTopic("_", 1);

        JSONObject obj = new JSONObject();
        for (int i = 0; i < this.room_number; i++){
            obj.clear();
            obj.put(players[0], time);
            obj.put(players[1], time);
            if (i%2==0)
                rooms.add(new SingleRoom(i, obj, players[0]));
            else
                rooms.add(new SingleRoom(i, obj, players[1]));
        }

    }

//    /** Given a specific topic this function returns the user that has sent the message **/
//    public String getUser(String topic){
//        int lastIndexOf = topic.lastIndexOf( "/" );
//        String user = topic.substring( lastIndexOf + 1 );
//        System.out.print(user);
//        return user;
//    }

    /** subtracts data from the topic **/
    public String subStringTopic(String splitChars, int index){
        //index = getTOPIC || (0) -> topic
        //index = getINSTANCE || (1) -> instance
        //index = getUSER || (2) -> user
        String[] parts = topic.split(splitChars);
        return parts[index];
    }
}
