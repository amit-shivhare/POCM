function getChannels(p, s, c, action) {
  var xhttp = null;
  var ts = new Date().getTime();
  if (window.XMLHttpRequest) {
	  xhttp = new XMLHttpRequest();
  }
  else xhttp = new ActiveXObject("Microsoft.XMLHTTP");
  var select = "/AdapterFramework/ChannelAdminServlet?party="+p+"&service=+"+s+"+&channel="+c+"&action="+action+"&nocache="+ts;
  xhttp.open("GET", select, false);
  xhttp.setRequestHeader("Cache-control", "no-cache");
  xhttp.send();
  return xhttp.responseText;
}

function processNextElement(element, string) {
  i = string.indexOf(element);
  if(i<0) return "not-found";
  else {
    decisionchar = string.substring(i+element.length, i+element.length+1);
    if(decisionchar=="/") return ("<->" + string.substring(i+1));
    else {
      subsubstring = string.substring(i+element.length+1);
      endindex = subsubstring.indexOf("</"+element); 
      value = subsubstring.substring(0, endindex);
      return value+"<->"+subsubstring.substring(endindex+3);
    }
  } 
}

function getElement(pResult) {
  i = pResult.indexOf("<->");
  if(i<0) return pResult;
  else return pResult.substring(0, i);
}

function getSubstring(pResult) {
  i = pResult.indexOf("<->");
  if(i<0) return pResult;
  else return pResult.substring(pResult.indexOf("<->")+3);
}

function readChannels(p, s, c, action) {
  var retlist = [];
  chxml = getChannels(p, s, c, action);
  if(processNextElement("Party", chxml) == "not/found") return null;
  else {
    pres = processNextElement("Party", chxml); 
    party = getElement(pres);
    chxml = getSubstring(pres);
    pres = processNextElement("Service", chxml); 
    service = getElement(pres);
    chxml = getSubstring(pres);
    pres = processNextElement("ChannelName", chxml); 
    chname = getElement(pres);
    chxml = getSubstring(pres);
    pres = processNextElement("ActivationState", chxml); 
    astate = getElement(pres);
    if(astate=="STARTED") astate = "0";
    else
        if(astate=="STOPPED") astate = "1";
        else
            if(astate=="INACTIVE") astate = "2";
            else
                astate = "9";
    chxml = getSubstring(pres);
    if(astate=='0') {
        pres = processNextElement("ChannelState", chxml); 
        chstate = getElement(pres);
        if(chstate=="OK") chstate = "0";
        else
            if(chstate=="ERROR") chstate = "1";
            else
                if(chstate=="INACTIVE") chstate = "2";
                else
                    chstate = "9";
        chxml = getSubstring(pres);
    }
    else chstate = "2";
    while(party!="not-found") {
      retlist.push("P-|"+party+"|-PS-|"+service+"|-SC-|"+chname+"|-C1-|"+astate+"|-12-|"+chstate+"|-2");
      pres = processNextElement("Party", chxml); 
      party = getElement(pres);
      chxml = getSubstring(pres);
      pres = processNextElement("Service", chxml); 
      service = getElement(pres);
      chxml = getSubstring(pres);
      pres = processNextElement("ChannelName", chxml); 
      chname = getElement(pres);
      chxml = getSubstring(pres);
      pres = processNextElement("ActivationState", chxml); 
      astate = getElement(pres);
      if(astate=="STARTED") astate = "0";
      else
          if(astate=="STOPPED") astate = "1";
          else
              if(astate=="INACTIVE") astate = "2";
              else
                  astate = "9";
      chxml = getSubstring(pres);
      if(astate=='0') {
          pres = processNextElement("ChannelState", chxml); 
          chstate = getElement(pres);
          if(chstate=="OK") chstate = "0";
          else
              if(chstate=="ERROR") chstate = "1";
              else
                  if(chstate=="INACTIVE") chstate = "2";
                  else
                      chstate = "9";
          chxml = getSubstring(pres);
      }
      else chstate = "2";
    }
  }
  retlist.sort();
  return retlist;
}  

