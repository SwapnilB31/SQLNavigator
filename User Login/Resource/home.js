var user, email, accounts, mysql, mssql, oracle, mysqlDriver, mssqlDriver, oracleDriver;

function newAlert(message) {
	document.getElementById("message").innerHTML = message;
	document.getElementById("dialog").style.display = "block";
}

function mine(ele) {
	var a = ele.parentElement.getElementsByClassName('collapse')[0];
	var name = ele.title;
	if(a.classList.length == 1) {
		a.classList.add("open");
		ele.innerHTML = '<i class="fa fa-chevron-circle-up" aria-hidden="true"></i>';
		if(name == "mssql")
			document.getElementById('security-mssql').disabled = true;
		document.getElementById('enabled-'+name).disabled = true;
	}
	else if(a.classList.contains("close")) {
		a.classList.remove("close");
		a.classList.add("open");
		ele.innerHTML = '<i class="fa fa-chevron-circle-up" aria-hidden="true"></i>';
		if(name == "mssql")
			document.getElementById('security-mssql').disabled = true;
		document.getElementById('enabled-'+name).disabled = true;
	}
	else if(a.classList.contains("open")) {
		a.classList.remove("open");
		a.classList.add("close");
		ele.innerHTML = '<i class="fa fa-chevron-circle-down" aria-hidden="true"></i>';
		if(name == "mssql")
			document.getElementById('security-mssql').disabled = false;
		document.getElementById('enabled-'+name).disabled = false;
	}
}

function run() {
  user = Handler.user;
  document.title = user.username + " : Home";
  email = user.email;
  accounts = user.accounts;
  mysql = accounts.elementAt(0);
  mssql = accounts.elementAt(1);
  oracle = accounts.elementAt(2);
  mysqlDriver = mysql.driver;
  mssqlDriver = mssql.driver;
  oracleDriver = oracle.driver;
  /** MySQL **/
  document.getElementById('username-mysql').value = mysql.username;
  document.getElementById('password-mysql').value = mysql.password;
  document.getElementById('enabled-mysql').checked = mysqlDriver.enabled;
	if(!mysqlDriver.enabled) {
		document.getElementById('mysql-button').classList.remove('button');
		document.getElementById('mysql-button').classList.add('disable-button');
		document.getElementById('mysql-button').disabled = true;
	}
  document.getElementById('mysql-driver').value = mysqlDriver.Driver;
  document.getElementById('mysql-url').value = mysqlDriver.url.split(":")[2].slice(2);
	var p = mysqlDriver.url.split(":")[3].slice(0,-1);
  document.getElementById('mysql-port').value = p == "3306" ? "" : p;
  document.getElementById('mysql-file').value = mysqlDriver.file;
  /**       **/

  /**  MS SQL  **/
  document.getElementById('username-mssql').value = mssql.username;
  document.getElementById('password-mssql').value = mssql.password;
  document.getElementById('enabled-mssql').checked = mssqlDriver.enabled;
	document.getElementById('security-mssql').checked = mssqlDriver.windowsAuthentication();
	authentication(document.getElementById('security-mssql'));
	if(!mssqlDriver.enabled) {
		document.getElementById('mssql-button').classList.remove('button');
		document.getElementById('mssql-button').classList.add('disable-button');
		document.getElementById('mssql-button').disabled = true;
	}
  document.getElementById('mssql-driver').value = mssqlDriver.Driver;
  document.getElementById('mssql-url').value = mssqlDriver.url.split(":")[2].slice(2);
	var p1 = mssqlDriver.url.split(":")[3].split(";")[0];
  document.getElementById('mssql-port').value = p1 == "1443" ? "" : p1;
  document.getElementById('mssql-file').value = mssqlDriver.file;
  /**          **/

  /**  Oracle  **/
  document.getElementById('username-oracle').value = oracle.username;
  document.getElementById('password-oracle').value = oracle.password;
  document.getElementById('enabled-oracle').checked = oracleDriver.enabled;
	if(!oracleDriver.enabled) {
		document.getElementById('oracle-button').classList.remove('button');
		document.getElementById('oracle-button').classList.add('disable-button');
		document.getElementById('oracle-button').disabled = true;
	}
  document.getElementById('oracle-driver').value = oracleDriver.Driver;
  document.getElementById('oracle-url').value = oracleDriver.url.split(":")[3].split("@")[1];
	var p2 = oracleDriver.url.split(":")[4];
  document.getElementById('oracle-port').value = p2 == "1521" ? "" : p2;
  document.getElementById('oracle-file').value = oracleDriver.file;
  /**          **/
  document.getElementById('email').value = email;
}

