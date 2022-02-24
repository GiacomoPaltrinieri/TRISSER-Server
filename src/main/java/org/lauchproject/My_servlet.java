package org.lauchproject;

import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "MyServlet",value ="/MyServlet")
//@MultipartConfig
public class My_servlet extends HttpServlet{

    @Override
    protected void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        // Hello
        //test commit ali not working github


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


        System.out.println(rules);
        System.out.println(users);

        /**new GameSettings(rules,users);**/

        //request.setAttribute("logs","THIS WILL BE A LOG");

        PrintWriter out = response.getWriter();
        out.write("this is a log");

       // out.println("<h2>Dati ricevuti</h2>");
        //out.println("<h2>HY GREG</h2>");

    }


    public void destroy() {
    }
}