function getValue(identifier, string) {
	var i = string.indexOf(identifier + "-|");
	if(i<0) return null;
	else {
		var substr = string.substring(i+3);
		i = substr.indexOf("|-" + identifier);
		if(i<0) return null;
		else return substr.substring(0, i);
	}
}

function displayChannels_(p, s, c, action, list) {
	  var channellist = readChannels(p, s, c, action);
	  var retstr = "<form name='ch'><table style=\"border-collapse: collapse\"><tr><td class=\"hdr\" style=\"width: 40px;\">Sel</td><td class=\"hdr\">Party</td><td class=\"hdr\">Service</td><td class=\"hdr\">Communication&nbsp;Channel</td><td class=\"hdr\">S1</td><td class=\"hdr\">S2</td></tr>";
	  var cc_s_00 = 0; //started/OK
	  var cc_s_01 = 0; //started/err
	  var cc_s_02 = 0; //started/inactive
	  var cc_s_12 = 0; //stopped/inactive
	  var cc_s_99 = 0; //unknown
	  for(i=0; i<channellist.length; i++) {
		var h = (i+1)%2 + 1;
		var cl = "c" + h;
	    retstr += "<tr class=\""+cl+"\">";
	    var party = getValue("P", channellist[i]);
	    var service = getValue("S", channellist[i]);
	    var channel = getValue("C", channellist[i]);
	    var status = getValue("1", channellist[i]);
	    var state  = getValue("2", channellist[i]);
	    var checked = "";
	    if(list.indexOf("-|" + party + "/" + service + "/" + channel + "|-")>-1) checked = "CHECKED";
	    var pparty = party;
	    if(pparty=="") pparty="null";
	    var value = channellist[i].replace("P-|", "<td class=\"pic\"><input type=\"checkbox\" name=\"ch_" +pparty+service+channel+ "\" value=\""+party+"/"+service+"/"+channel+ "/" + status + state + "\" " + checked + "></td><td width=\"150\">&nbsp;").replace("|-P", "</td>").replace("S-|", "<td width=\"220\">&nbsp;").replace("|-S", "</td>").replace("C-|", "<td width=\"420\">&nbsp;").replace("|-C", "</td>");

	    if(status=="0") {
	    	if(state=="0") cc_s_00++;
	    	else
	    	if(state=="1") cc_s_01++;
	    	else
	    	if(state=="2") cc_s_02++;
	    	else
	    	if(state=="9") cc_s_99++;
	    }
	    else
	    if(status=="1") cc_s_12++;
	    else
	    	cc_s_99++
	    	
	    value = value.replace("1-|0|-1", "<td class=\"pic\"><img src=\"images/started.png\" alt=\"Started\"/></td>");
	    value = value.replace("1-|1|-1", "<td class=\"pic\"><img src=\"images/stopped.png\" alt=\"Stopped\"/></td>");
	    value = value.replace("1-|2|-1", "<td class=\"pic\"><img src=\"images/inactive.png\" alt=\"Inactive\"/></td>");
	    value = value.replace("1-|9|-1", "<td class=\"pic\"><img src=\"images/unknown.png\" alt=\"Unknown\"/></td>");
	    value = value.replace("2-|0|-2", "<td class=\"pic\"><img src=\"images/ok.png\" alt=\"OK\"/></td>");
	    value = value.replace("2-|1|-2", "<td class=\"pic\"><img src=\"images/error.png\" alt=\"Error\"/></td>");
	    value = value.replace("2-|2|-2", "<td class=\"pic\"><img src=\"images/inactive.png\" alt=\"Inactive\"/></td>");
	    value = value.replace("2-|9|-2", "<td class=\"pic\"><img src=\"images/unknown.png\" alt=\"Unknown\"/></td>");
	    retstr += value;
	    retstr += "</tr>";
	  }	  
	  retstr += "</table></form>";
	  
	  var ccstatus = "<table style=\"border-collapse: collapse\">";
	  ccstatus += "<tr class=\"c2\"><td style=\"width: 130px;\" class=\"sx\">Channel Count:</td><td style=\"width: 50px;\"></td><td style=\"width: 30px; text-align: right;\" class=\"sx\">" + channellist.length + "&nbsp;</td></tr>";
	  ccstatus += "<tr class=\"c2\"><td class=\"sx\">Started/OK</td><td class=\"pic\"><img src=\"images/started.png\" alt=\"Started\"/>&nbsp;<img src=\"images/ok.png\" alt=\"OK\"/></td><td style=\"text-align: right;\" class=\"sx\">" + cc_s_00 + "&nbsp;</td></tr>";
	  ccstatus += "<tr class=\"c2\"><td class=\"sx\">Started/Error</td><td class=\"pic\"><img src=\"images/started.png\" alt=\"Started\"/>&nbsp;<img src=\"images/error.png\" alt=\"Error\"/></td><td style=\"text-align: right;\" class=\"sx\">" + cc_s_01 + "&nbsp;</td></tr>";
	  ccstatus += "<tr class=\"c2\"><td class=\"sx\">Started/Inactive</td><td class=\"pic\"><img src=\"images/started.png\" alt=\"Started\"/>&nbsp;<img src=\"images/inactive.png\" alt=\"Inactive\"/></td></td><td style=\"text-align: right;\" class=\"sx\">" + cc_s_02 + "&nbsp;</td></tr>";
	  ccstatus += "<tr class=\"c2\"><td class=\"sx\">Stopped/Inactive</td><td class=\"pic\"><img src=\"images/stopped.png\" alt=\"Stopped\"/>&nbsp;<img src=\"images/inactive.png\" alt=\"Inactive\"/></td></td><td style=\"text-align: right;\" class=\"sx\">" + cc_s_12 + "&nbsp;</td></tr>";
	  ccstatus += "<tr class=\"c2\"><td class=\"sx\">Unknown Status</td><td class=\"pic\"><img src=\"images/unknown.png\" alt=\"Unknown\"/>&nbsp;<img src=\"images/unknown.png\" alt=\"Unknown\"/></td></td><td style=\"text-align: right;\" class=\"sx\">" + cc_s_99 + "&nbsp;</td></tr>";
	  ccstatus += "</table";
	  document.getElementById("ccstatus").innerHTML = ccstatus;
	  
	  return retstr;
	}

