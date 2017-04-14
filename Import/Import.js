var rowSize = -1;

function reset() {
	var table = document.getElementsByTagName('table')[0];
	var tr = table.getElementsByTagName('tr');
	for(var i = 1; i < tr.length; i++) {
		var td = tr[i].getElementsByTagName('td');
		for(var j = 0; j < td.length; j++) {
			switch(j) {
				case 0:
					var input = td[j].getElementsByTagName('input')[0];
					input.value = input.getAttribute("data-def");
					input.style.backgroundColor = "#fff";
					break;
				case 1:
					var select = td[j].getElementsByTagName('select')[0];
					select.selectedIndex = 0;
					input.style.backgroundColor = "#fff";
					break;
				case 2:
					var input = td[j].getElementsByTagName('input')[0];
					input.value = input.getAttribute("data-def");
					input.style.backgroundColor = "#fff";
					break;
				case 3:
					var checkbox = td[j].getElementsByTagName('input')[0];
					checkbox.checked = true;
					input.style.backgroundColor = "#fff";
					break;
				case 4:
					var select = td[j].getElementsByTagName('select')[0];
					select.selectedIndex = 0;
					input.style.backgroundColor = "#fff";
					break;
			}
		}
	}
	document.getElementById('dialog').style.display = 'none';
}

function NotNull(select) {
	if(select.value == "primary key") {
		tr = select.parentElement.parentElement;
		td = tr.getElementsByTagName('td');
		checkbox = td[3].getElementsByTagName('input')[0];
		checkbox.checked = false;
	}
}

function removeQuotes(string) {
    var str = "";
	var singleQuote = string.split("'");
    for(var i = 0; i < singleQuote.length - 1; i++) {
        str += singleQuote[i]+"\u2019";
    }
    str += singleQuote[singleQuote.length - 1];
    return str;
}

function newAlert(message) {
	document.getElementById("message").value = message;
	document.getElementById("dialog").style.display = "block";
}

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
var lengths = ["tinyint","smallint","mediumint","int","bigint","bit","char","varchar","text","blob"];
var noSize = ["boolean","date","datetime","timestamp","time","year"];

var illegal = [",",";",":","?",".","'","\\","/","~"];

function validate() {
	var errCount = 0;
	if(arguments.length >= 0) {
		var okay = true;
		var errStr = ""
		var table = document.getElementsByTagName('table')[0];
		var tr = table.getElementsByTagName('tr');
		rowSize = tr.length;
		for(var i = 1; i < tr.length; i++) {
			var str = "";
			var td = tr[i].getElementsByTagName('td');
			var field = td[0].getElementsByTagName('input')[0];
			var type = td[1].getElementsByTagName('select')[0];
			var lengthy = td[2].getElementsByTagName('input')[0];
			var Null = td[3].getElementsByTagName('input')[0];
			var key = td[4].getElementsByTagName('select')[0].value;
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
						lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
						okay = false;
					}
					if(parts.length == 2 && (isNaN(Number(parts[0])) || isNaN(Number(parts[1])))) {
						str += "Non Numeric values of length#";
						lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
						okay = false;
					}
					if(parts.length == 2 && (!isNaN(Number(parts[0])) || !isNaN(Number(parts[1])))) {
							if(Number(parts[0]) > typeLengths[dataType][0] || Number(parts[1]) > typeLengths[dataType][1])
							{
								str += "The length for the data type exceeds the permitted range#";
								lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
								okay = false;
							}
					} 
				}
			}
			else if(inArray(lengths,dataType)) {
				if(len.length != 0) {
					if(isNaN(len)) {
						str += "Non numeric value for length#";
						lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
						okay = false;
					}
					else {
						if(Number(len) > typeLengths[dataType]) {
							str += "The length for the data type exceeds the permitted range#";
							lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
							okay = false;
						}
					}
				}
			}
			else if(inArray(noSize,dataType)) {
				if(len.length != 0) {
					str += "The "+dataType+" type does not take a length argument#";
					lengthy.style.backgroundColor = "rgba(240,70,78,0.1)";
					okay = false;
				}
			}

			/** Validation of field names */
			for(var j = 0; j < illegal.length; j++)
				if(name.indexOf(illegal[j]) > -1) {
					str += "The characters ',', ; , : , ? , . , ' , \", \\ , / , ~  are not allowed in Attribue names#";
					field.style.backgroundColor = "rgba(240,70,78,0.1)";
					okay = false;
					break;
				}
			
			/**Primary key and not null*/

			if(key == "primary key" && empty == true) {
				str += "Primary key cannot be null";
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
				errStr += "\n "+errCount+". For the field "+name+":"+error+"";
			}

		}
		errStr += "";
		if(!okay) {
			newAlert(errStr);
		}
		return okay;
	}
}

