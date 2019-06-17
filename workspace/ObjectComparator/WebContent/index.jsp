<%@ page import="java.io.File, java.io.FileReader, java.io.BufferedReader, java.io.FileWriter, java.util.Enumeration, java.util.ArrayList, java.util.Properties" %>
<%
%>
<html>
  <head>
    <title>SAP PO Object Comparator v1.0</title>
    <link href="style.css" rel="stylesheet"/>
    <script>
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

    function composeRequest() {
	var sreq = "qc=All+software+components&underL=true&userL=&deletedL=N&xmlReleaseL=7.1&queryRequestXMLL=&result=RA_XILINK&result=NAME&result=NAMESPACE&result=VERSIONID&result=MODIFYUSER&result=MODIFYDATE&action=Start+query";
	var oTypes = document.getElementById("objectTypes"), otype, i;
    for(i = 0; i < oTypes.length; i++) {
        otype = oTypes[i];
        if (otype.selected) {
            sreq += "&types=" + otype.value;
        }
    }
    if(document.x.oname.value.length>0) sreq += "&qcActiveL0=true&qcKeyL0=NAME&qcOpL0=EQ&qcValueL0="+document.x.oname.value; 
    if(document.x.onamespace.value.length>0) sreq += "&qcActiveL0=true&qcKeyL0=NAMESPACE&qcOpL0=EQ&qcValueL0="+document.x.onamespace.value; 
	var cServers = document.getElementById("compareTo"), cServer, i;
    for(i = 0; i < cServers.length; i++) {
        cServer = cServers[i];
        if (cServer.selected) {
            sreq += "&compareTo=" + cServer.value;
        }
    }
    if(document.x.onlyDifferences.checked) sreq += "&onlyDifferences=true";
    return sreq;	
}

function callComparator() {
	var xhttp = null;
	var ts = new Date().getTime();
	if (window.XMLHttpRequest) {
		xhttp = new XMLHttpRequest();
	}
	else xhttp = new ActiveXObject("Microsoft.XMLHTTP");
	var url = "Comparator?nocache="+ts;
	xhttp.open("POST", url, true);
	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhttp.onreadystatechange = function() {//Call a function when the state changes.
	    if(xhttp.readyState == 4) {
	        processResponse(xhttp.responseText);
	    }
	}
	xhttp.send(composeRequest());
}

function processResponse(message) {
	var i = message.indexOf("ComparatorException: ");
	if(i>-1) {
		messageNotification("3" + message + "^");
		alert(i);
	}
	else {
		var i = message.indexOf("^");
		var amount = message.substring(0,i);
		messageNotification("2Processing has been finished. There were found " + amount + " objects which differ.^");
        document.getElementById("content").innerHTML = message.substring(i+1);
	}
}
    </script>
  </head>
  <body>
   <div id="label">
   		<h2>SAP PO Object Comparator v1.0</h2>
   		<img src="images/mt.jpg" style="display: inline-block; vertical-align: top; float: right;" />
   </div>
   <div class="message0" id="messager">&nbsp;</div>
   <div style="padding: 8px;">
    <div class="lined">
      <input class="exec" type="button" value=" Compare Objects " onClick="javascript:callComparator();"/>
    </div>
    <form name="x">
    <div style="padding-bottom: 5px;">
       <table style="padding-top: 5px;">
         <tr>
         <td class="sk3">Compare Objects To</td>
<td class="sk">
<select id="compareTo">
  <option value="SI3">SI3</option>
  <option value="DI3">DI3</option>
  <option value="OI3">OI3</option>
  <option value="QI3">QI3</option>
  <option value="PI3">PI3</option>
</select>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input style="vertical-align: bottom" type="checkbox" name="onlyDifferences" value="true" checked/>Display only differences</td>
         </tr>
         </table>
    </div>
    <div class="lined">
         <table>
         <tr>
             <td class="sk">Object Name</td>
             <td class="sk"><input type="text" name="oname"  onFocus="document.x.onamespace.value='';" maxlength="200" size="80" />&nbsp;<input type="button" value=" Remove " onClick="document.x.oname.value='';"/></td>
         </tr>
         <tr>
             <td class="sk">Object Namespace</td>
             <td class="sk"><input type="text" name="onamespace"  onFocus="document.x.oname.value='';" maxlength="200" size="80" />&nbsp;<input type="button" value=" Remove " onClick="document.x.onamespace.value='';"/></td>
         </tr>
         <tr>
             <td class="sk">Object Types to Compare</td>
             <td class="sk">
