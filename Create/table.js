var dbase = "";
var dbnode;
var table = "";
var query = "";
var curTR;
var tabNode;
var lastVal = "";
var newVal = "";
var editable = {};
var editId = "";
var lastLens = [];
var empty;
var checkBox;
var selectOption;
var changeCount = 0;
var tabData;
var position = "";
var newTable = "";

function run() {
    var databaseList = DB.dblist;
    var side_bar = document.getElementById("side-bar");
    var html = side_bar.innerHTML;
    for(var i = 0; i < databaseList.size(); i++) {
        var tree = '<details data-index = "'+i+'" style = " background: -webkit-linear-gradient(top, #FAFAFA, #E1E1E1); !important"><summary class = "summary" style = "text-align : left !important; padding-left : 12px;" onclick = "selectDB(this)">';
        tree += databaseList.get(i).database+"</summary>";
        var tables = databaseList.get(i).tables;
        if(tables.size() == 1 && tables.get(0).toString() == "No Tables") {
            tree += "";
        }
        else {
            for(var j = 0; j < tables.size(); j++) {
                tree += '<span data-i = "'+j+'"class = "span"style = "padding-left : 12px; text-align : left !important;" onclick="createDesc(this)"><i class="fa fa-arrow-circle-right" aria-hidden="true"></i> '+tables.get(j)+'</span>';
            }
        }
        tree += "</details>";
        html += tree;
    }
    side_bar.innerHTML = html;
    document.getElementsByClassName('db-in-use')[0].innerHTML = "The database '"+databaseList.get(0).database+"'  is in use";
    setInitDB();
}

function selectDB(summary) {
    var db = summary.innerHTML;
    dbnode = summary;
    var success = SQL.use_db(db);
    if(success) {
        document.getElementsByClassName('db-in-use')[0].innerHTML = "The database '"+db+"'  is in use";
        dbase = db;
    }
    else {
        newAlert("The execution of the sql query failed due to some error! The database could not be selected for use");
    }
}

function setInitDB() {
    var side_bar = document.getElementById('side-bar');
    var details = side_bar.getElementsByTagName('details');
    dbnode = details[0].getElementsByTagName('summary')[0];
    //newAlert(details[0].getElementsByTagName('summary')[0].innerHTML);
}

function createDesc(span) {
    tabNode = span;
    var body = document.getElementById('body');
    var x = tabNode.parentElement.getAttribute("data-index");
    var y = tabNode.getAttribute('data-i');
    body.setAttribute("data-tab",x+","+y);
    var name = span.innerHTML.slice(60);
    //newAlert(name);
    var thisDB = span.parentElement.getElementsByTagName('summary')[0].innerHTML;
     var select = "<optgroup label=\"Numeric\"><option value = \"tinyint\">TINYINT</option>"
                    + "<option value = \"smallint\">SMALLINT</option><option value = \"mediumint\">MEDIUMINT</option><option value = \"int\">INT</option>"
                    + "<option value = \"bigint\">BIGINT</option><option value = \"float\">FLOAT</option><option value = \"numeric\">NUMERIC</option><option value = \"double\">DOUBLE</option>"
                    + "<option value = \"bit\">BIT</option><option value = \"boolean\">BOOLEAN</option></optgroup><optgroup label=\"Date and time\">"
                    + "<option value = \"date\">DATE</option><option value = \"datetime\">DATETIME</option><option value = \"timestamp\">TIMESTAMP</option>"
                    + "<option value = \"time\">TIME</option><option value = \"year\">YEAR</option></optgroup><optgroup label=\"String\"><option value = \"char\">CHAR</option>"
                    + "<option value = \"varchar\">VARCHAR</option><option value = \"text\">TEXT</option><option value = \"longtext\">LONGTEXT</option><option value = \"blob\">BLOB</option><option value = \"longblob\">LONGBLOB</option></optgroup>";
    var uni = "<option value = 'unique'>UNIQUE</option>"
                + "<option value = 'primary key'>PRIMARY KEY</option>";
    if(thisDB != dbase) {
        newAlert("The database '"+thisDB+"' is not currently in use. Click on the name '"+thisDB+"' from the database tree and then click on the table '"+name+"' to view its structure");
    }
    else {
        var TableData = SQL.desc_table(name);
        tabData = TableData;
        var html = "<span class = 'span' style = 'color : darkslategrey; font-family : \"Lobster\",\"Serif\"; !important; font-size : 22px; -webkit-text-stroke : 0.5px'>Structure : '"+name+"'</span><table class = 'table' id = 'struct-table'><tr class = \"b\"><th>Field</th><th>Type</th><th>Length</th><th>Null</th><th>Key</th><th>Options</th></tr>"; 
        for(var i = 0; i < TableData.size(); i++) {
            var attr = TableData.get(i);
            html += "<tr class = 'tr' data-i='"+i+"'><td class = 'td editable' id = '"+i+","+"field' ondblclick = 'toInput(this,\"field\")' style = 'width : 190px !important; text-align : center;'>";
            html += attr.Field+"</td>";
            var type = attr.Type.indexOf(")") > -1 ? attr.Type.split("(")[0] : attr.Type;
            var len = attr.Type.indexOf(")") > -1 ? attr.Type.split("(")[1].split(")")[0] : "";
            html += "<td class = 'td'><select onchange = 'changeDataType(this)'><option value = '"+type+"'>"+type.toLocaleUpperCase()+"</option>"+select+"</select></td>";
            html += "<td class = 'td editable' id = '"+i+","+"len' ondblclick = 'toInput(this,\"length\")' style = 'width : 190px !important; text-align : center;'>"+len+"</td>";
            html += "<td class = 'td'><input type = 'checkbox' onclick = 'AlterNull(this)' id = '"+i+",check'"+(attr.Null == "YES" ? "checked = 'checked'" : "")+"/></td>";
            html += "<td class = 'td'><select onchange = 'AlterKey(this)'>"+(attr.Key == "" || attr.Key == "MUL" ? "<option value = ''>NONE</option>" + uni : (attr.Key == "PRI" ? "<option value = 'primary key'>PRIMARY KEY</option><option value = 'unique'>UNIQUE</option>" : uni) + "<option value = ''>NONE</option>")+"</select></td>";
            html += "<td class = 'td del' onclick = 'queryBuilder(this,\"drop col\")'> <i class='fa fa-trash' aria-hidden='true'></i> Delete</td>"
            html += "</tr>";
        }
        html += "</table>";
        html += "<div style = 'text-align : center; margin : 15px;' id = 'tableOptions'><div class = 'table-opt' onclick = 'queryBuilder(this,\"drop tab\")'><i class='fa fa-trash' aria-hidden='true'></i> Drop Table</div><div class = 'table-opt' onclick = 'addColumns()'><i class='fa fa-plus' aria-hidden='true'></i> Add Columns</div></div>";
        body.innerHTML = html;
        addToEditable();
        table = name;
    }
}

