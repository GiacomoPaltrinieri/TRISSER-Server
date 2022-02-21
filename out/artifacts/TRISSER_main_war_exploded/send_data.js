
function take_values(){

   /* var bot_num=document.forms["myform"] ["bot_num"].value;
    var email_1=document.forms["myform"] ["email_1"].value;
    var email_2=document.forms["myform"] ["email_2"].value;
    var email_3=document.forms["myform"] ["email_3"].value;
    var temp_gioco_bot=,documdocument.forms["myform"] ["temp_gioco_bot"].value;*/

    var http = new XMLHttpRequest();

    http.open("POST", "http://localhost:8080/TRISSER_main_war_exploded/MyServlet", true);
    http.setRequestHeader("Content-type","application/x-www-form-urlencoded");

    http.onerror = function() { // only triggers if the request couldn't be made at all
        alert(`Network Error`);
    };

    http.send("num_bot_p",document.forms["myform"] ["bot_num"].value,
        "email_1_P",document.forms["myform"] ["email_1"].value,
        "email_2_p",document.forms["myform"] ["email_2"].value,
        "email_3_p",document.forms["myform"] ["email_3"].value,
        "temp_gioco_bot_p",document.forms["myform"] ["temp_gioco_bot"].value,
        "temp_connessione_p",document.forms["myform"] ["temp_connessione"].value,
        "data_start_game_p",document.forms["myform"] ["data_start_game"].value,
        "temp_start_game_p",document.forms["myform"] ["temp_start_game"].value);

    return false;

}