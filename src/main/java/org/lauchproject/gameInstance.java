package org.lauchproject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.io.File.separator;

public class gameInstance {
    final int getTOPIC=0;
    final int getINSTANCE=1;
    final int getUSER=2;
    /** Attributes **/
    private String topic;
    private JSONObject players; // players involved in this room and time
    private JSONArray  rooms; // all the rooms (room_number, ArrayList moves, userMove)
    private String[] whoHasToMove; // list of who has to make a move
    private String p1,p2;

    /** Methods **/

    /** Constructor **/
    public gameInstance(JSONObject players, JSONArray rooms, String topic) {
        this.players = players;
        this.rooms = rooms;
        this.topic = subStringTopic("/", topic, getTOPIC);

        p1 = subStringTopic("_", this.topic, 0);
        p2 = subStringTopic("_", this.topic, 1);

        whoHasToMove = new String[rooms.size()]; // if the room number is divisible by 2 p1 starts, is the room number is odd p2 starts
        for (int i = 0; i < rooms.size(); i++){
            if (i % 2 == 0)
                whoHasToMove[i] = p1;
            else
                whoHasToMove[i] = p2;
        }
    }

    public void validMove()

    public void moveValidation (int move, String topic){
        String user = subStringTopic("/", topic,getUSER);
    }
//    /** Given a specific topic this function returns the user that has sent the message **/
//    public String getUser(String topic){
//        int lastIndexOf = topic.lastIndexOf( "/" );
//        String user = topic.substring( lastIndexOf + 1 );
//        System.out.print(user);
//        return user;
//    }

    /** subtracts data from the topic **/
    public String subStringTopic(String splitChars, String topic, int index){
        //index = getTOPIC || (0) -> topic
        //index = getINSTANCE || (1) -> instance
        //index = getUSER || (2) -> user
        String[] parts = topic.split(splitChars);
        return parts[index];
    }
}