function createNewTableUI() {
    var body = document.getElementById('body');
    body.innerHTML = '<div id = "newTabDia" class = "new-tab-dia"><span style = "margin : 3px">Create Table</span> <input type = \'text\' class = \'in\'><span style = "margin : 3px"> having </span><input type = \'text\' class = \'in\' style = \'width : 90px;\'> <span style = "margin : 3px"> columns </span><button class = "createTabButton" onclick = "createTableTab()">Go</button></div>';
}

function createTableTab() {
    var dia = document.getElementById('newTabDia');
    var inp = dia.getElementsByTagName('input');
    var name = inp[0].value;
    var col = inp[1].value;
    var colLen;
    var select = "<select><option value = '' disabled selected>Select a Data Type</option><optgroup label=\"Numeric\"><option value = \"tinyint\">TINYINT</option>"
                    + "<option value = \"smallint\">SMALLINT</option><option value = \"mediumint\">MEDIUMINT</option><option value = \"int\">INT</option>"
                    + "<option value = \"bigint\">BIGINT</option><option value = \"float\">FLOAT</option><option value = \"numeric\">NUMERIC</option><option value = \"double\">DOUBLE</option>"
                    + "<option value = \"bit\">BIT</option><option value = \"boolean\">BOOLEAN</option></optgroup><optgroup label=\"Date and time\">"
                    + "<option value = \"date\">DATE</option><option value = \"datetime\">DATETIME</option><option value = \"timestamp\">TIMESTAMP</option>"
                    + "<option value = \"time\">TIME</option><option value = \"year\">YEAR</option></optgroup><optgroup label=\"String\"><option value = \"char\">CHAR</option>"
                    + "<option value = \"varchar\">VARCHAR</option><option value = \"text\">TEXT</option><option value = \"longtext\">LONGTEXT</option><option value = \"blob\">BLOB</option><option value = \"longblob\">LONGBLOB</option></optgroup></select>";
    var uni = "<select onchange = 'NotNull(this)'><option value = ''>NONE</option><option value = 'UNIQUE'>UNIQUE</option><option value = 'PRIMARY KEY'>PRIMARY KEY</option></select>";
        var details = dbnode.parentElement;
        var index = details.getAttribute("data-index");

        if(DB.dblist.get(index).tables.contains(name)){
            newAlert("The database '"+dbase+"' already has a table with the name '"+name+"'!");
        }
        else if(name.indexOf(" ") > -1) {
            newAlert("Field name cannot have spaces in them");
        }
        else if(!isIllegal(name)) {
            newAlert("Illegal Name for the table. Table Name cannot contain ',', ; , : , ? , . , ' , \", \\ , / , ~ and `.");
        }
        else if(allEmpty(name)) {
            newAlert("Table name cannot be empty!");
        }
        else if(isNaN(parseInt(col))) {
            newAlert("The table '"+name+"' cannot have '"+col+"' columns!");
        }
        else {
            colLen = parseInt(col);
            var body = document.getElementById('body');
            var html = "";
            html += "<table id = 'newTab' class = 'table'><tr><th class = 'b'>Field</th class = 'b'><th class = 'b'>Type</th><th class = 'b'>Length</th><th class = 'b'>Null</th><th class = 'b'>Key</th></tr>";
            for(var i = 0; i < colLen; i++) {
                html += "<tr class = 'tr'>";
                html += "<td class = 'td'><input type = 'text'/></td>";
                html += "<td class = 'td'>"+select+"</td>";
                html += "<td class = 'td'><input type = 'text'/></td>";
                html += "<td class = 'td'><input type = 'checkbox'/></td>";
                html += "<td class = 'td'>"+uni+"</td>";
                html += "</tr>";
            }
            html += "</table>";
            html += "<div style = 'text-align : center; margin : 15px;'><button class = 'butt' onclick = 'createTable(\""+name+"\")'>Create Table</button></div>";
            body.innerHTML = html;
        }
}

function createTable(name){
    var tab = document.getElementById('newTab');
    if(validate(tab)) {
        var tr = tab.getElementsByTagName('tr');
        var qry = "CREATE TABLE `"+name+"`(";
        for(var i = 1; i < tr.length - 1; i++) {
            var td = tr[i].getElementsByTagName('td');
            console.log(td);
            var field = td[0].getElementsByTagName('input')[0].value;
            var type = td[1].getElementsByTagName('select')[0].value;
            var len = td[2].getElementsByTagName('input')[0].value;
            var Null = td[3].getElementsByTagName('input')[0].checked == true? "" : " NOT NULL";
            var key = td[4].getElementsByTagName('select')[0].value;

            qry += "`"+field+"` "+type+(len == "" ? "" : "("+len+")")+(key == "" ? "" : " "+key)+Null+",";
        }
        var td = tr[tr.length-1].getElementsByTagName('td');
        var field = td[0].getElementsByTagName('input')[0].value;
        var type = td[1].getElementsByTagName('select')[0].value;
        var len = td[2].getElementsByTagName('input')[0].value;
        var Null = td[3].getElementsByTagName('input')[0].checked == true? "" : " NOT NULL";
        var key = td[4].getElementsByTagName('select')[0].value;

        qry += "`"+field+"` "+type+(len == "" ? "" : "("+len+")")+(key == "" ? "" : " "+key)+Null+");";

        document.getElementById('message1').innerHTML = "The following query will create a new table '"+name+"' in the database '"+dbase+"'. Do you want to execute it?";
        document.getElementById('query').innerHTML = qry;
        query = qry;
        document.getElementById('query').setAttribute("data-queryType","create table");
        var pre = document.getElementById('query');
        hljs.highlightBlock(pre);
        document.getElementById('dialog1').style.display = 'block';
        disableAll("dialog1");
        newTable = name;

    }
}

