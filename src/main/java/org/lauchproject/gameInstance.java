package org.lauchproject;
import java.util.ArrayList;

/**
 * The gameInstance class is used to manage and contain all the information about a single topic where two players will dispute all the matches between each other.
 * Mail1_mail2 -> GameInstance Object.
 * In a GameInstance Object are defined all the games (SingleGame) where the two players will dispute their matches.
 *
 * @see MQTTPubPrint
 * @see SingleGame
 * @author Giacomino
 **/

public class gameInstance {
    public static final int getTOPIC=0;
    public static final int getINSTANCE=1;
    public static final int getPLAYER=2;

    /** Attributes **/

    /** Contains the topic name that will define the GameInstance Object. --> mail1_mail2. **/
    private String topic;
    /** Contains the number of games that will have to be played in a single room. **/
    private int room_number;
    /** Array containing the two players involved in a single GameInstance. **/
    private String[] players = new String[2]; // players involved in this room
    /** ArrayList containing every SingleGame that the two players have to play against each other. **/
    private ArrayList<SingleGame>  single_rooms = new ArrayList<>(); // all the single_rooms (room_number, ArrayList moves, userMove)
    /** Contains the two timers that will be given to every player in every match. **/
    private StopWatchTimer timers[] = new StopWatchTimer[2];

    /** Methods **/

    /**
     * Public constructor.
     * The class attributes will be updated using inserted data.
     * An ArrayList defining every game gets instanced.
     *
     * @param topic Contains the topic name --> mail1_mail2 that uniquely identifies the Instance.
     * @param room_number Contains the number of games the two players will have to play
     * @param time Contains the available time that the two users will have to make their moves.
     */
    public gameInstance(String topic, int room_number, String time) {
        this.topic = subStringTopic(topic, "/", getTOPIC);
        this.room_number = room_number;

        players[0] = subStringTopic(this.topic, "_", 0); // first player
        players[1] = subStringTopic(this.topic, "_", 1); //second player

        timers[0] = new StopWatchTimer(Integer.parseInt(time), players[0]);
        timers[1] = new StopWatchTimer(Integer.parseInt(time), players[1]);
        for (int i = 0; i < this.room_number; i++){
            if (i%2==0) // if the instance number is not odd the first player to move will be player[0]
                single_rooms.add(new SingleGame(i, timers, players[0]));
            else // if the instance number is odd the first player to move will be player[1]
                single_rooms.add(new SingleGame(i, timers, players[1]));
        }

    }

    /**
     * Public getter used to return the topic name that defines the GameInstance Object.
     *
     * @return Returns the topic name.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * This function is used to find the single game where a player can make a move and let him play, choosing a move to execute.
     *
     * @param instance Contains the identifier that refers to a single game.
     * @param player Contains the player name that executes the move.
     * @param move Contains the move the player wants to execute.
     */
    public void makeAMove(int instance, String player, int move){
        for (int i = 0; i < single_rooms.size(); i++){
            if (single_rooms.get(i).getRoomNumber() == instance){
                System.out.println("entra in MakeAMove su gameInstance " + move + " " + player);
                single_rooms.get(i).makeMove(move, player, topic);
            }
        }
    }

    /**
     *
      * @param string The string you want to split.
     * @param splitChars The char or string you want to use as a delimiter to split the String.
     * @param index An integer number that defines which part of the split String you want to return.
     * @return Returns the subString generated using the parameters inserted.
     */
    public static String subStringTopic(String string, String splitChars, int index){
        //index = getTOPIC || (0) -> topic
        //index = getINSTANCE || (1) -> instance
        //index = getPLAYER || (2) -> user
        String[] parts = string.split(splitChars);
        return parts[index];
    }

    /**
     *
     * @param player
     * @return Returns true if the player is one of the players involved in the GameInstance Object, else way the returned value will be false.
     */
    public boolean isPlayedBy(String player) {
        if (topic.contains(player))
            return true;
        return false;
    }

    /**
     * This function is used to set a user as a winner in <stron>every</stron> SingleGame Object
     * @param user Contains the user that has to be set as the loser in every SingleGame Object
     */
    public void hasLost(String user) {
        String winner = topic.replace("_", "");
        winner = winner.replace(user, "");
        MQTTPubPrint.removeTopic(topic); // stop listening to this topic
        System.out.println("Winner on room" + topic + " = " + winner);

        for (int i = 0; i < single_rooms.size(); i ++){
            single_rooms.get(i).setWinner(winner);
        }
    }

    /**
     * Getter used to return every Object that describes a game part of the GameInstance.
     * @return ArrayList containing every SingleGame Object defined in a single GameInstance.
     */
    public ArrayList<SingleGame> getSingle_rooms() {
        return single_rooms;
    }
}
