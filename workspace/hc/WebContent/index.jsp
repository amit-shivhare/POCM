<%
String server = request.getServerName();
String port = Integer.toString(request.getServerPort());
String url = "";
String protocol = "http://";

if(request.isSecure()) {
	protocol = "https://";
}

url = protocol + server + ":" + port;

%>
<html>
  <head>
    <title>SAP PI ChannelManager v1.0</title>
    <link href="style.css" rel="stylesheet"/>
    <script language="JavaScript">
    	var server = "<%= url %>";
    	var type_http = "/HttpAdapter/HttpMessageServlet";
    	var type_soap = "/XISOAPAdapter/MessageServlet";
    	
    	function composeURL(){
        	var adder = "&receiverParty=&receiverService=";
			var endpoint = server;
			var type = type_http;
			if(document.x.elements["type"].selectedIndex>0) {
				type = type_soap;
				add = adder;
			}
			else add = "";
			endpoint = server + type + "?";
			endpoint += "senderParty=" + document.x.elements["party"].value;
			endpoint += "&senderService=" + document.x.elements["sender"].value;
			endpoint += "&interface=" + document.x.elements["interface"].value;
			endpoint += "&interfaceNamespace=" + document.x.elements["namespace"].value;
			endpoint += "&qos=" + document.x.elements["qos"][document.x.elements["qos"].selectedIndex].value;
			document.x.url.value = endpoint + add;
			return true;
        }

        function execPOST(){
        	  if (window.XMLHttpRequest) {
        	      xhttp = new XMLHttpRequest();
        	  }
        	  else xhttp = new ActiveXObject("Microsoft.XMLHTTP");
        	  xhttp.open("POST", document.x.url.value, false);
        	  xhttp.setRequestHeader("SOAPAction", "http://sap.com/xi/WebService/soap1.1");
        	  xhttp.send(document.x.request.value);
        	  document.x.response.value = xhttp.responseText;
        }
    </script>
  </head>
  <body onLoad="composeURL();">
   <div id="label"><h2>HTTP Client v1.0</h2></div>
   <div class="message0" id="messager">&nbsp;</div>
   <div style="padding: 8px;">
   	<div style="height: 5px"></div>
       <form name="x">
    <div class="lined">
         <span>Endpoint URL</span>
         <input type="text" name="url" value="" size="190" />
    </div>
    <div style="padding: 5px;">
         <span>ICO</span>
         <select name="ico">
         	<option value="empty"></option>
         </select>
    </div>
    <div class="lined">
         <span>Type</span>
         <select name="type" onChange="javascript:composeURL();">
         	<option value="HTTP">HTTP</option>
         	<option value="SOAP">SOAP</option>
         </select>
         <span>Party</span>
         <input type="text" name="party" size="15" onChange="javascript:composeURL();"/>
         <span>Sender</span>
         <input type="text" name="sender" size="15" onChange="javascript:composeURL();" />
         <span>Interface</span>
         <input type="text" name="interface" size="40" onChange="javascript:composeURL();" />
         <span>Namespace</span>
         <input type="text" name="namespace" size="40" onChange="javascript:composeURL();" />
         <span>QoS</span>
         <select name="qos" onChange="javascript:composeURL();">
         	<option value="BE">BE</option>
         	<option value="EO">EO</option>
         	<option value="EOIO">EOIO</option>
         </select>
         <input type="button" name="rst" value=" Reset " onClick="document.forms[0].reset(); composeURL();" />
    </div>
    <div style="padding: 20px;">
      	<div><span>Request</span></div>
      	<div>
      		<div style="display: inline;">
      			<textarea name="request" cols="80" rows="10"></textarea>
      		</div>
      		<div style="display: inline; vertical-align: top;">
	      		<input type="button" value=" Send Request " onClick="javascript:execPOST();"/>
      		</div>
      	<div style="margin-top: 5px;"><span>Response</span></div>
      	<div><textarea name="response" cols="80" rows="10"></textarea></div>
    </div>
       </form>
   </div>
  </body>
</html>
