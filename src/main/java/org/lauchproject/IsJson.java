package org.lauchproject;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import java.nio.charset.StandardCharsets;

public final class IsJson {

    private static final Gson gson = new Gson();

    public IsJson(){}

    public static boolean isJSONValid(String jsonInString) {

        String fist= "" +jsonInString.charAt(0);
        String last= ""+jsonInString.charAt(jsonInString.length()-1);
        //System.out.println(fist+last);
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

    //For Testing
    /*public static void main(String[] args){
        JSONObject json=new JSONObject();
        json.put("trisser.bot2@gmail.com","true");

        MqttMessage message=new MqttMessage(json.toString().getBytes());
        System.out.println("MQTTMESSAGE-->"+message);
        System.out.println(isJSONValid(message.toString()));
    }*/
}