function show (check) {
  var name = check.title;
  if(check.checked) {
    document.getElementById('password-'+name).type = "text";
  }
  else {
    document.getElementById('password-'+name).type = "password";
  }
}

function enable (check) {
	var name = check.title;
	var button = document.getElementById(name+"-button");
	if(check.checked) {
		button.classList.remove('disable-button');
		button.classList.add('button');
		button.disabled = false;
	}
	else {
		button.classList.remove('button');
		button.classList.add('disable-button');
		button.disabled = true;
	}
}

function authentication(check) {
	var name = check.title;
	var username = document.getElementById('username-'+name);
	var password = document.getElementById('password-'+name);
	if(check.checked) {
		enable(check);
		document.getElementById('enabled-'+name).checked = true;
		document.getElementById('enabled-'+name).disabled = true;
		document.getElementById(name+'-button').classList.remove('button');
		document.getElementById(name+'-button').classList.add('disable-button');
		document.getElementById(name+'-button').disabled = true;
		username.value = "";
		username.readOnly = true;
		password.value = "";
		password.readOnly = true;
	}
	else {
		username.readOnly = false;
		password.readOnly = false;
		document.getElementById('enabled-'+name).disabled = false;
		document.getElementById('enabled-'+name).checked = false;
		//document.getElementById(name+'-button').classList.add('button');
		username.value = mssql.username;
		password.value = mssql.password;
	}
}

function validate() {
	var correct = true;
	var errors = "<ol>";
	var mysqlEnabled = document.getElementById('enabled-mysql').checked;
	var mssqlEnabled = document.getElementById('enabled-mssql').checked;
	var oracleEnabled = document.getElementById('enabled-oracle').checked;
	var integratedSecurity = document.getElementById('security-mssql').checked;
	if(mysqlEnabled) {
		if(document.getElementById('username-mysql').value == "" && document.getElementById('password-mysql').value == "") {
			correct = false;
			errors += "<li>The Username and password for MySQL cannot be left empty, when the Driver is enabled</li>";
		}
		if(!isNum(document.getElementById('mysql-port').value)) {
			correct = false;
			errors += "<li>The Port number for the instance of MySQL has to be a number</li>";
		}
	}
	if(mssqlEnabled && !integratedSecurity) {
		if(document.getElementById('username-mssql').value == "" || document.getElementById('password-mssql').value == "") {
			correct = false;
			errors += "<li>The Username and/or password for Microsoft SQL Server cannot be left empty, when the Driver is enabled</li>";
		}
		if(!isNum(document.getElementById('mssql-port').value)) {
			correct = false;
			errors += "<li>The Port number for the instance of Microsoft SQL Server has to be a number</li>";
		}
	}
	if(oracleEnabled) {
		if(document.getElementById('username-oracle').value == "" || document.getElementById('password-oracle').value == "") {
			correct = false;
			errors += "<li>The Username and/or password for Oracle Database cannot be left empty, when the Driver is enabled</li>";
		}
		if(!isNum(document.getElementById('oracle-port').value)) {
			correct = false;
			errors += "<li>The Port number for the instance of Oracle Database has to be a number</li>";
		}
	}
	if(document.getElementById('email').value == "") {
		correct = false;
		errors += "<li>You must provide an email address for your account. It cannot be left empty</li>";
	}
	errors += "</ol>";
	if(!correct)
		newAlert("<div style = 'text-align : left; margin-left : 15px; margin-right : 15px;'>The data cannot be saved because :"+errors+"</div>");
	return correct;
}