function displayChannelsSetup(p, s, c, action, list) {
	  var channellist = readChannels(p, s, c, action);
	  var retstr = "<form name='ch'><table style=\"border-collapse: collapse\"><tr><td class=\"hdr\">Party</td><td class=\"hdr\">Service</td><td class=\"hdr\">Communication&nbsp;Channel</td><td class=\"hdr\">Identifiers</td></tr>";
	  var separator = "->";
	  for(i=0; i<channellist.length; i++) {
		var h = (i+1)%2 + 1;
		var cl = "c" + h;
	    retstr += "<tr class=\""+cl+"\">";
	    var party = getValue("P", channellist[i]);
	    var service = getValue("S", channellist[i]);
	    var channel = getValue("C", channellist[i]);
	    var cnl = channellist[i].substring(0, channellist[i].indexOf("1-|"));
	    var value1 = cnl.replace("P-|", "<td width=\"320\">&nbsp;").replace("|-P", "</td>").replace("S-|", "<td width=\"350\">&nbsp;").replace("|-S", "</td>").replace("C-|", "<td width=\"460\">&nbsp;").replace("|-C", "</td>");
	    var vname = "chn_" + cnl.replace("P-|", "").replace("S-|", "").replace("C-|", "").replace("|-P", separator).replace("|-S", separator).replace("|-C", separator);
	    vname = vname.replace("chn_->", "chn_null->");
	    var value2 = "<td><input type='text' name='" + vname + "' value='' size='50' /></td>";
	    retstr += value1 + value2;
	    retstr += "</tr>";
	  }	  
	  retstr += "</table></form>";
	  return retstr;
	}

function countChannels() {
	var number = 0;
	for(i=0; i<document.ch.length; i++) {
		  if(document.ch.elements[i].checked) number++;
	}
    return number;
}

function modifySelectedChannels(action, p, s, c) {
    modifySelectedChannels_(action, p, s, c, 0);
}

var processingIndex = 0;
var changedCCList = "";

