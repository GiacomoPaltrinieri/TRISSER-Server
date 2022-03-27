package org.lauchproject;

import com.google.gson.Gson;

/**
 * Given a String return true or false if the string has a correct jsonArray or jsonObj syntax
 * link to stack overflow.'https://stackoverflow.com/questions/10174898/how-to-check-whether-a-given-string-is-valid-json-in-java#10174938'
 * @author Ali
 * @version 1.0
 */
public final class IsJson {
    /**Attributes**/
    /** Google Gson Object **/
    private static final Gson gson = new Gson();

    /**Methods**/
    /**Empty Constructor **/
    private IsJson(){}

    /**
     * Return true Only for json arrays and objects
     * @param jsonInString
     * @return boolean
     */
    public static boolean isJSONValid(String jsonInString) {
        /** assigne to 'first' the firts character of the string  **/
        String fist= "" +jsonInString.charAt(0);
        /** assigne to 'last' the last character of the string  **/
        String last= ""+jsonInString.charAt(jsonInString.length()-1);

        /** Check if the string contain the braces'{}' or the square brackets'[]'**/
        if(fist.equals("{") && last.equals("}") || fist.equals("[") && last.equals("]")){
            /** Try and catch if there is a json syntax error **/
            try {
                /** Try to parse the string into a json**/
                System.out.println(gson.fromJson(jsonInString, Object.class));
                /**Return true if the parse worked**/
                return true;
            } catch(com.google.gson.JsonSyntaxException ex) {
                /** Retrun false if it dosen't**/
                return false;
            }
        }else{
            /** Return false also if is not an Obj or Array**/
            return false;
        }
    }
}