function inArray(arr,ele) {
	for(var i = 0; i < arr.length; i++) {
		if(arr[i] == ele)
			return true;
	}
	return false;
}

function go() {
	var obj;
	if(arguments.length == 1)
		obj = arguments[0];
	if(validate()) {

		document.getElementById('dialog').style.display = 'none';
		var table = document.getElementsByTagName('table')[0];
		var tr = table.getElementsByTagName('tr');
		var tabName = document.getElementsByTagName('div')[0].getAttribute('data-tab');
		var db = document.getElementsByTagName('div')[0].getAttribute('data-db');
		statement = "create table `"+tabName+"` (";
		for(var i = 1; i < tr.length -1; i++) {
			var td = tr[i].getElementsByTagName('td');
			var name = td[0].getElementsByTagName('input')[0].value;
			var type = td[1].getElementsByTagName('select')[0].value;
			var len = td[2].getElementsByTagName('input')[0].value;
			var Null = td[3].getElementsByTagName('input')[0].checked;
			var key = td[4].getElementsByTagName('select')[0].value;

			statement += " `"+name+"` "+type + (len.length == 0 ? "" : "("+len+")")+(key == "none" ? "" : " "+key)+(Null == true ? "" : key == "primary key"? "" : " not null")+ ",";
		}
		var td = tr[tr.length - 1].getElementsByTagName('td');
		var name = td[0].getElementsByTagName('input')[0].value;
		var type = td[1].getElementsByTagName('select')[0].value;
		var len = td[2].getElementsByTagName('input')[0].value;
		var Null = td[3].getElementsByTagName('input')[0].checked;
		var key = td[4].getElementsByTagName('select')[0].value;

		statement += " `"+name+"` "+type + (len.length == 0 ? "" : "("+len+")")+(key == "none" ? "" : " "+key)+(Null == true ? "" : " not null")+");";
		//newAlert(statement);
		//newAlert("Daichi");
		//newAlert(statement);
		var res = SQL.execQuery("use "+db+";");
		//var res = true;
		//newAlert("otawa");
		//newAlert(res);
		if(res) {
			var res = SQL.execQuery(statement);
			//var res = true;
			if(res) {
				var html = "<div class = \"newBody\" align = \"center\"><h4 style = \"font-family : 'Lucida Calligraphy', Serif; text-align : left !important;\">Table "+tabName+" has been successfully created in the Database "+db+". ";
				html += "To import all the data from the selected File, click the Import Data Button</h4><button class = \"button\">Import Data</button><div id=\"myProgress\" style = \"display : block; text-align : left !important;\">";
				html += "<div id=\"myBar\"></div></div><div align = \"right\" id = \"per\" style = \"font-family : 'Arial', Sans-serif\">0%</div></div>";
				document.getElementsByTagName('div')[0].innerHTML = html;
				var button = document.getElementsByTagName('button')[0];
				if(arguments.length == 1) {
					button.addEventListener("click",function(){
						import1(obj);
					});
				}
				else {
					button.addEventListener("click",function(){
						import1();
					});
				}				
			}
		}
	}
}

