<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Trisser</title>
        <link rel="stylesheet" type="text/css" href="home_page_style.css">
    </head>
    <body>
        <h1 id="titolo"> TRISSER </h1>
        <br/>
        <form action="/MyServlet" method="post" id="my_form">
            <input type="date" placeholder="DATA" name="data">
            <input type="time" placeholder="TIME" name="time">
            <input type="number" placeholder="NUMERO BOT" name="bot_num">
            <input type="email" placeholder="EMAIL" name="email_1">
            <input type="email" placeholder="EMAIL" name="email_2">
            <input type="email" placeholder="EMAIL" name="email_3">

            <button type="submit">Submit</button>
        </form>
    </body>
</html>