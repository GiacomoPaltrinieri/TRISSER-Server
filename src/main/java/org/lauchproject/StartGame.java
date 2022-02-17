package org.lauchproject;

import org.json.simple.JSONArray;

public class StartGame {
    public static void main(String[] args){
        JSONArray userInfo = GameSettings.fileToJsonArray("userInfo.txt"); // userInfo will now contain all the info sent via email
        System.out.println(userInfo.toString().replace("\\", ""));
    }
}
