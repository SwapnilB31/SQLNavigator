function loginKey(event) {if(event.keyCode == 13) login();}

function login() {
  var username = document.getElementById('username').value;
  var password = document.getElementById('password').value;
  var success = Handler.Login(username,password);
  var out = document.getElementById('t');
  if(success) {
    newAlert("Success");
    window.setTimeout(Go,1000);
    //test.load(test.lurl+"SQLNavigator/UserLogin/home.html");
   //window.setTimeout(Go,1000);
  }
  else {
    newAlert("The Username and Password combination does not exist");
    document.getElementById('username').value = "";
    document.getElementById('password').value = "";
  }
}

function Go() {
  window.open("home.html");
}

function run() {
  return "Gooey Booey Chika laka chucha";
}

function newAlert(message) {
	document.getElementById("message").innerHTML = message;
	document.getElementById("dialog").style.display = "block";
}
