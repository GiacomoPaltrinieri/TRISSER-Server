package org.lauchproject;

import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "MyServlet",value ="/MyServlet")
public class My_servlet extends HttpServlet{

    @Override
    protected void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        // Hello
        //test commit ali not working github
        PrintWriter out = response.getWriter();
        out.println("<h3>Rule selected!</h3>");


        String bot_num=request.getParameter("bot_num_p");
        System.out.println(bot_num);
        String bot_num_3=request.getParameter("bot_num");
        System.out.println(bot_num_3);
        String email_1=request.getParameter("email_1_p");
        String email_2=request.getParameter("email_2_P");
        String email_3=request.getParameter("email_3_P");
        String temp_gioco_bot=request.getParameter("temp_gioco_bot_p");
        String temp_connessione=request.getParameter("temp_connessione_p");
        String data_start_game=request.getParameter("data_start_game_p");
        String temp_start_game=request.getParameter("temp_start_game_p");



        JSONObject rules = new JSONObject();
            rules.put("time", temp_gioco_bot);
            rules.put("bot_number", bot_num);
            rules.put("connection_time", temp_connessione);
            rules.put("date", data_start_game + " " + temp_start_game + ":00");

        ArrayList<String> users=new ArrayList<>();
        users.add(email_1);
        users.add(email_2);
        users.add(email_3);

        System.out.println(rules);
        System.out.println(users);

        //new GameSettings(rules,users);

        out.println("<h2>Dati ricevuti</h2>");
        out.println("<h2>HY GREG</h2>");

    }

    public void destroy() {
    }
}
