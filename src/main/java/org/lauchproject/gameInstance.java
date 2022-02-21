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
    private String[] players = new String[2]; // players involved in this room
    private ArrayList<SingleRoom>  rooms; // all the rooms (room_number, ArrayList moves, userMove)
    private String[] whoHasToMove; // list of who has to make a move
    private String p1,p2;

    /** Methods **/

    /** Constructor **/
    public gameInstance(String[] players, JSONArray rooms, String topic) {
        this.players = players;
        this.rooms = rooms;
        this.topic = subStringTopic("/", getTOPIC);

        players[0] = subStringTopic("_", 0);
        players[1] = subStringTopic("_", 1);

        whoHasToMove = new String[rooms.size()]; // if the room number is divisible by 2 p1 starts, is the room number is odd p2 starts
        for (int i = 0; i < rooms.size(); i++){
            if (i % 2 == 0)
                whoHasToMove[i] = players[0];
            else
                whoHasToMove[i] = players[1];
        }
    }

    public void validMove(){}

    public void moveValidation (int move){
        String user = subStringTopic("/", getUSER);
        int instance = Integer.parseInt(subStringTopic("/", getINSTANCE));
        if (user.equals(whoHasToMove[instance])){
            //User turn, correct
        }else{
            //not his turn, incorrect
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