function NotNull(select) {
	if(select.value == "primary key") {
		tr = select.parentElement.parentElement;
		td = tr.getElementsByTagName('td');
		checkbox = td[3].getElementsByTagName('input')[0];
		checkbox.checked = false;
	}
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
            case "drop col":
               curTR.style.display = 'none';
               newAlert("The query has been successfully executed");
               break;
            case "drop tab":
                document.getElementById('body').innerHTML = "";
                var details = tabNode.parentElement;
                details.removeChild(tabNode);
                var index = parseInt(details.getAttribute("data-index"));
                var tabIndex = parseInt(tabNode.getAttribute("data-i"));
                var succ = DB.dblist.get(index).tables.remove(tabIndex);
                newAlert("The query has been successfully executed");
                break;
            case "alter field":
                document.getElementById(editId).innerHTML = newVal;
                enableAll();
                makeAllEditable();
                newAlert("The query has been successfully executed");
                break;
            case "alter length":
                document.getElementById(editId).innerHTML = lastLens.length == 2 ? lastLens[0]+","+lastLens[1] : lastLens[0];
                enableAll();
                makeAllEditable();
                newAlert("The query has been successfully executed");
                break;
            case "alter null":
                enableAll();
                //makeAllEditable();
                newAlert("The query has been successfully executed");
                break;
            case "drop key":
            case "add key":
                enableAll();
                newAlert("The query has been successfully executed");
                createDesc(tabNode);
                break;
            case "alter type":
                enableAll();
                makeAllEditable();
                newAlert("The query has been successfully executed");
                createDesc(tabNode);
                changeCount = 0;
                break;
            case "add column":
                enableAll();
                newAlert("The query was successfully executed");
                createDesc(tabNode);
            case "create table":
                enableAll();
                var details = dbnode.parentElement;
                var i = details.getAttribute("data-index");
                console.log(i);
                DB.dblist.get(i).tables.add(newTable);
                var len = details.getElementsByTagName('span').length;
                details.innerHTML += '<span data-i = "'+len+'"class = "span"style = "padding-left : 12px; text-align : left !important;" onclick="createDesc(this)"><i class="fa fa-arrow-circle-right" aria-hidden="true"></i> '+newTable+'</span>';
                newAlert("The query was successfully executed");
                var tabNodeList = details.getElementsByTagName('span');
                var node = tabNodeList[tabNodeList.length-1];
                createDesc(node);
        }
    }
    else {
        newAlert("query failed!");
        //newAlert(intent);
        switch(intent) {
             case "alter field":
             case "alter length":   
                makeAllUneditable();
                break;
            case "alter null":
                //newAlert("yaha aya");
                document.getElementById(checkBox).checked = !empty;
                break;
            case "drop key":
            case "add key": 
                createDesc(tabNode);
                break;
            case "alter type":
                makeAllEditable();
                createDesc(tabNode);
                changeCount = 0;
                break;
            case "add column":
                enableAll();
            case "create table":
                document.getElementById('body').innerHTML = "";
        }
        enableAll();
    }
}

function queryBuilder(td,intent) {
    var qry = "";
    switch(intent) {
        case "drop col":
            var tr = td.parentElement;
            var col = tr.getElementsByTagName('td')[0].innerHTML;
            document.getElementById('message1').innerHTML = "The following query will delete the selected row. Do you want to execute it?";
            qry  = "ALTER TABLE `"+table+"` DROP COLUMN `"+col+"`;";
            document.getElementById('query').innerHTML = qry;
            document.getElementById('query').setAttribute("data-queryType","drop col");
            curTR = tr;
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            query = qry;
            break;
        case "drop tab":
             document.getElementById('message1').innerHTML = "The following query will delete the table '"+table+"'. Do you want to execute it?";
             qry  = "DROP TABLE `"+table+"`;";
             document.getElementById('query').innerHTML = qry;
             document.getElementById('query').setAttribute("data-queryType","drop tab");
             var pre = document.getElementById('query');
             hljs.highlightBlock(pre);
             document.getElementById('dialog1').style.display = 'block';
             query = qry;
             break;
        case "alter field":
            document.getElementById('message1').innerHTML = "The following query will change the name of the column '"+lastVal+"'. Do you want to execute it?";
            document.getElementById('query').innerHTML = query;
            document.getElementById('query').setAttribute("data-queryType","alter field");
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            disableAll("dialog1");
            break;
        case "alter length":
            document.getElementById('message1').innerHTML = "The following query will change the name of the length of the column from '"+lastVal+"' to '"+(lastLens.length == 2 ? lastLens[0]+","+lastLens[1] : lastLens[0])+"' . Do you want to execute it?";
            document.getElementById('query').innerHTML = query;
            document.getElementById('query').setAttribute("data-queryType","alter length");
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            disableAll("dialog1");
            break;
        case "alter null":
            document.getElementById('message1').innerHTML = "The following query will change the nullability of this column. Do you want to execute it?";
            document.getElementById('query').innerHTML = query;
            document.getElementById('query').setAttribute("data-queryType","alter null");
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            disableAll("dialog1");
            break;
        case "alter type":
            document.getElementById('message1').innerHTML = "The following query will change the datatype and/or it's length for this column. Do you want to execute it?";
            document.getElementById('query').innerHTML = query;
            document.getElementById('query').setAttribute("data-queryType","alter type");
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            disableAll("dialog1");
            break;
        case "drop key":
            document.getElementById('message1').innerHTML = "The following query will drop a key constraint from this column. Do you want to execute it?";
            document.getElementById('query').innerHTML = query;
            document.getElementById('query').setAttribute("data-queryType","drop key");
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            disableAll("dialog1");
            break;
        case "add key":
            document.getElementById('message1').innerHTML = "The following query will add a key constraint to this column. Do you want to execute it?";
            document.getElementById('query').innerHTML = query;
            document.getElementById('query').setAttribute("data-queryType","add key");
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            disableAll("dialog1");
            break;
        case "add column":
            document.getElementById('message1').innerHTML = "The following query will add new columns to the table '"+table+"'. Do you want to execute it?";
            document.getElementById('query').innerHTML = query;
            document.getElementById('query').setAttribute("data-queryType","add column");
            var pre = document.getElementById('query');
            hljs.highlightBlock(pre);
            document.getElementById('dialog1').style.display = 'block';
            disableAll("dialog1");
            break;

    }
}

function newAlert(message) {
	document.getElementById("message").innerHTML = message;
	document.getElementById("dialog").style.display = "block";
}

