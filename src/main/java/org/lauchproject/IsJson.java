package org.lauchproject;

import com.google.gson.Gson;


public final class IsJson {

    private static final Gson gson = new Gson();

    private IsJson(){}

    public static boolean isJSONValid(String jsonInString) {

        String fist= "" +jsonInString.charAt(0);
        String last= ""+jsonInString.charAt(jsonInString.length()-1);
        System.out.println(fist+last);
        /** Return true Only for json arrays and objects **/
        if(fist.equals("{") && last.equals("}") || fist.equals("[") && last.equals("]")){
            try {
                System.out.println(gson.fromJson(jsonInString, Object.class));
                return true;
            } catch(com.google.gson.JsonSyntaxException ex) {
                return false;
            }
        }else{
            return false;
        }
    }

    //The main was for test Purpose
    /*public static void main(String[] args){
        System.out.println(isJSONValid("[\"Ford\", \"BMW\", \"Fiat\"]"));
    }*/



}