function modifySelectedChannels_(action, p, s, c, pi) {
	  if(pi==0) {
		  processingIndex = 0;
		  changedCCList = "";
		  if(!confirm('You are going to manipulate ' + countChannels() + ' channels. Is it OK?')) {
			  hideProgressBox(1);
			  return false;
		  }
	  }

	  var length = document.ch.length;
	  if(processingIndex<length) {
		  if(document.ch.elements[processingIndex].checked) {
			  var value = document.ch.elements[processingIndex].value;
			  var k = value.indexOf("/");
			  var party = value.substring(0, k);
			  value = value.substring(k+1);
			  k = value.indexOf("/");
			  var service = value.substring(0, k);
			  value = value.substring(k+1);
			  k = value.indexOf("/");
			  var channel = value.substring(0, k);
			  getChannels(party, service, channel, action);
			  changedCCList += "-|" + party + "/" + service + "/" + channel + "|-";
		  }
		  processingIndex++;
		  setTimeout(function() { modifySelectedChannels_(action, p, s, c, processingIndex); }, 0);
		  var perc = length / 100;
		  perc = processingIndex / perc;
		  displayProgressBox(Math.round(perc));
	  }
	  else {
		  document.getElementById('content').innerHTML = displayChannels_(p, s, c, "status", changedCCList);
		  hideProgressBox(0);
		  return false;
	  }
	  
}

function selectionAll() {
	var number = 0;
	  for(i=0; i<document.ch.length; i++) {
		  document.ch.elements[i].checked=true;
		  number++;
	  }
	  messageNotification("1Number of channels selected: " + number + "^");
}

function selectionInvert() {
	var number = 0;
	  for(i=0; i<document.ch.length; i++) 
		  if(document.ch.elements[i].checked) {
			  document.ch.elements[i].checked=false;
		  }
		  else {
			  document.ch.elements[i].checked=true;
			  number++;
		  }
	  messageNotification("1Number of channels selected: " + number + "^");
}

function selectionStarted() {
	var number = 0;
	  for(i=0; i<document.ch.length; i++) 
		  if(document.ch.elements[i].value.substring(document.ch.elements[i].value.length-2, document.ch.elements[i].value.length-1)=='0') {
			  document.ch.elements[i].checked=true;
			  number++;
		  }
		  else document.ch.elements[i].checked=false;
	  messageNotification("1Number of channels selected: " + number + "^");
}

function selectionClear() {
	  for(i=0; i<document.ch.length; i++) 
		  document.ch.elements[i].checked=false;
	  messageNotification("2Selection has been cleared.^");
}

function messageNotification(m) {
	var message = "";
	var nindex = m.indexOf("^")
	while(nindex>-1) {
		message = m.substring(0, nindex);
		m = m.substring(nindex + 1);
		nindex = m.indexOf("^");
		var cl = message.substring(0, 1);
		if(cl=="0" || cl=="1" || cl=="2" || cl=="3") {
			document.getElementById("messager").className = "message" + cl;
			message = message.substring(1);
		}
		document.getElementById("messager").innerHTML = message;
	}
	return m;
}

function saveList(plist) {
    var xmlHttpReq;
    var ts = new Date().getTime();
    var url = "savelist.jsp?ts=" + ts;
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    else xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    xmlHttpReq.open('POST', url, false);
    xmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpReq.send("list=" + plist);
    messageNotification(xmlHttpReq.responseText);
}

function displaySelectionList() {
  var res = "<table style=\"border-collapse: collapse\"><tr><td class=\"hdr\" style=\"width: 280px;\">Party</td><td class=\"hdr\" style=\"width: 280px;\">Service</td><td class=\"hdr\" style=\"width: 360px;\">Communication&nbsp;Channel</td></tr>";
  var final = "";
  var k = 0;
  for(i=0; i<document.ch.length; i++) {
	var ob = document.ch.elements[i];
	if(ob.checked) {
		var c = k++ % 2;
		c++;
		cl = "c" + c;
		final += "<tr class=\"" + cl + "\" style=\"height: 20px;\"><td>&nbsp;";
		var hs = ob.value.substring(0, ob.value.lastIndexOf("/")).replace("/", "^^^").replace("/", "^^^").replace("/", "^^^");
		final += hs.replace("^^^", "</td><td>&nbsp;").replace("^^^", "</td><td>&nbsp;").replace("^^^", "</td><td>&nbsp;");
		final += "</td></tr>";
	}
  }
  if(final=="") res = "No channel is selected.";
  else {
	  res += final; 
      res += "</table>";
  }
  displayInLogBox("List of Selected Channels", "1000", res);
  return false;
}

