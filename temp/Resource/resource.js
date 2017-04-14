var clicked = {};
var primaryKey = 0;
var unique = [];
var columns = [];
var rowValues = [];
var prevVal = "";
var uni = "";
var pri = "";


function search() {
	var input, filter, table, tr, td, i, tdi, j;
	input = document.getElementById("myInput");
	filter = input.value.toUpperCase();
	if(filter === "") {
		var table = document.getElementById("myTable");
		var tr = table.getElementsByTagName("tr");
		for (i = 0; i < tr.length; i++) {
			var tdi = tr[i].getElementsByTagName("td");
			for(j = 0; j < tdi.length; j++) {
				var td = tdi[j];
				td.style.textDecoration = "none";
				tr[i].style.display = "";
			}
		}
	}
	else {
		table = document.getElementById("myTable");
		tr = table.getElementsByTagName("tr");
		for (i = 0; i < tr.length; i++) {
			tdi = tr[i].getElementsByTagName("td");
			for(j = 0; j < tdi.length; j++) {
				td = tdi[j];
				if (td)
					if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
						tr[i].style.display = "";
						td.style.textDecoration = "underline";
						break;
					}
                else {
                    tr[i].style.display = "none";
                    td.style.textDecoration = "none";
                }
            }
        }
    }
}


function toInput(td) {
		if(clicked[td.id] == false) {
			var height = td.offsetHeight;
			var width = td.offsetWidth;
			var id = td.id;
			var row = Number(id.split(",")[0]);
			var col = Number(id.split(",")[1]);
			uni = inArray(col.toString(),unique) ? "UNIQUE" : "";
			rowValues = getRowValues(row);
			pri = col === primaryKey ? "PRIMARY KEY" : "";
			var pre = td.getElementsByTagName("pre");
			var value = pre[0].innerHTML;
			prevVal = value;
			OKStatus(td.getAttribute("data-type")+", NULL = "+td.getAttribute("data-null")+", "+uni+pri,"OK");
			td.innerHTML = "<textarea id = '"+td.id+"t"+"' onkeydown = 'accept(event,\""+td.id+"\")'></textarea>";
			document.getElementById(td.id+"t").value = value;
			document.getElementById(td.id+"t").style.height = height;
			document.getElementById(td.id+"t").style.width = width;
			allTrue();
    }
}

