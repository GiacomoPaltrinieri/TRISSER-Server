package org.lauchproject;

import java.util.ArrayList;

public class TESTWin {
    
    public static ArrayList<PlayerPoints> arr = new ArrayList<>();

    public static void main(String[] args) {
        arr.add(new PlayerPoints("giack"));
        arr.add(new PlayerPoints("ali"));
        arr.add(new PlayerPoints("greg"));

        arr.get(0).setWins(7);
        arr.get(1).setWins(12);
        arr.get(2).setWins(3);

        for (int i = 0; i < arr.size(); i++)
            System.out.println(arr.get(i).returnValue());
        System.out.println("\n\n\n");

        PlayerPoints temp;
        for (int i = 0; i < arr.size(); i++) {
            temp = arr.get(i);
            for (int j = i; j < arr.size(); j++) {
                if (arr.get(j).getWins() > temp.getWins()) {
                    arr.set(i, arr.get(j));
                    arr.set(j, temp);
                    temp = arr.get(i);
                }
            }
        }
        for (int i = 0; i < arr.size(); i++)
            System.out.println(arr.get(i).returnValue());
    }
}