function displayInLogBox(label, width, content) {
	var ob = document.getElementById("logBox");
	document.getElementById("logBoxContent").innerHTML = content;
	document.getElementById("lbLabel").innerHTML = label;
	ob.style.width = width;
	ob.style.display = "block";
}

function saveSelection() {
	var filename = prompt("Save Selection As", "");
	if(filename==null) return false;
	var content = "";
    var xmlHttpReq;
    var ts = new Date().getTime();
    for(i=0; i<document.ch.length; i++) {
    	var ob = document.ch.elements[i];
    	if(ob.checked) {
    		content += ob.value + "-NIT-";
    	}
    }
    var url = "saveSelection.jsp?ts=" + ts;
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    else xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    xmlHttpReq.open('POST', url, false);
    xmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    var con = "filename="+filename+"&path="+document.getElementById("path").innerHTML + "&content=" + content;
    xmlHttpReq.send(con);
    messageNotification(xmlHttpReq.responseText);
}

function saveSnapshot() {
	var filename = prompt("Save Snapshot As", "");
	if(filename==null) return false;
	var content = "";
    var xmlHttpReq;
    var ts = new Date().getTime();
    for(i=0; i<document.ch.length; i++) {
    	ob = document.ch.elements[i];
   		content += ob.value + "-NIT-";
    }
    var url = "saveSnapshot.jsp?ts=" + ts;
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    else xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    xmlHttpReq.open('POST', url, false);
    xmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    var con = "filename="+filename+"&path="+document.getElementById("path").innerHTML + "&content=" + content;
    xmlHttpReq.send(con);
    messageNotification(xmlHttpReq.responseText);
}

function restoreSelection(filename) {
	document.getElementById("logBox").style.display = "none";
    var res = "<table style=\"border-collapse: collapse\"><tr><td class=\"hdr\" style=\"width: 280px;\">Party</td><td class=\"hdr\" style=\"width: 280px;\">Service</td><td class=\"hdr\" style=\"width: 360px;\">Communication&nbsp;Channel</td><td class=\"hdr\" style=\"width: 80px;\">Status</td></tr>";
	var message = "";
	var k = 0;
	var exist = 0;
	var nexist = 0;
	if(filename==null) return false;
	var content = "";
    var xmlHttpReq;
    var ts = new Date().getTime();
    var url = "restoreSelection.jsp?ts=" + ts;
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    else xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    var con = "filename="+filename+"&path="+document.getElementById("path").innerHTML + "&content=" + content;
    xmlHttpReq.open('POST', url, false);
    xmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpReq.send(con);
    content = messageNotification(xmlHttpReq.responseText);
    if(content!=null && content!="null" && content!="") {
        var l = content.split("-NIT-");
        selectionClear();
        for(i=0; i<l.length-1; i++) {
        	var found = false;
        	var processed = l[i].substring(0, l[i].length-3);
            for(j=0; j<document.ch.length; j++) {
            	var ob = document.ch.elements[j]
            	var obval = ob.value.substring(0, ob.value.length-3);
            	if(obval==processed) {
            		ob.checked = true;
            		found = true;
            		break;
            	}
            }
        	if(!found) {
        		var c = k++ % 2;
        		c++;
        		cl = "c" + c;
        		res += "<tr class=\"" + cl + "\" style=\"height: 20px;\"><td>&nbsp;";
        		var hs = l[i].substring(0, l[i].lastIndexOf("/")).replace("/", "^^^").replace("/", "^^^").replace("/", "^^^");
        		res += hs.replace("^^^", "</td><td>&nbsp;").replace("^^^", "</td><td>&nbsp;").replace("^^^", "</td><td>&nbsp;");
        		res += "</td><td class=\"pic\" style=\"color: white; background-color: red;\">MISSING</td></tr>";
        		nexist++;
        	}
        	else exist++;
        }
        message = "2Selection has been sucessfully restored. Number of channels selected: " + exist + "^";
        if(nexist>0) {
        	res += "</table>";
        	message = "1Selection has been partially restored. Number of channels selected: "+ exist+ ", not-existing channels: " + nexist + "&nbsp;&nbsp;<a style=\"color: blue; text-decoration: none;\" href=\"javascript:\"  onClick=\"document.getElementById('logBox').style.display='block';\">[Details...]</a>^";
        	displayInLogBox("Missing Communication Channels", "1060", res);
        }
    }
    else 
    	if(xmlHttpReq.responseText==""){
            selectionClear();
    		message = "1There was no channel to be selected according to selection '" + filename + "'!^";
    	}
    messageNotification(message);
}

