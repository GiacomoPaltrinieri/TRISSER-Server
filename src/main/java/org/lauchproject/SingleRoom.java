package org.lauchproject;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SingleRoom {

    /** Attributes **/
    private int roomNumber; // mail1_mail2/38 -> roomNumber=38
    private StopWatchTimer[] timers = new StopWatchTimer[2];
    private ArrayList<Integer> moves = new ArrayList<>(); // list of moves that have been made
    private String playerToMove; //name of the player that has to move
    private String winner; // the name of the winning bot
    private String temp;
    public static ArrayList<String> winningMoves = new ArrayList<>(Arrays.asList("147", "258", "369", "123", "456", "789", "159", "357"));

    /** Constructor **/
    public SingleRoom(int roomNumber, StopWatchTimer[] timers, String playerToMove) {
        this.roomNumber = roomNumber;
        this.timers = timers;
        this.playerToMove = playerToMove;
    }

    public void makeMove(int move, String user) {
        if (user.equals(playerToMove)){
            // user turn to move
            if (moves.contains(move)){
                System.out.println("move already done");
            }else if(move > 9 || move < 1){
                System.out.println("invalid move");
            } else if(moves.size()<=5){
                moves.add(move);
                changePlayerToMove();
                System.out.println("numero di mosse insufficienti per vincere, mossa valida");
            }else{
                moves.add(move);
                if (isWinning(move)){
                    System.out.println(playerToMove + " has won");
                }else if (moves.size() == 9){
                    System.out.println("no one has won (pareggio)");
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



    private boolean isWinning(int move) {
        int oddOrNot = 0;
        ArrayList<Integer> playerMoves = new ArrayList<>();
        if (moves.size()%2 == 0)
            oddOrNot = 1; // le mosse fatte da questo utente sono quelle nelle caselle pari

        if (oddOrNot == 1){
            for (int i = 0; i < moves.size() - 1; i+=2)
                playerMoves.add(moves.get(i));
        }
        else
            for (int i = 1; i < moves.size() - 1; i+=2)
                playerMoves.add(moves.get(i));
        Collections.sort(playerMoves);
        System.out.println(playerMoves);

        temp="";
        for (int i = 0; i < playerMoves.size(); i++)
            temp = temp + playerMoves.get(i);

        for (int i = 0; i < winningMoves.size(); i++)
            if (winningMoves.get(i).contains(temp)) // maybe
                return true;
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
}
