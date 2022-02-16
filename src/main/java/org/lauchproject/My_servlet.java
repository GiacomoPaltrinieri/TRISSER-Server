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
    protected void  doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        // Hello
        PrintWriter out = response.getWriter();
        out.println("<h3>Rule selected!</h3>");

        String data=request.getParameter("data");
        String time=request.getParameter("time");
        String bot_num=request.getParameter("bot_num");
        String email_1=request.getParameter("email_1");
        String email_2=request.getParameter("email_2");
        String email_3=request.getParameter("email_3");

        JSONObject rules = new JSONObject();
            rules.put("time", 20);
            rules.put("bot_number", bot_num);
            rules.put("connection_time", 20);
            rules.put("date", data+time+":00");

        ArrayList<String> users=new ArrayList<>();
        users.add(email_1);
        users.add(email_2);
        users.add(email_3);

        new GameSettings(rules,users);

    }

    public void destroy() {
    }
}