function compareSnapshot(filename) {
	document.getElementById("logBox").style.display = "none";
    var result = "<table style=\"border-collapse: collapse\"><tr><td class=\"hdr\" style=\"width: 280px;\">Party</td><td class=\"hdr\" style=\"width: 280px;\">Service</td><td class=\"hdr\" style=\"width: 360px;\">Communication&nbsp;Channel</td><td class=\"hdr\" style=\"width: 80px;\">Snapshot</td><td class=\"hdr\" style=\"width: 80px;\">Current</td></tr>";
    var res = "";
    var message = "";
	var k = 0;
	var exist = 0;
	var nexist = 0;
	if(filename==null) return false;
	var content = "";
    var xmlHttpReq;
    var ts = new Date().getTime();
    var url = "getSnapshot.jsp?ts=" + ts;
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    else xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    var con = "filename="+filename+"&path="+document.getElementById("path").innerHTML + "&content=" + content;
    xmlHttpReq.open('POST', url, false);
    xmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpReq.send(con);
    content = messageNotification(xmlHttpReq.responseText);

	var counter = 0;
    for(i=0; i<document.ch.length; i++) {
    	
    	var hc = (counter % 2) + 1;
    	var cl = "c" + hc;

	    var c_item = document.ch.elements[i].value;
    	var c_item_name = c_item.substring(0, c_item.length-3);
    	var c_item_status = c_item.substring(c_item.length-2);
    	var s_item_status = "";
    	
    	var index = content.indexOf(c_item_name);
    	if(index>-1) {
    		var p1 = content.substring(0, index);
    		var p2 = content.substring(index);
    		index = p2.indexOf("-NIT-");
    		var s_item = p2.substring(0, index);
    		p2 = p2.substring(index+5);
    		content = p1 + p2;
    		s_item_status = s_item.substring(s_item.length-2);
    	}
    	else {
    		s_item_status = "NA";
    	}

    	//check each item
    	
    	var line_ = createCompareLine(c_item_name, s_item_status, c_item_status, cl);
    	if(line_!="") counter++;
    	res += line_;
    }
	
	//remaining missing items in current
	var remaining = content.split("-NIT-");
	for(i=0; i<remaining.length; i++) {

    	var hc = (counter % 2) + 1;
    	var cl = "c" + hc;

    	var s_item = remaining[i];
    	var s_item_name = s_item.substring(0, s_item.length-3);
    	var s_item_status = s_item.substring(s_item.length-2);
		if(remaining[i]!="") {
			res += createCompareLine(s_item_name, s_item_status, "NA", cl);
		}
		counter++;
	}
	
	if(res=="") result = "There are no differences between statuses of the snapshot and currently displayed channels.";
	else {
		result += res
		result += "</table>";
	}
	counter--;
    displayInLogBox("Comparison to Snapshot: " + filename + " (Differences: " + counter + ")", "1150", result);
}

