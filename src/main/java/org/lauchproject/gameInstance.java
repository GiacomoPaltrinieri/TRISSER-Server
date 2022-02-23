package org.lauchproject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.io.File.separator;
/** This class describes every single room, it's players and everything related to all the games that have to played in that  **/
public class gameInstance {
    public static final int getTOPIC=0;
    public final int getINSTANCE=1;
    public final int getUSER=2;
    /** Attributes **/
    private String topic;
    private int room_number;
    private String[] players = new String[2]; // players involved in this room
    private ArrayList<SingleRoom>  rooms = new ArrayList<>(); // all the rooms (room_number, ArrayList moves, userMove)

    /** Methods **/

    /** Constructor **/
    public gameInstance( String topic, int room_number, String time) {
        this.topic = subStringTopic(topic, "/", getTOPIC);
        this.room_number = room_number;

        players[0] = subStringTopic(this.topic, "_", 0); // first player
        players[1] = subStringTopic(this.topic, "_", 1); //second player

        JSONObject obj = new JSONObject();
        for (int i = 0; i < this.room_number; i++){
            obj.clear();
            obj.put(players[0], new StopWatchTimer(Integer.parseInt(time)));
            obj.put(players[1], new StopWatchTimer(Integer.parseInt(time)));
            if (i%2==0) // if the instance number is not odd the first player to move will be player[0]
                rooms.add(new SingleRoom(i, obj, players[0]));
            else // if the instance number is odd the first player to move will be player[1]
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
    public static String subStringTopic(String string, String splitChars, int index){
        //index = getTOPIC || (0) -> topic
        //index = getINSTANCE || (1) -> instance
        //index = getUSER || (2) -> user
        String[] parts = string.split(splitChars);
        return parts[index];
    }
}