function accept (event, ele) {
	if(event.ctrlKey && event.keyCode === 13) {
		//(ele);
		var id = ele + "t";
		var texta = document.getElementById(id);
		var value = texta.value;
		//(value);
		var datatype = texta.parentElement.getAttribute("data-type");
		var type = findTypeCheckPara(datatype);
		var Null = texta.parentElement.getAttribute("data-null");
		//(Null+","+value);
		var tableName = document.getElementById('myTable').getAttribute("data-name");
		var row = Number(ele.split(",")[0]);
		var col = Number(ele.split(",")[1]);
		var colNames = getColumnNames();
		var constraint = primaryKey == -1 && inArray(col.toString(),unique) ? col : primaryKey == -1? Number(unique[0]) : primaryKey;
		////(constraint);
		AlertStatus(Null == "NO",value.length === 0);
		if(value.length === 0 || isItAllNewLine(value)) {
			if(Null == "NO" || Null == "no") {
				newAlert("A NULL value cannot be inserted into '"+colNames[col]+"', NULL = NO");
				AlertStatus(datatype+" NULL = "+Null+", "+uni+pri,"A NULL value cannot be inserted into '"+colNames[col]+"', NULL = NO");
			}
			else {
				if(checktype(type,value)) {
					if(inArray(col.toString(),unique)) {
						if(!checkforDuplicate(row+1,col,value) && value != prevVal) {
							//newAlert("UPDATE "+tableName+" SET "+colNames[col]+" = "+value+" WHERE "+colNames[constraint]+" = "+rowValues[constraint]+"constraint = "+constraint);
							QuickUpdate.update(tableName,colNames[col],null,colNames[constraint],rowValues[constraint]);
							OKStatus("OK","Table Sucessfully Updated");
							changeTD(ele,value);
						}
						else {
							newAlert("UPDATE "+tableName+" SET "+colNames[col]+" = "+value+" WHERE "+colNames[constraint]+" = "+rowValues[constraint]+"constraint = "+constraint);
							AlertStatus(datatype+" NULL = "+Null+", "+uni+pri,"Duplicate value : "+value+" cannot be applied to '"+colNames[col]+"', UNIQUE'");
						}
					}
					else {
						//newAlert("UPDATE "+tableName+" SET "+colNames[col]+" = "+value+" WHERE "+colNames[constraint]+" = "+rowValues[constraint]+"constraint = "+constraint);
						QuickUpdate.update(tableName,colNames[col],null,colNames[constraint],rowValues[constraint]);
						OKStatus("OK","Table Sucessfully Updated");
						changeTD(ele,value);
					}
				}
			}
		}
		else if(checktype(type,value)) {

			if(col === primaryKey) {
				if(!checkforDuplicate(row+1,col,value) && value != prevVal) {
					//newAlert("UPDATE "+tableName+" SET "+colNames[col]+" = "+value+" WHERE "+colNames[constraint]+" = "+rowValues[constraint]+"constraint = "+constraint);
					var success = QuickUpdate.update(tableName,colNames[col],value,colNames[primaryKey],prevVal);
					if(success) {
						OKStatus("OK","Table Sucessfully Updated");
						changeTD(ele,value);
					}
				}
				else {
					AlertStatus(datatype+" NULL = "+Null+", "+uni+pri,"Duplicate value : "+value+" cannot be applied to the Primary Key");
				}
			}
			else if(inArray(col.toString(),unique)) {
				if(!checkforDuplicate(row+1,col,value) && value != prevVal) {
					//newAlert("UPDATE "+tableName+" SET "+colNames[col]+" = "+value+" WHERE "+colNames[constraint]+" = "+rowValues[constraint]+"constraint = "+constraint);
					var success = QuickUpdate.update(tableName,colNames[col],value,colNames[constraint],rowValues[constraint]);
					OKStatus("OK","Table Sucessfully Updated");
					changeTD(ele,value);
				}
				else {
					AlertStatus(datatype+" NULL = "+Null+", "+uni+pri,"Duplicate value : "+value+" cannot be applied to '"+colNames[col]+"', UNIQUE'");
				}
			}
		else {
			//newAlert("UPDATE "+tableName+" SET "+colNames[col]+" = "+value+" WHERE "+colNames[constraint]+" = "+rowValues[constraint]+"constraint = "+constraint);
			var success = QuickUpdate.update(tableName,colNames[col],value,colNames[constraint],rowValues[constraint]);
			if(success) {
				OKStatus("OK","Table Sucessfully Updated");
				changeTD(ele,value); 
			}
		}
	}
	//newAlert(!checkforDuplicate(row,col,value)+"<br>"+value != prevVal);
	else {
			newAlert("The value : '"+value+"' cannot be inserted into '"+colNames[col]+"' : '"+datatype+"'");
			AlertStatus(datatype+" NULL = "+Null+", "+uni+pri,"The value : '"+value+"' cannot be inserted into '"+colNames[col]+"' : '"+datatype+"'");
		}
	}
	else if(event.ctrlKey && event.keyCode === 16) {
		var td = document.getElementById(ele);
		changeTD(ele,prevVal);
		OKStatus(td.getAttribute("data-type")+", NULL = "+td.getAttribute("data-null")+", "+uni+pri,"OK");
	}
}


function changeTD(ele,value) {
	var td = document.getElementById(ele);
	td.innerHTML = "<pre>"+value+"</pre>";
	allFalse();
}

function getUnique() {
	var table = document.getElementById('myTable');
	var uniqueKeys = table.getAttribute("data-unique");
	if(uniqueKeys.indexOf(" ") === -1) {
		unique.push(uniqueKeys);
	}
	else {
		unique = uniqueKeys.split(" ");
	}
}

function inArray(val,arr) {
	for(var i = 0; i < arr.length; i++)
		if(arr[i] == val)
			return true;
	return false;
}

function addToClicked() {
	var table = document.getElementById("myTable");
	var tr = table.getElementsByTagName("tr");
	for (i = 0; i < tr.length; i++) {
		var tdi = tr[i].getElementsByTagName("td");
		for(j = 0; j < tdi.length; j++) {
			var td = tdi[j];
				clicked[td.id] = false;
		}
	}
}

function newAlert(message) {
	document.getElementById("message").innerHTML = message;
	document.getElementById("dialog").style.display = "block";
}

function AlertStatus(type,status) {
	var statusBar = document.getElementsByClassName("status-bar")[0];
	statusBar.style.borderTop = '1px solid gold';
	statusBar.style.backgroundColor = 'lightyellow';
	statusBar.style.color = 'darkorange';
	document.getElementById('typeChosen').innerHTML = type;
	document.getElementById('status').innerHTML = status;
}

function OKStatus(type,status) {
	var statusBar = document.getElementsByClassName("status-bar")[0];
	statusBar.style.borderTop = '1px solid green';
	statusBar.style.backgroundColor = 'lightgreen';
	statusBar.style.color = 'green';
	document.getElementById('typeChosen').innerHTML = type;
	document.getElementById('status').innerHTML = status;
}