function createCompareLine(name, p_old, p_new, p_cl) {
	var res = "";
	if(p_old==p_new) return "";
	else {

		res += "<td>&nbsp;";
		name = name.replace("/", "^^^").replace("/", "^^^").replace("/", "^^^");
		res += name.replace("^^^", "</td><td>&nbsp;").replace("^^^", "</td><td>&nbsp;").replace("^^^", "</td><td>&nbsp;");
		res += "</td>";
		
		var oldstatus1 = p_old.substring(0,1);
		var oldstatus2 = p_old.substring(1);
		
		if(oldstatus1=="0")
			oldstatus1 = "<img src=\"images/started.png\" alt=\"Started\"/>";
		else if (oldstatus1=="1")
			oldstatus1 = "<img src=\"images/stopped.png\" alt=\"Stopped\"/>";
		else if (oldstatus1=="2")
			oldstatus1 = "<img src=\"images/inactive.png\" alt=\"Inactive\"/>";
		else if (oldstatus1=="9")
			oldstatus1 = "<img src=\"images/unknown.png\" alt=\"Unknown\"/>";
		else if (oldstatus1=="N")
			oldstatus1 = "MISSING";
		
		if(oldstatus2=="0")
			oldstatus2 = "<img src=\"images/ok.png\" alt=\"OK\"/>";
		else if (oldstatus2=="1")
			oldstatus2 = "<img src=\"images/error.png\" alt=\"Error\"/>";
		else if (oldstatus2=="2")
			oldstatus2 = "<img src=\"images/inactive.png\" alt=\"Inactive\"/>";
		else if (oldstatus2=="9")
			oldstatus2 = "<img src=\"images/unknown.png\" alt=\"Unknown\"/>";
		else if (oldstatus2=="A")
			oldstatus2 = "";
		
		if(oldstatus1=="MISSING")
			res += "<td class=\"pic\" style=\"color: white; background-color: red;\">MISSING</td>";
		else
			res += "<td class=\"pic\">" + oldstatus1 + "&nbsp" + oldstatus2 + "</td>";
		
		
		var oldstatus1 = p_new.substring(0,1);
		var oldstatus2 = p_new.substring(1);
		
		if(oldstatus1=="0")
			oldstatus1 = "<img src=\"images/started.png\" alt=\"Started\"/>";
		else if (oldstatus1=="1")
			oldstatus1 = "<img src=\"images/stopped.png\" alt=\"Stopped\"/>";
		else if (oldstatus1=="2")
			oldstatus1 = "<img src=\"images/inactive.png\" alt=\"Inactive\"/>";
		else if (oldstatus1=="9")
			oldstatus1 = "<img src=\"images/unknown.png\" alt=\"Unknown\"/>";
		else if (oldstatus1=="N")
			oldstatus1 = "MISSING";
		
		if(oldstatus2=="0")
			oldstatus2 = "<img src=\"images/ok.png\" alt=\"OK\"/>";
		else if (oldstatus2=="1")
			oldstatus2 = "<img src=\"images/error.png\" alt=\"Error\"/>";
		else if (oldstatus2=="2")
			oldstatus2 = "<img src=\"images/inactive.png\" alt=\"Inactive\"/>";
		else if (oldstatus2=="9")
			oldstatus2 = "<img src=\"images/unknown.png\" alt=\"Unknown\"/>";
		else if (oldstatus2=="A")
			oldstatus2 = "";
		
		if(oldstatus1=="MISSING")
			res += "<td class=\"pic\" style=\"color: white; background-color: red;\">MISSING</td>";
		else
			res += "<td class=\"pic\">" + oldstatus1 + "&nbsp" + oldstatus2 + "</td>";
		
		return("<tr class=\"" + p_cl + "\">" + res + "</tr>");
	}
}

function getFilesList(filetype) {
    var xmlHttpReq;
    var ts = new Date().getTime();
    var url = "getFilesList.jsp?ts=" + ts;
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    else xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    var params = "folder=" + document.getElementById("path").innerHTML + "&filetype=" + filetype; 
    xmlHttpReq.open('POST', url, false);
    xmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpReq.send(params);
    return(xmlHttpReq.responseText);	
}

