package org.lauchproject;

import org.json.simple.JSONObject;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/**
 * Get the rules for the tournament from the front-end
 * @author Ali
 * @version 4.0
 */
@WebServlet(name = "MyServlet",value ="/MyServlet")
public class My_servlet extends HttpServlet{
    private static ArrayList<String> users=new ArrayList<>();
    private static ArrayList<String> logs=new ArrayList<>();
    private static JSONObject rules = new JSONObject();
    private static String bot_num,temp_gioco_bot,temp_connessione,data_start_game,temp_start_game,bot_istance;

    /**
     * User Getter
     * @return ArrayList of Strings
     */
    public static ArrayList<String> getUsers() {
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
     * Bot Connectio time Getter
     * @return String
     */
    public static String getTemp_connessione() {
        return temp_connessione;
    }

    /**
     * Tournament starting time getter
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
     * Bot istance number(total matches of the tournament) Getter
     * @return
     */
    public static String getBot_istance() {
        return bot_istance;
    }

    /**
     * Post method to get Tournaments rules from the front-end request body
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        bot_istance=request.getParameter("bot_istance");
        bot_num=request.getParameter("bot_num");
        temp_gioco_bot=request.getParameter("temp_gioco_bot");
        temp_connessione=request.getParameter("temp_connessione");
        data_start_game=request.getParameter("data_start_game");
        temp_start_game=request.getParameter("temp_start_game");

        int i=0;

        for (i=0;i<Integer.parseInt(bot_num);i++){
             users.add(request.getParameter("email_"+i));
        }

        rules.put("time", temp_gioco_bot);
        rules.put("bot_number", bot_istance);
        rules.put("connection_time", temp_connessione);
        rules.put("date", data_start_game + " " + temp_start_game + ":00");

        PrintWriter out = response.getWriter();
        out.write("Server Started\nWork in Progress!!!");
        System.out.println(rules);
        System.out.println(users);
        System.out.println("TEMP GIOCO = "+temp_gioco_bot);
        System.out.println("temp conne = "+temp_connessione);
        System.out.println("bot istance = "+bot_istance);
        new GUI_CLI_Run("GUI");

    }

    /**
     * Killing the servlet istance
     */
    public void destroy() {
    }
}
