<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Trisser</title>
        <link rel="stylesheet" type="text/css" href="home_page_style.css">
    </head>
    <body>
    <h1 id="titolo"> TRISSER </h1>
    <br>
    <div id="mybody">
        <form id="myform"  method="post" onsubmit="return take_values()">
            <h2>Set Rules</h2>
            <div id="alfa_cont">
                <div class="container" >

                    <input type="number" placeholder="NUMERO ISTANZE BOT" name="bot_istance" class="myin"  required>
                    <input type="number" placeholder="NUMERO BOT" name="bot_num" class="myin" onchange="create_input()" required>

                    <div id="input_cont">
                        <input type="email" placeholder="EMAIL" name='email_1' class="myin" >
                        <input type="email" placeholder="EMAIL" name='email_2' class="myin" >
                        <input type="email" placeholder="EMAIL" name='email_3' class="myin" >
                        <!-- INPUT TAG IS ADDED BY THE JS SCRIPT-->
                    </div>
                </div>

                <div class="container">
                    <label >Tempo di gioco a disposizione del bot:</label>
                    <input type="number" name="temp_gioco_bot" value="20">
                    <label >Tempo di connessione:</label>
                    <input type="number" name="temp_connessione" value="20">
                    <label >Data inizio gioco:</label>
                    <input type="date" placeholder="DATA" name="data_start_game" required>
                    <label >Orario inizio gioco:</label>
                    <input type="time" placeholder="TIME" step="1" name="temp_start_game" required>
                </div>
            </div>

            <button type="submit">Submit</button>
        </form>

        <div id="serverLog">
            <textarea name="log" id="log" cols="80" rows="20"></textarea>
        </div>
    </div>

    <script src="send_data.js">
    </script>
    </body>
</html>