function newAlert1(message) {
	document.getElementById("message2").value = message;
	document.getElementById("dialog2").style.display = "block";
}

/** ne classes - b, tr, s-neg , for summary and if details.open, icon, logo, db-in-use, table-opt, del */

function disableAll(id) {
    var ignore = document.getElementById(id);
    var child = document.all;
    var disable = false;
    for(var i = 0; i < child.length; i++) {
        if(child[i].id == id || isDescendant(ignore,child[i])) {
            
        } 
        else if(disable) {
            if(child[i].nodeName == 'DIV' || child[i].nodeName == 'SPAN' || child[i].nodeName == 'SUMMARY' || child[i].classList.contains('del')) {
                child[i].style.pointerEvents = 'none';
            }
            else if(child[i].nodeName == 'DETAILS') {
                child[i].classList.add('s-neg');
            }
            else if(isDescendant(document.getElementById('struct-table'),child)) {

            }
            else {
                child[i].disabled = true;
            }
            if(child[i].classList.contains('b')) {
                child[i].classList.remove('b');
                child[i].classList.add('b-neg');
            }
            else if(child[i].classList.contains('summary')) {
                child[i].classList.remove('summary')
                child[i].classList.add('sum-neg');
            }
            else if(child[i].classList.contains('tr')) {
                child[i].classList.remove('tr');
                child[i].classList.add('tr-neg');
            }
            else if(child[i].classList.contains('icon')) {
                child[i].classList.remove('icon');
                child[i].classList.add('icon-neg');
            }
            else if(child[i].classList.contains('logo')) {
                child[i].classList.remove('logo');
                child[i].classList.add('logo-neg');
            }
            else if(child[i].classList.contains('db-in-use')) {
                child[i].classList.remove('db-in-use');
                child[i].classList.add('db-in-use-neg');
            }
            else if(child[i].classList.contains('table-opt')) {
                child[i].classList.remove('table-opt');
                child[i].classList.add('table-opt-neg');
            }
            else if(child[i].classList.contains('del')) {
                child[i].classList.remove('del');
                child[i].classList.add('del-neg');
            }
        }
        if(child[i].nodeName == "BODY") {
            disable = true;
        }
        else if(child[i].nodeName == 'IFRAME') {
            disable = false;
        }
    }
    var side_bar = document.getElementById('side-bar');
    side_bar.style.overflowY = 'hidden';
    side_bar.style.backgroundColor = "#8f8f8f";
    document.getElementById('nav').style.backgroundColor = "#08492e";
    var body = document.getElementsByTagName('body')[0];
    body.style.backgroundColor = '#8f8f8f';
}

function enableAll () {
    var child = document.all;
    var enable = false;
    for(var i = 0; i < child.length; i++) {
        if(enable) {
            if(child[i].nodeName == 'DIV'  || child[i].nodeName == 'SPAN' || child[i].nodeName == 'SUMMARY') {
                child[i].style.pointerEvents = 'auto';
            }
            else if(child[i].nodeName == 'DETAILS') {
                child[i].classList.remove('s-neg');
            }
            else if(isDescendant(document.getElementById('struct-table'),child));
            else {
                child[i].disabled = false;
            }
            if(child[i].classList.contains('b-neg')) {
                child[i].classList.remove('b-neg');
                child[i].classList.add('b');
            }
            else if(child[i].classList.contains('sum-neg')) {
                child[i].classList.remove('sum-neg')
                child[i].classList.add('summary');
            }
            else if(child[i].classList.contains('tr-neg')) {
                child[i].classList.remove('tr-neg');
                child[i].classList.add('tr');
            }
            else if(child[i].classList.contains('icon-neg')) {
                child[i].classList.remove('icon-neg');
                child[i].classList.add('icon');
            }
            else if(child[i].classList.contains('logo-neg')) {
                child[i].classList.remove('logo-neg');
                child[i].classList.add('logo');
            }
            else if(child[i].classList.contains('db-in-use-neg')) {
                child[i].classList.remove('db-in-use-neg');
                child[i].classList.add('db-in-use');
            }
            else if(child[i].classList.contains('table-opt-neg')) {
                child[i].classList.remove('table-opt-neg');
                child[i].classList.add('table-opt');
            }
            else if(child[i].classList.contains('del-neg')) {
                child[i].classList.remove('del-neg');
                child[i].classList.add('del');
                child[i].style.pointerEvents = 'auto';

            }
        }
        if(child[i].nodeName == "BODY") {
            enable = true;
        }
    }
    var side_bar = document.getElementById('side-bar');
    side_bar.style.overflowY = 'auto';
    side_bar.style.backgroundColor = "#eeeeee";
    document.getElementById('nav').style.backgroundColor = "#0d794c";
    var body = document.getElementsByTagName('body')[0];
    body.style.backgroundColor = '#fff';
}

function isDescendant(parent, child) {
     var node = child.parentNode;
     while (node != null) {
         if (node == parent) {
             return true;
         }
         node = node.parentNode;
     }
     return false;
}

function toInput(td,type) {
    editId = td.id;
    if(editable[td.id]) {
        makeAllUneditable(); 
        var val = td.innerHTML;
        lastVal = val;
        td.innerHTML = "<input type = 'text' value = '"+val+"' onkeyup = 'submitInputValue(event,this,\""+type+"\")'/>";
    }
}

