package org.lauchproject;

import org.json.simple.JSONObject;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/**
 * Get the rules for the tournament from the send-data.js script
 * @author Ali
 * @version 4.0
 */
@WebServlet(name = "MyServlet",value ="/MyServlet")
public class My_servlet extends HttpServlet{
    /**Attributes**/
    /** ArrayLIst of Strings used to store all the bots emails **/
    private static ArrayList<String> users=new ArrayList<>();
    /**JSONObject use to store all the tournament rules**/
    private static JSONObject rules = new JSONObject();
    /**String variables to contain single rules **/
    private static String bot_num,temp_gioco_bot,temp_connessione,data_start_game,temp_start_game,bot_instance;
    /**Methods**/
    /**Getters**/
    /**
     * User Getter
     * @return ArrayList of Strings
     */
    public static ArrayList<String> getPlayers() {
        return users;
    }

    /**
     * Rules Getter
     * @return JSONObject
     */
    public static JSONObject getRules() {
        return rules;
    }

    /**
     * Bot numer Getter
     * @return String
     */
    public static String getBot_num() {
        return bot_num;
    }

    /**
     * Time of Single match Getter
     * @return String
     */
    public static String getTemp_gioco_bot() {
        return temp_gioco_bot;
    }

    /**
     * Bot Connection time Getter
     * @return String
     */
    public static String getTemp_connessione() {
        return temp_connessione;
    }

    /**
     * Tournament starting Date getter
     * @return String
     */
    public static String getData_start_game() {
        return data_start_game;
    }

    /**
     * Tournament starting time getter
     * @return String
     */
    public static String getTemp_start_game() {
        return temp_start_game;
    }

    /**
     * Bot instance number(total matches of the tournament) Getter
     * @return
     */
    public static String getBot_instance() {
        return bot_instance;
    }

    /**
     * Post method to get Tournaments rules from the XMLHttpReques created by send_data.js
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /**Setting the content type of the response**/
        response.setContentType("text/html");

        /**Initializing bot_istance with front-end parameter  **/
        bot_instance=request.getParameter("bot_istance");
        /**Initializing bot_num with front-end parameter  **/
        bot_num=request.getParameter("bot_num");
        /**Initializing temp_gioco_bot with front-end parameter  **/
        temp_gioco_bot=request.getParameter("temp_gioco_bot");
        /**Initializing temp_connessione with front-end parameter  **/
        temp_connessione=request.getParameter("temp_connessione");
        /**Initializing data_Start_game with front-end parameter  **/
        data_start_game=request.getParameter("data_start_game");
        /**Initializing temp_start_game with front-end parameter  **/
        temp_start_game=request.getParameter("temp_start_game");

        /**For loop to add in users bots emails
         * this will repeat for bot_num
         * **/
        for (int i=0;i<Integer.parseInt(bot_num);i++){
             users.add(request.getParameter("email_"+i));
        }

        /**Add in JSONObject temp_gioco_bot**/
        rules.put("time", temp_gioco_bot);
        /**Add in JSONObject bot_instance**/
        rules.put("bot_number", bot_instance);
        /**Add in JSONObject temp_connessione**/
        rules.put("connection_time", temp_connessione);
        /**Add in JSONObject data_start_game and temp_start_game**/
        rules.put("date", data_start_game + " " + temp_start_game);

        /**Send to the front-end a simple massage **/
        PrintWriter out = response.getWriter();
        out.write("Server Started\nWork in Progress!!!");

        /**
         * Initializing new  GUI_CLI_Run Object
         * @param String
         */
        new GUI_CLI_Run("GUI");

    }

    /**
     * Killing the servlet instance
     */
    public void destroy() {
    }
}