function getSelections() {
    var content = messageNotification(getFilesList("selection"));
    if(content!=null && content!="null" && content!="") {
        var result = "";
    	var list = content.split("-NIT-");

    	result = "<table style=\"border-collapse: collapse\"><tr><td class=\"hdr\" style=\"width: 50px;\">Sel</td><td class=\"hdr\" style=\"width: 280px;\">Selection</td><td class=\"hdr\" style=\"width: 80px;\">Action</td></tr>";
		for(i=0; i<list.length-1; i++) {
			var c = i % 2;
			c++;
			cl = "c" + c;
			result += "<tr class=\""+cl+"\"><td class=\"pic\"><input type=\"checkbox\" name=\"ss_" + list[i] + "\" /></td><td>&nbsp;" + list[i] + "</td><td class=\"pic\"><input type=\"button\" value=\" Restore \" onClick=\"javascript:restoreSelection('"+list[i]+"');\" /></td></tr>";
		}
		result += "<tr class=\"message1\"><td class=\"pic\"><input type=\"checkbox\" name=\"ssa_delete\" onClick=\"selAll(this);\" /></td><td>&nbsp;Delete Selected Selection(s)</td><td class=\"pic\"><input type=\"button\" value=\"  Delete  \" onClick=\"javascript:delSel('selection');\" /></td></tr>";
		result += "</table>";

    }
    else result = "There are no selections available.";
    displayInLogBox("Available Selections", "480", result);
}

function selAll(o) {
	var checker = false;
	if(o.checked) checker = true;
	var l = document.lform.length;
	for(i=0; i<l; i++) 
		document.lform.elements[i].checked = checker;
}

function getSnapshots() {
    var content = messageNotification(getFilesList("snapshot"));
    if(content!=null && content!="null" && content!="") {
        var result = "";
    	var list = content.split("-NIT-");

    	result = "<table style=\"border-collapse: collapse\"><tr><td class=\"hdr\" style=\"width: 50px;\">Sel</td><td class=\"hdr\" style=\"width: 280px;\">Selection</td><td class=\"hdr\" style=\"width: 80px;\">Action</td></tr>";
		for(i=0; i<list.length-1; i++) {
			var c = i % 2;
			c++;
			cl = "c" + c;
			result += "<tr class=\""+cl+"\"><td class=\"pic\"><input type=\"checkbox\" name=\"ss_" + list[i] + "\" /></td><td>&nbsp;" + list[i] + "</td><td class=\"pic\"><input type=\"button\" value=\" Compare \" onClick=\"javascript:compareSnapshot('"+list[i]+"');\" /></td></tr>";
		}
		result += "<tr class=\"message1\"><td class=\"pic\"><input type=\"checkbox\" name=\"ssa_delete\" onClick=\"selAll(this);\" /></td><td>&nbsp;Delete Selected Snapshot(s)</td><td class=\"pic\"><input type=\"button\" value=\"  Delete  \" onClick=\"javascript:delSel('snapshot');\" /></td></tr>";
		result += "</table>";

    }
    else result = "There are no snapshots available.";
    displayInLogBox("Available Snapshots", "480", result);
}

function delSel(type) {
	if(!confirm("Do you really want to delete selected files?")) return false;
	var params = "folder=" + document.getElementById("path").innerHTML + "&filetype=" + type + "&list=";
	var l = document.lform.length;
	for(i=0; i<l; i++) {
		if(document.lform.elements[i].name.indexOf("ss_")==0) {
			if(document.lform.elements[i].checked) {
				params += document.lform.elements[i].name.substring(3) + "-NIT-"; 
			}
		}
	}
	
    var xmlHttpReq;
    var ts = new Date().getTime();
    var url = "deleteFiles.jsp?ts=" + ts;
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    else xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    xmlHttpReq.open('POST', url, false);
    xmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpReq.send(params);
    messageNotification(xmlHttpReq.responseText);
    if(type=="selection") getSelections();
    else getSnapshots();
}

var processingwin = null;

function cancelMessager() {
	messageNotification("0&nbsp;^");
}

function displayProgressBox(percentage) {
	var barwidth = window.innerWidth - 38;
	var donew = Math.floor(barwidth / 100 * percentage);
	var content = "<div id=\"done\" style=\"display: inline-block; background-color: #73B225; height: 14px;\">&nbsp;</div>";
	content += "<span id=\"pbtext\">&nbsp;" + percentage + "%</span>";
	var msgr = document.getElementById("messager");
	msgr.className = "message9";
	msgr.innerHTML = content;
	document.getElementById("done").style.width=donew + "px";
	if(percentage>95)
		document.getElementById("pbtext").innerHTML = "";
}

function hideProgressBox(p) {
	var msgr = document.getElementById("messager");
	if(p==0)
		messageNotification("2Action has been successfully finished.^");
	else
		messageNotification("1Action has been cancelled.^");
}