function submitInputValue(event,ele,type) {
    if(event.ctrlKey && event.keyCode === 13) {
        switch(type) {
            case "field":
            {
                if(!isIllegal(ele.value)) {
                    newAlert("The characters ',', ; , : , ? , . , ' , \", \\ , / , ~, ` are not allowed in Field Names names");
                }
                else {
                    var tr = ele.parentElement.parentElement;
                    var type = tr.getElementsByTagName('td')[1].getElementsByTagName('select')[0].value;
                    var len = tr.getElementsByTagName('td')[2].innerHTML;
                    var Null = tr.getElementsByTagName('td')[3].getElementsByTagName('input')[0].checked == true ? " NULL" : " NOT NULL";
                    //var td = ele.parentElement;
                    //td.innerHTML = ele.value;
                    makeAllEditable();
                    query = "ALTER TABLE `"+table+"` CHANGE `"+lastVal+"` `"+ele.value+"` "+type+(len == "" ?  "" : "("+len+")")+Null+";";
                    newVal = ele.value;
                    queryBuilder(this,"alter field");
                }
            }
            break;
            case "length":
            {
                var dataType = ele.parentElement.parentElement.getElementsByTagName('td')[1].getElementsByTagName('select')[0].value;
                var name = ele.parentElement.parentElement.getElementsByTagName('td')[0].innerHTML;
                var Null = ele.parentElement.parentElement.getElementsByTagName('td')[3].getElementsByTagName('input')[0].checked == true ? " NULL" : " NOT NULL";
                var len; 
                if(inArray(lengths,dataType)) {
                    len = parseInt(ele.value);
                    if(isNaN(len)) {
                        newAlert("Non integer values are not permitted in the length/values of type '"+dataType+"'");
                    }
                    else {
                        if(len > typeLengths[dataType]) {
                            newAlert("The value you have entered exceeds the permitted range for the type '"+dataType+"'");
                        }
                        else {
                            lastLens = [len];
                            query = "ALTER TABLE `"+table+"` CHANGE `"+name+"` `"+name+"` "+dataType+"("+len+")"+Null+";";
                            queryBuilder(ele,"alter length");
                        }
                    }
                }
                else if(inArray(twoLengths,dataType)) {
                    if(ele.value.indexOf(",") == -1) {
                        newAlert("This value is not permitted for the Data Type '"+dataType+"'. Please seperate the lengths of the Decimal(D) and Fraction(F) parts as D,F");
                    }
                    else {
                        var len1 = parseInt(ele.value.split(",")[0]);
                        var len2 = parseInt(ele.value.split(",")[1]);
                        if(isNaN(len1) || isNaN(len1)) {
                            newAlert("Non integer values are not permitted in the length/values of type '"+dataType+"'");
                        }
                        else {
                            if(len1 > typeLengths[dataType][0] || len2 > typeLengths[dataType][1]) {
                                newAlert("The values you have entered exceeds the permitted range for the type '"+dataType+"'");
                            }
                            else {
                                lastLens = [len1,len2];
                                query = "ALTER TABLE `"+table+"` CHANGE `"+name+"` `"+name+"` "+dataType+"("+len1+","+len2+")"+Null+";";
                                queryBuilder(ele,"alter length");
                            }
                        }
                    }
                }
                else if(inArray(noSize,dataType)) {
                    if(ele.value != "") {
                        newAlert("This Data Type - '"+dataType+"` takes no arguments for its length!");
                    }
                    else {
                        lastLens = [""];
                        query = "ALTER TABLE `"+table+"` CHANGE `"+name+"` `"+name+"` "+dataType+" "+Null+";";
                        queryBuilder(ele,"alter length");
                    }
                }
            }
            break;
        }
    }
    else if (event.ctrlKey && event.keyCode === 16) {
        var td = ele.parentElement;
        td.innerHTML = lastVal;
        makeAllEditable();
    }
}

function changeDataType(select) {
    if(changeCount == 0) {
        changeCount++;
        disableAll("bottom-bar");
        makeAllUneditable();
        document.getElementById("bottom-bar").style.display = 'block';
        var tr = select.parentElement.parentElement;
        var td = tr.getElementsByTagName('td')[2];
        select.parentElement.style.pointerEvents = 'auto';
        select.style.pointerEvents = 'auto';
        select.disabled = false;
        var kids = select.getElementsByTagName('option');
        for(var i = 0; i < kids.length; i++)
            kids[i].disabled = false;
        var optgroups = select.getElementsByTagName('optgroup');
        for(var i = 0; i < optgroups.length; i++) {
            var options = optgroups[i].getElementsByTagName('option');
            optgroups[i].disabled = false
            for(var j = 0; j < options.length; j++) {
                options[j].disabled = false;
            }
        }
        td.style.pointerEvents = 'auto';
        var len = td.innerHTML;
        td.innerHTML = "<input type = 'text' value = '"+len+"'>";
        selectOption = select;

    }
}

function submitTypeChangeInfo() {
    var select = selectOption;
    var tr = select.parentElement.parentElement;
    var name = tr.getElementsByTagName('td')[0].innerHTML;
    var dataType = select.value;
    var lenVal = tr.getElementsByTagName('td')[2].getElementsByTagName('input')[0].value;
    var Null = tr.getElementsByTagName('td')[3].getElementsByTagName('input')[0].checked == true ? " NULL" : " NOT NULL";
    var len;
    if(inArray(lengths,dataType)) {
        len = parseInt(lenVal);
        if(isNaN(len)) {
            enableAll();
            newAlert("Non integer values are not permitted in the length/values of type '"+dataType+"'");
            keepItDark(select);
        }
        else {
            if(len > typeLengths[dataType]) {
                enableAll();
                newAlert("The value you have entered exceeds the permitted range for the type '"+dataType+"'");
                keepItDark(select);
            }
            else {
                query = "ALTER TABLE `"+table+"` CHANGE `"+name+"` `"+name+"` "+dataType+"("+len+")"+Null+";";
                enableAll();
                queryBuilder(select,"alter type");
                document.getElementById("bottom-bar").style.display = 'none';
            }
        }
    }
    else if(inArray(twoLengths,dataType)) {
        if(lenVal.indexOf(",") == -1) {
            enableAll();
            newAlert("This value is not permitted for the Data Type '"+dataType+"'. Please seperate the lengths of the Decimal(D) and Fraction(F) parts as D,F");
            keepItDark(select);
        }
        else {
            var len1 = parseInt(lenVal.split(",")[0]);
            var len2 = parseInt(lenVal.split(",")[1]);
            if(isNaN(len1) || isNaN(len1)) {
                enableAll();
                newAlert("Non integer values are not permitted in the length/values of type '"+dataType+"'");
                keepItDark(select);
            }
            else {
                if(len1 > typeLengths[dataType][0] || len2 > typeLengths[dataType][1]) {
                    enableAll();
                    newAlert("The values you have entered exceeds the permitted range for the type '"+dataType+"'");
                    keepItDark(select);
                }
                else {
                    query = "ALTER TABLE `"+table+"` CHANGE `"+name+"` `"+name+"` "+dataType+"("+len1+","+len2+")"+Null+";";
                    enableAll();
                    queryBuilder(select,"alter type");
                    document.getElementById("bottom-bar").style.display = 'none';
                }
            }
        }
    }
    else if(inArray(noSize,dataType)) {
        if(lenVal != "") {
            enableAll();
            newAlert("This Data Type - '"+dataType+"` takes no arguments for its length!");
            keepItDark(select);
        }
        else {
            lastLens = [""];
            query = "ALTER TABLE `"+table+"` CHANGE `"+name+"` `"+name+"` "+dataType+" "+Null+";";
            enableAll();
            queryBuilder(select,"alter type");
            document.getElementById("bottom-bar").style.display = 'none';
        }
    }
}


