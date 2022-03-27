/**
 * Function for Sending data to MyServlet whitout Reloading page
 * create a post request(XMLHttpRequest) for My_servlet and send Rules in the body
 * @author Ali
 * @version 1.0
 * @returns {boolean}
 */
function take_values(){
    /**
     * Initialaizing var data with values from the html form(myform)
     * @type {string}
     */
    var data="bot_istance="+encodeURIComponent(document.forms["myform"] ["bot_istance"].value)+
        "&bot_num="+encodeURIComponent(document.forms["myform"]["bot_num"].value)+
        "&temp_gioco_bot="+encodeURIComponent(document.forms["myform"] ["temp_gioco_bot"].value)+
        "&temp_connessione="+encodeURIComponent(document.forms["myform"] ["temp_connessione"].value)+
        "&data_start_game="+encodeURIComponent(document.forms["myform"] ["data_start_game"].value)+
        "&temp_start_game="+encodeURIComponent(document.forms["myform"] ["temp_start_game"].value);

    /**
     * For loop to get Dynamically emails and add them to data
     * this will repeat for bot_num
     */
    for (i=0;i<document.forms["myform"]["bot_num"].value;i++){
        data=data+"&email_"+i+"="+encodeURIComponent(document.forms["myform"] ["email_"+i].value);
    }
    /**Creating a new XMLHttpRequest Object in var "http"**/
    var http = new XMLHttpRequest();

    /**
     * Get My_servlet responce back
     * add the responce to the rule setting page(index.jsp)
     * this was supposed to print server logs on the setting page
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

    /**Open a new asynchronous post request to MyServlet**/
    http.open("POST", "http://localhost:8080/TRISSER_main_war_exploded/MyServlet", true);
    /**set the content-type of the request**/
    http.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    /**send the rules(data) throw the request created before**/
    http.send(data);
    /**Returning false allow the page to not reload**/
    return false;

}

/**
 *Function to create Multiple input tags for the email input
 * the tag number is determinated according to the bot numbers
 */
function create_input(){
    /**Get the number of the total bots playing in the tournament **/
    var cont=document.forms["myform"]["bot_num"].value;
    /**Assign to container the name of the container that willi contain the input tags **/
    var container=document.getElementById("input_cont")
    /**Initializing container to empty**/
    container.innerHTML="";
    /**Creating a variable that will contain the name of the tag generated dynamically**/
    var name="email_";
    /**
     * For loop to generate input tags
     * this will repeat for bot_num(cont)
     * **/
    for (i=0;i<cont;i++){
        container.innerHTML=container.innerHTML+ '<input type="email" placeholder="EMAIL" name='+name+i+' class="myin" required>';
    }
}