function import1(){
	var tabName = document.getElementsByTagName('div')[0].getAttribute('data-tab');
	var db = document.getElementsByTagName('div')[0].getAttribute('data-db');
	var level =  document.getElementById("per");
	var elem = document.getElementById("myBar");
	//newAlert(arguments.length);
	if(arguments.length == 0) {
		var arr = SQL.array;
		var r = SQL.row;
		var c = SQL.col;
		var i = 0;
		for(var a = 0; a < r; a++) {
			var statement = "insert into `"+tabName+"` values (";
			for(var b = 0; b < c - 1; b++) {
				var item = arr.get(i).toString().slice(1,-1);
				statement +=  item == ""? null+",":"'"+removeQuotes(item)+"',";
				i++;
				}
				var last = arr.get(i).toString().slice(1,-1);
				statement += last == "" ? null+");"  : "'"+removeQuotes(last)+"');";
				i++;
				var res = SQL.execQuery(statement);
				console.log(statement);
				var size = r * c;
				var per = Math.floor(i * 100 / size);
				elem.style.width = per + "%";
				level.innerHTML = per + "%";
				if(res) {
					if(per == 100) {
						function s() {
							document.getElementsByTagName('div')[1].innerHTML = "<h4 style = \"font-family : 'Lucida Calligraphy', Serif; text-align : left !important;\">Data from the excel document has been sucessfully imported to the SQL table. Please close the window to continue using SQLNavigator</h4>";
							//newAlert("from excel");
						}
						setTimeout(s,1500);
					}
				}
				else {
					document.getElementsByTagName('div')[1].innerHTML = "<h4 style = \"font-family : 'Lucida Calligraphy', Serif; text-align : left !important; color : red\">The transfer of Data from the excel document to the SQL Table couldn't be completed because some rows in the table had illegal values, incompatible with standard SQL datatypes.<br> This table cannot be imported. Sorry!</h4>";
					SQL.execQuery("drop table "+tabName+";");
					return false;
				}
			}
		}
		else if(arguments.length == 1) {
			var data = arguments[0];
			var size = data.length;
			for(var i = 0; i < data.length; i++) {
				var statement = "insert into `"+tabName+"` values (";
				for(var j = 0; j < data[i].length - 1; j++) {
					var item = data[i][j];
					statement +=  item == ""? ""+",":"'"+removeQuotes(item)+"',";
				}
				var last = data[i][data[i].length - 1];
				statement += last == "" ? ""+");"  : "'"+removeQuotes(last)+"');";
				console.log(statement);
				var res = SQL.execQuery(statement);
				if(res) {
					var per = Math.floor((i +1) * 100 /size);
					elem.style.width = per + "%";
					level.innerHTML = per + "%";
					if(per == 100) {
						function t() {
							document.getElementsByTagName('div')[1].innerHTML = "<h4 style = \"font-family : 'Lucida Calligraphy', Serif; text-align : left !important;\">Data from the JSON File has been sucessfully imported to the SQL table. Please close the window to continue using SQLNavigator</h4>";
							//newAlert("from json");
						}
						setTimeout(t,1500);
					}
				}
				else {
					document.getElementsByTagName('div')[1].innerHTML = "<h4 style = \"font-family : 'Lucida Calligraphy', Serif; text-align : left !important; color : red\">The transfer of Data from the JSON File to the SQL Table couldn't be comleted because some rows in the table had illegal values, incompatible with standard SQL datatypes.<br> This table cannot be imported. Sorry!</h4>";
					SQL.execQuery("drop table "+tabName+";");
					return false;
				}
				
			}
		}
}


document.addEventListener("mousedown", function(e) {
  if (e.target.id === "fbInspectFrame") {
    var inspectedElement = Firebug.browser.getElementFromPoint(e.clientX, e.clientY);

    // Here goes the code, which processes the inspected element
  }
});