function cancelTypeChange() {
    enableAll();
    //makeAllEditable();
    document.getElementById("bottom-bar").style.display = 'none';
    changeCount = 0;
    createDesc(tabNode);
}

function changecount() {
    changeCount = 0;
}

function keepItDark(select) {
    disableAll('dialog');
    var buttons = document.getElementById("bottom-bar").getElementsByTagName('input');
    buttons[0].style.pointerEvents = 'auto'; buttons[0].disabled = false;
    buttons[1].style.pointerEvents = 'auto'; buttons[1].disabled = false;
    var tr = select.parentElement.parentElement;
    var td = tr.getElementsByTagName('td')[2];
    td.getElementsByTagName('input')[0].disabled = false;
    select.parentElement.style.pointerEvents = 'auto';
    select.style.pointerEvents = 'auto';
    select.disabled = false;
    var kids = select.getElementsByTagName('option');
    for(var i = 0; i < kids.length; i++)
    kids[i].disabled = false;
    var optgroups = select.getElementsByTagName('optgroup');
    for(var i = 0; i < optgroups.length; i++) {
        var options = optgroups[i].getElementsByTagName('option');
        optgroups[i].disabled = false
        for(var j = 0; j < options.length; j++) {
            options[j].disabled = false;
        }
    }
    td.style.pointerEvents = 'auto';
    
}
/** Methods for setting the editabilty of table structure */
function addToEditable() {
    var collection = document.getElementsByClassName('editable');
    for(var i = 0; i < collection.length; i++) {
        editable[collection[i].id] = true;
    }
}

function makeAllUneditable() {
    var collection = document.getElementsByClassName('editable');
    for(var i = 0; i < collection.length; i++) {
        editable[collection[i].id] = false;
    }
    var table = document.getElementById('body').getElementsByTagName('table')[0];
    var tr = table.getElementsByTagName('tr');
    for(var i = 1; i < tr.length; i++) {
        var td = tr[i].getElementsByTagName('td');
        for(var j = 0; j < td.length; j++) {
            if(td[j].id == "") {
                td[j].style.pointerEvents = 'none';
                td[j].style.filter = "brightness(79%)";
            }
        }
    }
}


function makeAllEditable() {
    var collection = document.getElementsByClassName('editable');
    for(var i = 0; i < collection.length; i++) {
        editable[collection[i].id] = true;
    }
    var table = document.getElementById('body').getElementsByTagName('table')[0];
    var tr = table.getElementsByTagName('tr');
    for(var i = 1; i < tr.length; i++) {
        var td = tr[i].getElementsByTagName('td');
        for(var j = 0; j < td.length; j++) {
            if(td[j].id == "") {
                td[j].style.pointerEvents = 'auto';
                td[j].style.filter = "brightness(100%)";
            }
        }
    }
}

/** End */

function AlterNull(ele) {
    checkBox = ele.id;
    var tr = ele.parentElement.parentElement;
    var name = tr.getElementsByTagName('td')[0].innerHTML;
    var type = tr.getElementsByTagName('td')[1].getElementsByTagName('select')[0].value;
    var len = tr.getElementsByTagName('td')[2].innerHTML;
    empty = tr.getElementsByTagName('td')[3].getElementsByTagName('input')[0].checked;
    var Null =  empty == true ? " NULL" : " NOT NULL";
    query = "ALTER TABLE `"+table+"` CHANGE `"+name+"` `"+name+"` "+type+(len == "" ?  "" : "("+len+")")+Null+";";
    queryBuilder(this,"alter null"); 
    // query = "ALTER TABLE `"+table+"` CHANGE `"+lastVal+"` `"+ele.value+"` "+type+(len == "" ?  "" : "("+len+")")+Null+";";

}

function AlterKey(ele) {
    var key = ele.value;
    var tr = ele.parentElement.parentElement;
    var name = tr.getElementsByTagName('td')[0].innerHTML;
    var details = tabNode.parentElement;
    var i = parseInt(tr.getAttribute('data-i'));
    var tableData = SQL.desc_table(table);
    var prevVal =  tableData.get(i).Key; //DB.dblist.get(index).tables.remove(tabIndex)
    console.log(prevVal);
    if(key == ''){
        if(prevVal == "PRI") {
            var index = SQL.getIndex(table,name);
            query = "ALTER TABLE `"+table+"` DROP INDEX `"+index+"`;";
            queryBuilder(this,"drop key");
        }
        else if(prevVal == "UNI") {
            var index = SQL.getIndex(table,name);
            query = "ALTER TABLE `"+table+"` DROP INDEX `"+index+"`;";
            queryBuilder(this,"drop key");
        }
    }
    else if(key == 'primary key') {
        query = "ALTER TABLE `"+table+"` ADD PRIMARY KEY(`"+name+"`);";
        queryBuilder(this,"add key");
    }
    else if(key == 'unique') {
        if(prevVal == 'PRI') {
            newAlert("A Primary Key is already unique!");
            ele.selectedIndex = 0;
        }
        else {
            //console.log("here");
            query = "ALTER TABLE `"+table+"` ADD UNIQUE(`"+name+"`);";
            queryBuilder(this,"add key");
        }
    }
}

/** Type Checking methods and variables */
var typeLengths = {
	"tinyint" : 3,
	"smallint" : 5,
	"mediumint" : 8,
	"int" : 10,
	"bigint" : 19,
	 "bit" : 64,
	 "char" : 255,
	 "varchar" : 65535,
	 "numeric" : [35,30],
	 "double" : [15,53],
	 "float" : [15,23],
	 "real" : [15,23],
};

var twoLengths = ["numeric","double","float","real","decimal"];
var lengths = ["tinyint","smallint","mediumint","int","bigint","bit","char","varchar"];
var noSize = ["boolean","date","datetime","timestamp","time","year","text","blob","longtext","longblob"];
var illegal = [",",";",":","?",".","'","\\","/","~","`"];

function isIllegal(name) {
    for(var j = 0; j < illegal.length; j++)
	    if(name.indexOf(illegal[j]) > -1) {
            console.log(name+".indexOf("+illegal[j]+") = "+(name.indexOf(illegal[j])));
			return false;
		}
    return true;
}

