package org.lauchproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * The SingleGame class is used to manage and contain all the information about a single match between two different bots.
 * A SingleGame Object is used multiple times in a gameInstance Object where all the matches between two bots have to be disputed.
 *
 * @see gameInstance
 * @author Giacomino
 **/
public class SingleGame {

    /** roomNumber is a unique identifier that refers to a single game. **/
    private int roomNumber; // mail1_mail2/38 -> roomNumber=38
    /** timers array is used to contain the timers for the two bots. **/
    private StopWatchTimer[] timers = new StopWatchTimer[2];
    /** moves contains the set of moves that have been done in the game. **/
    private ArrayList<Integer> moves = new ArrayList<>(); // list of moves that have been made
    /** playerToMove contains the name (mail) that is expected to make a move. **/
    private String playerToMove; //name of the player that has to move
    /** winner contains the name (mail) of the winner of a match in case there is one. It may contain "none" in case no one wins the game**/
    private String winner = "StillPlaying"; // the name of the winning bot
    /** Contains the set of moves to be made in order to win the game **/
    public static ArrayList<String> winningMoves = new ArrayList<>(Arrays.asList("147", "258", "369", "123", "456", "789", "159", "357"));

    /** ---------------------------- Constructor ---------------------------- **/

    /**
     * The constructor is used to set the values that define a single room.
     *
     * @param roomNumber Used to identify the single game. mail1_mail2/n -> roomNumber = n.
     * @param timers Used to create a timer for every user involved in the SingleGame
     * @param playerToMove Used to define which player has to make the first move and then which player
     */
    public SingleGame(int roomNumber, StopWatchTimer[] timers, String playerToMove) {
        this.roomNumber = roomNumber;
        this.timers = timers;
        this.playerToMove = playerToMove;
    }

    /**
     * This function receives a move and, after verifying its validity, adds it to the moves array.
     *
     * @param move Contains the player move to be added to the array of moves.
     * @param user Contains the username of the player that has sent the move.
     * @param topic Contains the topic that identifies the game the two players are disputing.
     */
    public void makeMove(int move, String user, String topic) {
        if (user.equals(playerToMove)){
            // user turn to move
            if (moves.contains(move)){
                System.out.println("move already done");
                MQTTPubPrint.sendMessage(topic+"/"+roomNumber, "{\"error\":" + "\"" + moves.toString() + "\"," + "player:" + "\"" + playerToMove + "\"}");
            }else if(move > 9 || move < 1){
                //ADD MESSAGE ERROR
                MQTTPubPrint.sendMessage(topic+"/"+roomNumber, "{\"error\":" + "\"" + moves.toString() + "\"," + "player:" + "\"" + playerToMove + "\"}");
                System.out.println("invalid move");
            } else if(moves.size()<=4){
                moves.add(move);
                changePlayerToMove();
                System.out.println("numero di mosse insufficienti per vincere, mossa valida");
            }else{
                moves.add(move);
                if (isWinning()){
                    System.out.println(playerToMove + " has won");
                    setWinner(playerToMove);
                }else if (moves.size() >= 9){
                    System.out.println("no one has won (pareggio)");
                    setWinner("none");
                }
                 else{
                    changePlayerToMove();
                    System.out.println("not won");
                }
            }
        }else{
            System.out.println("not his turn");
        }
    }

    /**
     * This function is used to determine whether a move is winning for a specific user or not.
     *
     * @return returns true is the player has won the game, returns false if the game is still going.
     */
    private boolean isWinning() {
        ArrayList<Integer> playerMoves = new ArrayList<>();
        String[] winningMovesSet = new String[3];

        if (moves.size()%2 == 0)
            for (int i = 0; i < moves.size(); i+=2){
                if (moves.size()>=i) {
                    playerMoves.add(moves.get(i));
                }
            }
        else
            for (int i = 1; i < moves.size(); i+=2)
                if (moves.size()>i)
                    playerMoves.add(moves.get(i));

        System.out.println(playerMoves);
        boolean[] win = new boolean[3];

        for ( int k = 0; k < winningMoves.size(); k++){
                for (int j = 0; j < 3; j++){
                    winningMovesSet[j]= String.valueOf(winningMoves.get(k).charAt(j)); // single set of possible winning moves
                    win[j] = false;
                }

            for (int i = 0; i < winningMovesSet.length; i++){ // scorro ogni elemento del set di mosse vincenti
                for (int j = 0; j < playerMoves.size(); j++){ // scorro ogni mossa fatta dal giocatore
                    if (winningMovesSet[i].equals(playerMoves.get(j).toString())){ // se la mossa corrisponde a una del set vincente
                        win[i] = true;
                    }
                }
            }

            if (win[0] == true && win[1] == true && win[2] == true)
                return true;
        }
            return false;
    }

    /**
     * This function is used to get the room number of the specific game.
     * @return roomNumber returns the room number of the single game.
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * When a move is done, this function is used to set the player that has to move as the other player.
     * When stopping the timer, if the player has no time left, the winner will be set as the other player.
     */
    private void changePlayerToMove() {
        int t = 0;
        for (int i = 0; i <= 1; i++){
            if (!timers[i].getPlayer().equals(playerToMove) && t == 0){ //
                t++;
                playerToMove = timers[i].getPlayer();
                timers[i].stop();
                changePlayerToMove();
                if (timers[i].getTime(StopWatchTimer.SECONDS) <= 0)
                    setWinner(playerToMove);
                changePlayerToMove();
            }else
                timers[i].start();
        }
    }

    /**
     * Takes the winner name as an input, and sets the local attribute this.winner as the received parameter.
     * @param winner Contains the name of the bot that has won the game.
     */
    public void setWinner(String winner) {
        this.winner=winner;
    }

    /**
     * Sets the player that has to make a move as the loser, this.winner will be the user that has not to make a move.
     */
    public void setLoser(){
        if (playerToMove.equals(timers[0].getPlayer()))
            setWinner(timers[1].getPlayer());
        else if (playerToMove.equals(timers[1].getPlayer()))
            setWinner(timers[0].getPlayer());
    }

    /**
     * This function is used to return the winner name.
     * In case the game is still not over the returned value will be "StillPlaying".
     * In case there is not a winner, the returned value will be "none".
     *
     * @return this.winner returns the game winner name.
     */
    public String getWinner() {
        return this.winner;
    }
}
