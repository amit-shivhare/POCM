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


function cancelMessager() {
	messageNotification("0&nbsp;^");
}