function inArray(arr,ele) {
	for(var i = 0; i < arr.length; i++) {
		if(arr[i].toLocaleUpperCase() == ele.toLocaleUpperCase())
			return true;
	}
	return false;
}
/** End */

/** Method to add columns*/
function addColumns() {
    var body = document.getElementById('body');
    var html = body.innerHTML;
    if(document.getElementById('addColumn') == null) {
        console.log("Created that");
        var TableData = SQL.desc_table(table);
        html += "<div class = 'add-col' id = 'addColumn'>";
        html += "<span style = ' margin-left : 10px; margin-right : 10x;color : #333; font-family : \"Britannic Bold\",\"Serif\"; !important; font-size : 16px; -webkit-text-stroke : 0.5px'> Add </span><input style = 'width : 65px;' type = 'text'>";
        html += "<span style = ' margin-left : 7px; margin-right : 7px;color : #333; font-family : \"Britannic Bold\",\"Serif\"; !important; font-size : 16px; -webkit-text-stroke : 0.5px'> Columns(s) </span>";
        var select = "<select>";
        select += "<option value = 'FIRST'>at the begining</option>";
        for(var i = 0; i < tabData.size(); i++) {
            select += "<option value = 'AFTER `"+TableData.get(i).Field+"`'>after "+TableData.get(i).Field+"</option>";
        }
        select += "</select>";
        html += select+"<br>";
        html += "<div style = 'margin-top : 7px;'><input type = 'button' style = 'margin-right : 15px;' class = 'dia-button' onclick = 'addColumnsTable()' value = 'Submit'><input type = 'button' class = 'dia-button' onclick = 'this.parentElement.parentElement.style.display = \"none\"'; value = 'Cancel' ></div>";
        html += "</div>"
        body.innerHTML = html;
    }
    else {
        console.log("Revived that");
        document.getElementById('addColumn').style.display = 'block';
    }
}

function addColumnsTable() {
    var addColDiv = document.getElementById('addColumn');
    addColDiv.classList.remove('add-col');
    addColDiv.style.marginTop = '40px';
    var num = parseInt(addColDiv.getElementsByTagName('input')[0].value);
    var pos = addColDiv.getElementsByTagName('select')[0].value;
    position = pos;
    var select = "<select><option value = '' disabled selected>Select a Data Type</option><optgroup label=\"Numeric\"><option value = \"tinyint\">TINYINT</option>"
                    + "<option value = \"smallint\">SMALLINT</option><option value = \"mediumint\">MEDIUMINT</option><option value = \"int\">INT</option>"
                    + "<option value = \"bigint\">BIGINT</option><option value = \"float\">FLOAT</option><option value = \"numeric\">NUMERIC</option><option value = \"double\">DOUBLE</option>"
                    + "<option value = \"bit\">BIT</option><option value = \"boolean\">BOOLEAN</option></optgroup><optgroup label=\"Date and time\">"
                    + "<option value = \"date\">DATE</option><option value = \"datetime\">DATETIME</option><option value = \"timestamp\">TIMESTAMP</option>"
                    + "<option value = \"time\">TIME</option><option value = \"year\">YEAR</option></optgroup><optgroup label=\"String\"><option value = \"char\">CHAR</option>"
                    + "<option value = \"varchar\">VARCHAR</option><option value = \"text\">TEXT</option><option value = \"longtext\">LONGTEXT</option><option value = \"blob\">BLOB</option><option value = \"longblob\">LONGBLOB</option></optgroup></select>";
    //var uni = "<select><option value = ''>NONE</option><option value = 'UNIQUE'>UNIQUE</option><option value = 'PRIMARY KEY'>PRIMARY KEY</option></select>";
    if(isNaN(num)) {
        newAlert("Non-Integer parseInt of rows cannot be added to the table '"+table+"'!");
    }
    else {
        makeAllUneditable();
        document.getElementById('tableOptions').style.display = 'none';
        var html = "<span class = 'span' style = 'color : darkslategrey; font-family : \"Lobster\",\"Serif\"; !important; font-size : 22px; -webkit-text-stroke : 0.5px'>Add "+num+" columns to the table '"+table+"'</span>";
        html += "<table id = 'addColTab' class = 'table'><tr><th class = 'b'>Field</th class = 'b'><th class = 'b'>Type</th><th class = 'b'>Length</th><th class = 'b'>Null</th></tr>";
        for(var i = 0; i < num; i++) {
            html += "<tr class = 'tr'>";
            html += "<td class = 'td'><input type = 'text'/></td>";
            html += "<td class = 'td'>"+select+"</td>";
            html += "<td class = 'td'><input type = 'text'/></td>";
            html += "<td class = 'td'><input type = 'checkbox'/></td>";
            html += "</tr>";
        }
        html += "</table>";
        html += "<div style = 'text-align : center; margin : 15px;'><div class = 'table-opt' onclick = 'addColumnToTable()'><i class='fa fa-plus' aria-hidden='true'></i> Add Columns</div><div class = 'table-opt' onclick = 'restoreStructTable()'><i class=\"fa fa-times\" aria-hidden=\"true\"></i> Cancel</div></div>";
        addColDiv.innerHTML = html;
    }
}




