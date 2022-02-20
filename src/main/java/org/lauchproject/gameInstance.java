package org.lauchproject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.io.File.separator;

public class gameInstance {
    /** Attributes **/
    private JSONObject players; // players involved in this room and time
    private String gameInstanceName; // the name of the topic (mail1_mail2)
    private JSONArray  rooms; // all the rooms (room_number, ArrayList moves, userMove)

    /** Methods **/

    /** Constructor **/
    public gameInstance(JSONObject players, String gameInstanceName, JSONArray rooms) {
        this.players = players;
        this.gameInstanceName = gameInstanceName;
        this.rooms = rooms;
    }

    public void moveValidation (int move, String topic){

    }
    /** Given a specific topic this function returns the user that has sent the message **/
    public String getUser(String topic){
        int lastIndexOf = topic.lastIndexOf( "/" );
        String user = topic.substring( lastIndexOf + 1 );
        System.out.print(user);
        return user;
    }
}
