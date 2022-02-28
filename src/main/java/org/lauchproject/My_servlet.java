package org.lauchproject;

import org.json.simple.JSONObject;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.http.*;
import javax.servlet.annotation.*;


@WebServlet(name = "MyServlet",value ="/MyServlet")
public class My_servlet extends HttpServlet{
    private static ArrayList<String> logs=new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        String bot_num=request.getParameter("bot_num");
        String temp_gioco_bot=request.getParameter("temp_gioco_bot");
        String temp_connessione=request.getParameter("temp_connessione");
        String data_start_game=request.getParameter("data_start_game");
        String temp_start_game=request.getParameter("temp_start_game");

        int i=0;
        ArrayList<String> users=new ArrayList<>();
        for (i=0;i<Integer.parseInt(bot_num);i++){
             users.add(request.getParameter("email_"+i));
        }

        JSONObject rules = new JSONObject();
            rules.put("time", temp_gioco_bot);
            rules.put("bot_number", bot_num);
            rules.put("connection_time", temp_connessione);
            rules.put("date", data_start_game + " " + temp_start_game + ":00");


        new GameSettings(rules,users);

        PrintWriter out = response.getWriter();
        for (int j=0;j< logs.size();j++){
            out.write(logs.get(j));
        }


    }

    public static void getlog(String log){
        logs.add(log);
    }


    public void destroy() {
    }
}