function allTrue() {
	for(var i in clicked)
		clicked[i] = true;
}

function allFalse() {
	for(var i in clicked)
		clicked[i] = false;
}

function checkForReadOnly() {
	if(primaryKey === -1 && unique[0] == "") {
		allTrue();
		AlertStatus("ReadOnly","Cells can't be edited");
	}
}

function checkforDuplicate(row, col, value) {
	var table = document.getElementById('myTable');
	var tr = table.getElementsByTagName('tr');
	for(var i = 1; i < tr.length; i++) {
		if(i === (row)) {
			i++;
			if(i == tr.length) break;
		}
		else;
		var td = tr[i].getElementsByTagName('td')[col];
		var pre = td.getElementsByTagName('pre')[0];
		//(pre.innerHTML);
		if(pre.innerHTML == value) {
			return true;
		}
	}
	return false;
}

function getColumnNames() {
	var columns = [];
	var table = document.getElementById('myTable');
	var th = document.getElementsByTagName('tr')[0].getElementsByTagName('th');
	for(var i = 0; i < th.length; i++)
		columns.push(th[i].innerHTML);
	return columns;
}

function getRowValues(row) {
	var values = [];
	var table = document.getElementById('myTable');
	var tr = table.getElementsByTagName('tr')[row+1];
	var td = tr.getElementsByTagName('td');
	for(var i = 0; i < td.length; i++) {
		var pre = td[i].getElementsByTagName('pre')[0];
		values.push(pre.innerHTML);
	}
	return values;
}

function isItAllNewLine(str) {
	for(var i in str)
		if(str[i] != "\n")
			return false;
	return true;
}

/**   Begin type checking section   **/

function findTypeCheckPara(dataType) {
	var type = "";
	var args1 = "";
	var args2 = "";
	if(dataType.indexOf("(") > -1) {
		var parts = dataType.split("(");
		type = parts[0];
		if(parts[1].indexOf(",") > -1) {
			var params = parts[1].split(",");
			args1 = getNumber(params[0]);
			args2 = getNumber(params[1]);
		}
		else {
				args1 = getNumber(parts[1])
		}
	}
	else {
		type = dataType;
	}
	return args1 == "" && args2 == "" ? [type] : args2 == "" ? [type,args1] : [type,args1,args2];
}

function checktype(datatype,value) {
	var type = datatype[0];
	var args = findParas(datatype);
	var args1 = Number(args[0]);
	var args2 = Number(args[1]);
	var checked;
	switch(type) {
		case "int" :
			checked = int(value,args1);
			break;
		case "bigint" :
			checked = bigint(value,args1);
			break;
		case "smallint" :
			checked = smallint(value,args1);
			break;
		case "tinyint" :
			checked = tinyint(value,args1);
			break;
		case "bit" :
			checked = tinyint(value);
			break;
		case "decimal" :
		case "numeric" :
			checked = decimal(value,args1,args2);
			break;
		case "float" :
			checked = float(value,args1,args2)
			break;
		case "real" :
		case "double" :
			checked = real(value,args,args2);
			break;
		case "char" :
			checked = char(value,args1);
			break;
		case "varchar" :
		 checked = varchar(value,args1);
		 break;
		case "text" :
			checked = text(value);
			break;
		default :
			checked = false;
	}
	return checked;
}


function findParas(datatype) {
	if(datatype.length == 1)
		return ["-1","-1"];
	else if (datatype.length == 2)
		return [datatype[1],"-1"];
	else
		return [datatype[1],datatype[2]];
}

function getValueOfPre(id) {
	var td = document.getElementById(id);
	var value = td.getElementsByTagName('pre')[0].innerHTML;
	return value;
}


/**                                 **/

