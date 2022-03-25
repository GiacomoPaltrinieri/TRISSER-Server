package org.lauchproject;

import com.google.gson.Gson;

/**
 * Given a String return true or false if the string has a correct jsonArray or jsonObj syntax
 * @author Ali
 * @version 1.0
 */
public final class IsJson {

    private static final Gson gson = new Gson();

    private IsJson(){}

    /**
     * Return true Only for json arrays and objects
     * @param jsonInString
     * @return boolean
     */
    public static boolean isJSONValid(String jsonInString) {

        String fist= "" +jsonInString.charAt(0);
        String last= ""+jsonInString.charAt(jsonInString.length()-1);
        System.out.println(fist+last);

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
}