function save() {
	if(validate() == true) {
		var change = false;
		/** MySQL **/
		if(document.getElementById('enabled-mysql').checked) {
			var port1 = document.getElementById('mysql-port').value;
			var url1 = "jdbc:mysql://"+document.getElementById('mysql-url').value+":"+(port1 == "" ? "3306" : port1)+"/";
			mysqlDriver.setData(document.getElementById('mysql-driver').value,url1,"MySQL");
			mysqlDriver.setFileInfo(document.getElementById('mysql-file').value);
			//newAlert(document.getElementById('username-mysql').value+document.getElementById('password-mysql').value);
			mysql.setData(document.getElementById('username-mysql').value,document.getElementById('password-mysql').value);
			change = true;
		}
		//newAlert("Here");
		/** Microsoft SQL Server**/
		if(document.getElementById('enabled-mssql').checked) {
			var port2 = document.getElementById('mssql-port').value;
			var url2 = "";
			if(document.getElementById('security-mssql').checked)
				url2 = "jdbc:sqlserver://localhost:1443;instance=SQLEXPRESS;databaseName=master;integratedSecurity=true";
			else
				url2 = "jdbc:sqlserver://"+document.getElementById('mssql-url').value+":"+(port2 == 0 ? "1443" : port2)+";instance=SQLEXPRESS;databaseName=master;user="+document.getElementById('username-mssql').value+";password="+document.getElementById('password-mssql').value+";";
				mssqlDriver.setData(document.getElementById('mssql-driver').value,url2,"Microsft SQL Server");
				mssqlDriver.setFileInfo(document.getElementById('mssql-file').value);
				mssql.setData(document.getElementById('username-mssql').value,document.getElementById('password-mssql').value);
				change = true;
			}
			/** Oracle Database**/
			if(document.getElementById('enabled-oracle').checked) {
				var port3 = document.getElementById('oracle-port').value;
				var url3 = "jdbc:oracle:thin:"+document.getElementById('username-oracle').value+"/"+document.getElementById('password-oracle').value+"@"+document.getElementById('oracle-url').value+":"+(port3 == 0 ? "1521" : port3)+":xe";
				oracleDriver.setData(document.getElementById('oracle-driver').value,url3,"Oracle");
				oracleDriver.setFileInfo(document.getElementById('oracle-file').value);
				oracle.setData(document.getElementById('username-oracle').value,document.getElementById('password-oracle').value);
				change = true;
			}
			/** Enabled Data for drivers **/
			user.setEmail(document.getElementById('email').value);
			mysqlDriver.setEnabled(document.getElementById('enabled-mysql').checked);
			mssqlDriver.setEnabled(document.getElementById('enabled-mssql').checked);
			mssqlDriver.setIntegratedSecurity(document.getElementById('security-mssql').checked);
			oracleDriver.setEnabled(document.getElementById('enabled-oracle').checked);
			/**                          **/
			var saved = Handler.saveUserInformation();
			if(change) {
				newAlert("Data has been saved successfully");
			}
			else {
				newAlert("Data for disabled drivers cannot be changed");
			}
			return saved;
	}
	return false;
}

function isNum(chunk) {
    for(var i in chunk)
      if(!(isNumeral(chunk[i]) || chunk[i] === '.'))
        return false;
      return true;
}

function isNumeral(ele) {
  return ele >= '0' && ele <= '9';
}

function logoutSave(button) {
	if(save()) {
		logout();
	}
	else {
		button.parentElement.parentElement.style.display = "none";
	}
}

function logout() {
	window.open("login.html");
}