/** The section for type checking - Begin **/
/**                                       **/
/**                                       **/

		/**   Number Fetching - Begin         **/
		/**                                   **/
			function isNumeral(ele) {
			  return ele >= '0' && ele <= '9';
			}

			function getNumber(sliceExpr) {
			  var i = 0;
			  while( i < sliceExpr.length && isNumeral(sliceExpr[i]))
			    i++;
			  return sliceExpr.slice(0,i);
			}

			function isNum(chunk) {
					var i = 0;
					i += (chunk[0] == "-" ? 1 : 0);
			    for(; i < chunk.length; i++) {
			      if(!(isNumeral(chunk[i])))
			        return false;
					}
			    return true;
			}
		/**   Number Fetching - End           **/
		/**                                   **/


		function int(value,length) {
			var num = 0;
			var compare = 11;
			compare = value[0] == "-" ? 12 : 11;
			length = value[0] == "-" ? length + 1 : length;
			if(value.length > compare || value.length > length)
				return false;
			var isInt = isNum(value);
			if(isInt) {
				num = Number(value);
				isInt = num >= -2147483648 && num <= 2147483647;
			}
			return isInt;
		}

		function bigint(value,length) {
			var num1 = 0;
			var num2 = 0;
			var num = 0;
			var compare = 20;
			compare = value[0] == "-" ? 20 : 21;
			length = value[0] == "-" ? length + 1 : length;
			if(value.length > compare || value.length > length)
				return false;
			var isBigInt = isNum(value);
			var i = (value[0] == "-" ? 1 : 0);
			var comparison = (value[0] == "-" ? 854775808 : 854775807);
			if(isBigInt && value.length < 16) {
				num = Number(value);
				isBigInt = num >= -922337203685477 && num <= 922337203685477;
			}
			else if(isBigInt && (value.length > 16 && value.length <= 20)) {
				num1 = Number(value.slice(i,i+10));
				num2 = Number(value.slice(i+10,value.length));
				isBigInt = num1 <= 9223372036;
				isBigInt = num2 <= comparison;
			}
			else {
				isBigInt = false;
			}
			return isBigInt;
		}

		function smallint(value, length) {
			var num = 0;
			var compare = 7;
			compare = value[0] == "-" ? 8 : 7;
			length = value[0] == "-" ? length + 1 : length;
			if(value.length > compare || value.length > length)
				return false;
			var isSmallInt = isNum(value);
			if(isSmallInt) {
				num = Number(value);
				isSmallInt = num >= -32768 && num <= 32767;
			}
			return isSmallInt;
		}

		function tinyint(value, length) {
			var num = 0;
			var compare = 3;
			compare = value[0] == "-" ? 4 : 3;
			length = value[0] == "-" ? length + 1 : length;
			if(value.length > compare || value.length > length)
				return false;
			var isTinyInt = isNum(value);
			if(isTinyInt) {
				num = Number(value);
				isTinyInt = num >= 0 && num <= 255;
			}
			return isTinyInt;
		}

		function bit(value) {
			if(value.length != 1)
				return false;
			return value == "1" || value == "0";
		}

		function decimal(value,length,fraction) {
			var num = 0;
			if(value.length > length+1)
				return false;
			var isDecimal = true;
			if(value.indexOf(".") > -1) {
				var parts = value.split(".");
				var whole = parts[0]
				var dec = parts[1];
				isDecimal = whole.length <= (length - fraction) && isNum(whole) && isNum(dec);
			}
			else {
				isDecimal = value.length <= (length - fraction) && isNum(value);
			}
			if(isDecimal) {
				num = Number(value);
				isDecimal = num >= -Math.pow(10,38) && num <= Math.pow(10,38);
			}
			return isDecimal;
		}

		function float(value,length,fraction) {
			var num = 0;
			length = length == -1 ? 50 : length;
			if(value.length > length+1)
				return false;
			var isFloat = true;
			if(value.indexOf(".") > -1) {
				var parts = value.split(".");
				var whole = parts[0]
				var dec = parts[1];
				isFloat = whole.length <= (length - fraction) && isNum(whole) && isNum(dec);
			}
			else {
				isFloat = value.length <= (length - fraction) && isNum(value);
			}
			if(isFloat) {
				num = Number(value);
				isFloat = num >= -Math.pow(1.73,38) && num <= Math.pow(1.73,38);
			}
			return isFloat;
		}

		function real(value,length,fraction) {
			var num = 0;
			length = length == -1 ? 50 : length;
			if(value.length > length+1)
				return false;
			var isReal = true;
			if(value.indexOf(".") > -1) { 
				var parts = value.split(".");
				var whole = parts[0]
				var dec = parts[1];
				isReal = whole.length <= (length - fraction) && isNum(whole) && isNum(dec);
			}
			else {
				isReal = value.length <= (length - fraction) && isNum(value);
			}
			if(isReal) {
				num = Number(value);
				isReal = num >= -Math.pow(1.73,38) && num <= Math.pow(1.73,38);
			}
			return isReal;
		}
       
		function char(value, length) {
			if(value.length > length)
				return false;
			return true;
		}

		function varchar(value, length) {
			if(value.length > length)
				return false;
			return true;
		}

		function text(value) {
			if(value.length > 2147483647)
				return false;
			return true;
		}
/**  The section for type checking - End  **/
/**                                       **/
/**                                       **/