function addColumnToTable() {
    var tab = document.getElementById('addColTab');
    if(validate(tab)) {
        var tr = tab.getElementsByTagName('tr');
        var qry = "ALTER TABLE `"+table+"` ";
        if(tr.length == 2){
            var td = tr[1].getElementsByTagName('td');
            var name = td[0].getElementsByTagName('input')[0].value;
            var type = td[1].getElementsByTagName('select')[0].value;
            var len = td[2].getElementsByTagName('input')[0].value;
            var Null = td[3].getElementsByTagName('input')[0].checked;
            qry += "ADD `"+name+"` "+type+(len == ""? "" : "("+len+")")+(Null == true? " NULL" : " NOT NULL")+"  "+position+";";
            query = qry;
            queryBuilder(this,"add column");
        }
        else {
            for(var i = 1; i < tr.length -1; i++) {
                if(i == 1){
                    var td = tr[1].getElementsByTagName('td');
                    var name = td[0].getElementsByTagName('input')[0].value;
                    var type = td[1].getElementsByTagName('select')[0].value;
                    var len = td[2].getElementsByTagName('input')[0].value;
                    var Null = td[3].getElementsByTagName('input')[0].checked;
                    qry += "ADD `"+name+"` "+type+(len == ""? "" : "("+len+")")+(Null == true? " NULL" : " NOT NULL")+"  "+position+", ";
                }
                else {
                    var td = tr[i].getElementsByTagName('td');
                    var name = td[0].getElementsByTagName('input')[0].value;
                    var type = td[1].getElementsByTagName('select')[0].value;
                    var len = td[2].getElementsByTagName('input')[0].value;
                    var Null = td[3].getElementsByTagName('input')[0].checked;
                    var prevTd = tr[i-1].getElementsByTagName('td')[0];
                    var after = " AFTER `"+prevTd.getElementsByTagName('input')[0].value+"`";
                    qry += "ADD `"+name+"` "+type+(len == ""? "" : "("+len+")")+(Null == true? " NULL" : " NOT NULL")+"  "+after+", ";
                }
            }
            var td = tr[tr.length - 1].getElementsByTagName('td');
            var name = td[0].getElementsByTagName('input')[0].value;
            var type = td[1].getElementsByTagName('select')[0].value;
            var len = td[2].getElementsByTagName('input')[0].value;
            var Null = td[3].getElementsByTagName('input')[0].checked;
            var prevTd = tr[tr.length-2].getElementsByTagName('td')[0];
            var after = " AFTER `"+prevTd.getElementsByTagName('input')[0].value+"`";
            qry += "ADD `"+name+"` "+type+(len == ""? "" : "("+len+")")+(Null == true? " NULL" : " NOT NULL")+"  "+after+";";
            query = qry;
            queryBuilder(this,"add column");
        }
    }
}

function NameNotInTable(name) {
    var TableData = SQL.desc_table(table);
    for(var i = 0; i < TableData.size(); i++)
        if(TableData.get(i).Field == name) {
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

function validate(tab) {
    var errCount = 0;
    var okay = true;
    var errStr = "";
    var tr = tab.getElementsByTagName('tr');
    rowSize = tr.length;
    for(var i = 1; i < tr.length; i++) {
        var str = "";
        var td = tr[i].getElementsByTagName('td');
        var field = td[0].getElementsByTagName('input')[0];
        var type = td[1].getElementsByTagName('select')[0];
        var lengthy = td[2].getElementsByTagName('input')[0];
        var Null = td[3].getElementsByTagName('input')[0];
        var key = null;
        if(td.length == 5) {
            key = td[4].getElementsByTagName('select')[0].value;
        }
        var name = field.value;
        var dataType = type.value; 
        var len = lengthy.value;
        var empty = Null.checked;
        /** Length Related type-checking for different data types*/
        if(inArray(twoLengths,dataType)) {
            if(len.length != 0) {
                var parts = len.split(",");
                if(parts.length != 2) {
                    str += "Numeric or Decimal types need to specify the length as M,D, where M is the total length, and D is the length of the fraction#";
                    //lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
                    okay = false;
                }
                if(parts.length == 2 && (isNaN(parseInt(parts[0])) || isNaN(parseInt(parts[1])))) {
                    str += "Non Numeric values of length#";
                    //lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
                    okay = false;
                }
                if(parts.length == 2 && (!isNaN(parseInt(parts[0])) || !isNaN(parseInt(parts[1])))) {
                    if(parseInt(parts[0]) > typeLengths[dataType][0] || parseInt(parts[1]) > typeLengths[dataType][1])
                    {
                        str += "The length for the data type exceeds the permitted range#";
                        //lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
                        okay = false;
                    }
                } 
            }
        }
        else if(inArray(lengths,dataType)) {
            if(len.length != 0) {
                if(isNaN(len)) {
                    str += "Non numeric value for length#";
                    //lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
                    okay = false;
                }
                else {
                    if(parseInt(len) > typeLengths[dataType]) {
                        str += "The length for the data type exceeds the permitted range#";
                        //lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
                        okay = false;
                    }
                }
            }
        }
        else if(inArray(noSize,dataType)) {
            if(len.length != 0) {
                str += "The "+dataType+" type does not take a length argument#";
                //lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
                okay = false;
            }
        }
        else if(dataType == "") {
            str += "Select a Data Type!#";
            okay = false;
        }
        /** Validation of field names */
        for(var j = 0; j < illegal.length; j++)
            if(name.indexOf(illegal[j]) > -1 ) {
                str += "The characters ',', ; , : , ? , . , ' , \", \\ , / , ~, ` are not allowed in Attribue names#";
                //field.style.backgroundColor = "rgba(240,70,78,0.1)";
                okay = false;
                break;
            }
            if(allEmpty(name)) {
                str += "Field name cannot be empty#";
                //field.style.backgroundColor = "rgba(240,70,78,0.1)";
                okay = false;
            }
            if(td.length == 4 && !NameNotInTable(name)) {
                str += "A column with the name '"+name+"' already exists in the table '"+table+"'.";
                //field.style.backgroundColor = "rgba(240,70,78,0.1)";
                okay = false;
            }
            /**Primary key and not null*/
            if(key != null)
                if(key == "PRIMARY KEY" && empty == true) {
                str += "Primary Key cannot be null";
                okay = false;
                }
            if(str.length > 0) {
                errCount++;
                console.log(str);
                if(str[str.length - 1] == "#") 
                str = str.slice(0,str.length-1);
                var errors = str.split("#");
                var invar = errors[1] == "" ? 1 : errors.length;
                var error = "";
                for(var k = 0; k < invar; k++) 
                error += "\n\t "+(k+1)+". "+errors[k]+"";
                errStr += "\n "+errCount+". For "+(name == "" ? "Column Number "+i :"the field "+name)+" : "+error+"";
            }
        }
        errStr += "";
        if(!okay) {
            console.log("Got here");
            console.log(errStr);
            newAlert1(errStr);
        }
        return okay;
    }

function restoreStructTable() {
    createDesc(tabNode);
}
/*function resetAddColTab() {
    var table = document.getElementById('addColTab');
    var tr = document.getElementsByTagName('tr');
    for(var i = 1; i < tr.length; i++) {
        var td = tr[i].getElementsByTagName('td');
        td[0].getElementsByTagName('input')[0].style.backgroundColor = '#fff';
        td[2].getElementsByTagName('input')[0].style.backgroundColor = '#fff';
    }
}*/
/** End                  */