<select id="objectTypes" size="10" multiple>
<option value="ifmtypedef" selected>Data Type</option>
<option value="ifmtypeenh" selected>Data Type Enhancement</option>
<option value="ifmmessage" selected>Message Type</option>
<option value="ifmmessif" selected>Service Interface</option>
<option value="MAP_TEMPLATE" selected>Mapping Template</option>
<option value="XI_TRAFO" selected>Message Mapping</option>
<option value="MAPPING" selected>Operation Mapping</option>
<option value="ifmextdef" selected>External Definition</option>
<option value="ifmextmes" selected>External Message</option>
<option value="ifmfaultm" selected>Fault Message Type</option>
<option value="FOLDER" selected>Folder</option>
<option value="FUNC_LIB" selected>Function Library</option>
<option value="FUNC_LIB_PROG" selected>Function Library Program</option>
<option value="TRAFO_JAR" selected>Imported Archive</option>
<option value="RepBProcess" selected>Integration Process</option>
<option value="type">ABAP Dictionary Type</option>
<option value="processstep">Action</option>
<option value="BO_Action">Action</option>
<option value="AdapterMetaData">Adapter Metadata</option>
<option value="agent">Agent</option>
<option value="AlertCategory">Alert Category</option>
<option value="MAP_ARCHIVE_PRG">Archive Program</option>
<option value="BO_Association">Association</option>
<option value="mme_ca">Attribute</option>
<option value="mme">Attribute Definition</option>
<option value="arisminisymbol">Attribute Icon</option>
<option value="arisattribute">Attribute Type</option>
<option value="arisattrgroup">Attribute Type Group</option>
<option value="BO_Object">Business Object</option>
<option value="BO_Enh">Business Object Enhancement</option>
<option value="BO_Node">Business Object Node</option>
<option value="ConnTestObj">CONNTESTOBJ</option>
<option value="ariscommonfile">Common File</option>
<option value="ChannelTemplate">Communication Channel Template</option>
<option value="ariscxndef">Connection</option>
<option value="ifmcontobj">Context Object</option>
<option value="DOCU">Documentation</option>
<option value="arisfilter">Filter</option>
<option value="arisfssheet">Font Format</option>
<option value="rfc">Function Module</option>
<option value="idoc">IDoc</option>
<option value="imsg">IDoc Message Type</option>
<option value="iseg">IDoc Segment</option>
<option value="ityp">IDoc Type</option>
<option value="ifmclsfn">IFMCLSFN</option>
<option value="BO_DERIVATOR">LABEL_BO_DERIVATOR</option>
<option value="MAP_HELPER">MAP_HELPER</option>
<option value="arismacro">Macro</option>
<option value="arismodel">Model</option>
<option value="arismodeltype">Model Type</option>
<option value="RepBAMProcess">Monitoring Process</option>
<option value="namespdecl">Namespace Definition</option>
<option value="ariscomdef">OLE Object</option>
<option value="ifmopmess">OPERMES</option>
<option value="arisobjdef">Object Definition</option>
<option value="ifmoper">Operation</option>
<option value="processcomp">Process Component</option>
<option value="process">Process Integration Scenario</option>
<option value="BO_Query">Query</option>
<option value="rfcmsg">RFC Message</option>
<option value="arisreport">Report</option>
<option value="workspace">Software Component Version</option>
<option value="BO_StatusSchema">Status Schema</option>
<option value="BO_StatusVar">Status Variable</option>
<option value="RepPATProcess">Step Group</option>
<option value="RepSUBProcess">Subprocess</option>
<option value="arissymbol">Symbol</option>
<option value="BTMTaskAgent">Task Agent</option>
<option value="BTMTaskType">Task Type</option>
<option value="aristemplate">Template</option>
<option value="MAPPING_TEST">Test Cases</option>
<option value="aristextdef">Text Object</option>
<option value="ifmuitexts">UI Text Object</option>
<option value="ifmuitexvars">UI Text Variants</option>
<option value="usageProfile">Usage Profile</option>
<option value="ariswpfile">WebPublisher File</option>
<option value="ariswpscript">WebPublisher Script</option>
</select>
             </td>
         </tr>
       </table>
	  </div>      
       </form>
      <div id="content">
	  </div>
    </div>
  </body>
</html>
