/**
 * Function for Sending data to MyServlet whitout Reloading page
 * create a post request(XMLHttpRequest) for My_servlet and send Rules in the body
 * @author Ali
 * @version 1.0
 * @returns {boolean}
 */
function take_values(){

    var data="bot_istance="+encodeURIComponent(document.forms["myform"] ["bot_istance"].value)+
        "&bot_num="+encodeURIComponent(document.forms["myform"]["bot_num"].value)+
        "&temp_gioco_bot="+encodeURIComponent(document.forms["myform"] ["temp_gioco_bot"].value)+
        "&temp_connessione="+encodeURIComponent(document.forms["myform"] ["temp_connessione"].value)+
        "&data_start_game="+encodeURIComponent(document.forms["myform"] ["data_start_game"].value)+
        "&temp_start_game="+encodeURIComponent(document.forms["myform"] ["temp_start_game"].value);

    for (i=0;i<document.forms["myform"]["bot_num"].value;i++){
        data=data+"&email_"+i+"="+encodeURIComponent(document.forms["myform"] ["email_"+i].value);
    }

    var http = new XMLHttpRequest();
    /**
     * Get My_servlet responce back
     * add the responce to the rule setting page(index.jsp)
     */
    http.onreadystatechange = function() {
        if (http.readyState === 4) {
            var callback=(http.response);
            var textarea=document.getElementById("log");
            textarea.innerHTML="";
            console.log(callback)
            textarea.innerHTML=textarea.innerHTML+callback;
        }
    }

    http.open("POST", "http://localhost:8080/TRISSER_main_war_exploded/MyServlet", true);
    http.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    http.send(data);

    return false;

}

/**
 *Function to create Multiple input tags for the email input
 * the tag number is determinated according to the bot numbers
 */
function create_input(){
    var cont=document.forms["myform"]["bot_num"].value;
    var container=document.getElementById("input_cont")
    container.innerHTML="";
    var name="email_";
    for (i=0;i<cont;i++){
        container.innerHTML=container.innerHTML+ '<input type="email" placeholder="EMAIL" name='+name+i+' class="myin" required>';
    }
}
