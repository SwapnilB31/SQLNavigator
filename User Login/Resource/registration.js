function nameExists(username) {
  return Handler.checkDuplicate(username);
}

function areTheySame(password,confirmPassword) {
  return password == confirmPassword;
}

function run() {
  return true;
}

function register() {
  var username = document.getElementById('username').value;
  var password = document.getElementById('password').value;
  if(validate()) {
    if(Handler.Registration(username,password)) {
      window.setTimeout(Go,1000);
    }
    else {
      newAlert("Sorry, the registration failed due to some technical problems!");
    }
  }
}

function Go() {
  window.open("../home.html");
}

function validate() {
  var errors = "<ol>";
  var correct = true;
  if(nameExists(document.getElementById('username').value)) {
    correct = false;
    errors += "<li>The username '"+document.getElementById('username').value+"' has already been taken.</li>";
  }
  if(document.getElementById('username').value == "") {
    correct = false;
    errors += "<li>Your username cannot be \"\". Empty usernames are not allowed!</li>";
  }
  if(!areTheySame(document.getElementById('password').value,document.getElementById('confPassword').value)) {
    correct = false;
    errors += "<li>The password field and the confirm password field has different values</li>";
  }
  if(document.getElementById('password').value == "") {
    correct = false;
    errors += "<li>Your password cannot be \"\". Empty passwords are not allowed</li>";
  }
  errors += "</ol>";
  if(!correct)
    newAlert("<div style = 'text-align : left; margin-left : 15px; margin-right : 15px;'>The user cannot be created because :"+errors+"</div>");
  return correct;
}

function newAlert(message) {
	document.getElementById("message").innerHTML = message;
	document.getElementById("dialog").style.display = "block";
}
