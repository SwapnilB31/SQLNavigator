var dbase;
var query = "";
var dbname = "";

function run() {
    var side_bar = document.getElementById('side-bar');
    var dblist = DB.dblist;
    var html = side_bar.innerHTML;
    for(var i = 0; i < dblist.size(); i++) {
        html += "<details><summary summary class = 'summary'>"+dblist.get(i).database+"</summary>";
        html += "<span onclick = 'deleteDB(this)' class = 'table-opt' style = 'margin-left : 12px;'><i class='fa fa-trash' aria-hidden='true'></i> Delete</span>";
        html += "<span onclick = 'showDBInfo(this)' class = 'table-opt' style = 'margin-left : 12px;'><i class='fa fa-info-circle' aria-hidden='true'></i> Information</span>";
        html += "</details>";
    }
    side_bar.innerHTML = html;
}

function showDBInfo(span) {
    var body = document.getElementById('body');
    var db = span.parentElement.getElementsByTagName('summary')[0].innerHTML;
    var html = "<div><table class = 'dbinfo'><tr><td>Name : </td><td>"+db+"</td></tr>";
    var info = SQL.giveDBInfo(db);
    html += "<tr><td>CHARACTER_SET_NAME : </span></td><td>"+info.get(0)+"</td></tr>";
    html += "<tr><td>COLLATION_NAME : </span></td><td>"+info.get(1)+"</td></tr>";
    body.innerHTML = html;
}

function addDB() {
    var body = document.getElementById('body');
    var html = "<div align = 'center' class = 'dbinfo' style = 'color : darkslategrey !important'>";
    html += "<table  align = 'center'><tr><td>Create Database  </td><td><input type = 'text' id = 'db-name'></td></tr>";
    var select1 = "<select id = 'def-charset'><option value = 'utf8'>utf8</option>";
    var charSets = SQL.giveCharSets();
    for(var i = 0; i < charSets.size(); i++) {
        if(charSets.get(i) == 'utf8') {

        }
        else {
            select1 += "<option value = '"+charSets.get(i)+"'>"+charSets.get(i)+"</option>";
        }
    }
    select1 += "</select>";
    html += "<tr><td>with character set </td><td>"+select1+"</td></tr>";
    var select2 = "<select id = 'def-collate'><option value = 'utf8_general_ci'>utf8_general_ci</option>";
    var collate = SQL.giveCollate();
    for(var i = 0; i < collate.size(); i++) {
        if(charSets.get(i) == 'utf8') {

        }
        else {
            select2 += "<option value = '"+collate.get(i)+"'>"+collate.get(i)+"</option>";
        }
    }
    select2 += "</select>";
    html += "<tr><td>and collation </td><td>"+select2+"</td></tr></table>";
    html += "<div align = 'center' style = 'margin-top : 8px;'><button class = 'dia-button' onclick = 'createDatabase()'>Create Database</button></div></div>";
    body.innerHTML = html;
}

function createDatabase() {
    var name = document.getElementById('db-name').value;
    var charset = document.getElementById('def-charset').value;
    var collate = document.getElementById('def-collate').value;
    if(!isIllegal(name)) {
        newAlert("Illegal Name for the table. Table Name cannot contain ',', ; , : , ? , . , ' , \", \\ , / , ~ and `.");
    }
    else if(allEmpty(name)) {
            newAlert("Database's name cannot be empty!");
    }
    else {
        dbname = name;
        var qry = 'CREATE DATABASE `'+name+"` DEFAULT CHARACTER SET "+charset+" DEFAULT COLLATE "+collate+";";
        query = qry;
        document.getElementById('message1').innerHTML = "The following query will create a new database '"+name+"'. Do you want to execute it?";
        document.getElementById('query').innerHTML = qry;
        document.getElementById('query').setAttribute("data-queryType","add db");
        var pre = document.getElementById('query');
        hljs.highlightBlock(pre);
        document.getElementById('dialog1').style.display = 'block';
    }
}

function deleteDB(span) {
    var db = span.parentElement.getElementsByTagName('summary')[0].innerHTML;
    dbase = span.parentElement;
    var qry = 'DROP DATABASE `'+db+"`;";
    query = qry;
    document.getElementById('message1').innerHTML = "The following query will delete the database '"+db+"'. Do you want to execute it?";
    document.getElementById('query').innerHTML = qry;
    document.getElementById('query').setAttribute("data-queryType","drop db");
    var pre = document.getElementById('query');
    hljs.highlightBlock(pre);
    document.getElementById('dialog1').style.display = 'block';
}

function newAlert(message) {
	document.getElementById("message").innerHTML = message;
	document.getElementById("dialog").style.display = "block";
}

function drop() {
    document.getElementById('dialog1').style.display = 'none';
    var success = false;
    var intent = document.getElementById('query').getAttribute("data-queryType");
    try {
        success = SQL.execQuery(query);
    } catch(e) {
        enableAll();
    }
    if(success) {
        switch(intent) {
            case "drop db":
                var side_bar = document.getElementById('side-bar');
                side_bar.removeChild(dbase);
                newAlert("The database was successfully deleted");
                document.getElementById('body').innerHTML = "";
                break;
            case "add db":
                var side_bar = document.getElementById('side-bar');
                var html = side_bar.innerHTML;
                newAlert("The database was successfully created");
                html += '<details><summary class = "summary">'+dbname+"</summary>"; 
                html += "<span onclick = 'deleteDB(this)' class = 'table-opt' style = 'margin-left : 12px;'><i class='fa fa-trash' aria-hidden='true'></i> Delete</span>";
                html += "<span onclick = 'showDBInfo(this)' class = 'table-opt' style = 'margin-left : 12px;'><i class='fa fa-info-circle' aria-hidden='true'></i> Information</span>";
                html += "</details>";
                side_bar.innerHTML = html;
                document.getElementById('body').innerHTML = "";
                break;
        }
    }
}

/** Type checking section */
var illegal = [",",";",":","?",".","'","\\","/","~","`"];

function isIllegal(name) {
    for(var j = 0; j < illegal.length; j++)
	    if(name.indexOf(illegal[j]) > -1) {
            console.log(name+".indexOf("+illegal[j]+") = "+(name.indexOf(illegal[j])));
			return false;
		}
    return true;
}

function allEmpty(str) {
    var empty = true;
    for(var i = 0; i < str.length; i++) 
        if(str[i] != " " && str[i] != "\n") {
            empty = false;
            break;
        }
    return empty;
}
/** End                   */

