package org.lauchproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * This class represents a single instance of a room (mail1_mail2/n -> n is represented by a SingleRoom Object)
 **/


public class SingleRoom {

    /** Attributes **/
    private int roomNumber; // mail1_mail2/38 -> roomNumber=38
    private StopWatchTimer[] timers = new StopWatchTimer[2];
    private ArrayList<Integer> moves = new ArrayList<>(); // list of moves that have been made
    private String playerToMove; //name of the player that has to move
    private String winner = "StillPlaying"; // the name of the winning bot
    private String temp;
    public static ArrayList<String> winningMoves = new ArrayList<>(Arrays.asList("147", "258", "369", "123", "456", "789", "159", "357")); //,        Arrays.asList(["1","4", "7"], ["2","5", "8"], ["3","6", "9"], ["1","2", "3"], ["4","5", "6"], ["7","8", "9"], ["1","5", "9"], ["3","5", "7"])

    /** Constructor **/
    public SingleRoom(int roomNumber, StopWatchTimer[] timers, String playerToMove) {
        this.roomNumber = roomNumber;
        this.timers = timers;
        this.playerToMove = playerToMove;
    }

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

    public ArrayList<Integer> getMoves() {
        return moves;
    }


    /** returns true if the player has won, false if the game is still running **/
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

    public int getRoomNumber() {
        return roomNumber;
    }

    private String changePlayerToMove() {
        int t = 0;
        for (int i = 0; i <= 1; i++){
            if (!timers[i].getPlayer().equals(playerToMove) && t == 0){ //
                t++;
                playerToMove = timers[i].getPlayer();
                timers[i].stop();
                if (timers[i].getTime(StopWatchTimer.SECONDS) <= 0)
                    return timers[i].getPlayer() ;
            }else
                timers[i].start();
        }
        return "";
    }

    public void setWinner(String winner) {
        this.winner=winner;
    }

    public void setLoser(){
        if (playerToMove.equals(timers[0].getPlayer()))
            setWinner(timers[1].getPlayer());
        else if (playerToMove.equals(timers[1].getPlayer()))
            setWinner(timers[0].getPlayer());
    }

    public String getWinner() {
        return this.winner;
    }
}
