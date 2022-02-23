package org.lauchproject;

import com.google.gson.Gson;

public final class IsJson {

    private static final Gson gson = new Gson();

    private IsJson(){}

    public static boolean isJSONValid(String jsonInString) {
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    //The main was for test Purpose
    /*public static void main(String[] args){
        System.out.println(isJSONValid("{\"time\": \"20\"}"));
    }*/

}
