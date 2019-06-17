<%@ page import="java.io.File, java.io.FileReader, java.io.BufferedReader, java.io.FileWriter, java.util.Enumeration, java.util.ArrayList, java.util.Properties" %>
<%
	String version = request.getParameter("version");
	String filename = "CMLocation.dat";
	String setupfile = "channels.properties";
	String listfile = "listSelection.dat";
	String folder = "ChannelManagerAppData"; //app config file where 'location' is stored
	String separator = "\\";
	String location = "";
	String message = "";
	String js_channels = "";
	String js_values = "";
	String list = "";

	String fname = folder + separator + filename;
	if(new File(fname).exists()) {
		FileReader fr = new FileReader(fname);
		BufferedReader br = new BufferedReader(fr);
		location = br.readLine();
		br.close();
		fr.close();
		if(location!=null) {

			fname = location + separator + listfile;
			if(new File(fname).exists()) {
				try {
					fr = new FileReader(fname);
					br = new BufferedReader(fr);
					list = br.readLine();
					if(list==null) list = "";
					br.close();
					fr.close();
				}
				catch(Exception e) {
					message += "1Error during list read: " + e.getMessage() + "^";
				}
			}

			fname = location + separator + setupfile;
			if(new File(fname).exists()) {
				try {
					Properties props = new Properties();
					FileReader fr2 = new FileReader(fname);
					props.load(fr2);
					fr2.close();
					Enumeration pr = props.propertyNames();
					while(pr.hasMoreElements()) {
						String key = (String)pr.nextElement();
						if(key.startsWith("chn_")) {
							js_channels += ",'" + key + "'";
							js_values += ",'" + props.getProperty(key) + "'";
						}
					}
				}
				catch(Exception e) {
					message += e.getMessage() + "^";
				}
			}
		}
	}

	//read values
	if(js_channels.length()>0)
		js_channels = js_channels.substring(1);
	if(js_values.length()>0)
		js_values = js_values.substring(1);
	
	message = message.replaceAll("\\\\", "/").replaceAll("'", "-");
	
	if(version==null) 
		version = "";
	else
		version = "?version=" + version;
	
%>
<html>
  <head>
    <title>SAP PO ChannelManager v3.1</title>
    <link href="style.css" rel="stylesheet"/>
    <script language="javascript" src="script.js"></script>
    <script>
	var js_channels = [<%= js_channels %>];
	var js_values = [<%= js_values %>];
	var checker = false;
	function selectList(psource) {
		if(psource=="") {
		    messageNotification("1Selection list does not contain any value.^");
		    return;
		}
		var counter = 0;
		selectionClear();
		for(i=0; i<js_values.length; i++) { //for each CC
			checker = false;
			var line = js_values[i];
			var list = line.split(",");
			var source_list = psource.split(",");
			for(g=0; g<source_list.length; g++) { //for each list selection value
				source_list[g] = source_list[g].trim();
				if(source_list[g]=="") continue;
				for(j=0; j<list.length; j++) {  //for each CC separate single value
					if(source_list[g]==list[j]) {
						var cc = js_channels[i].replace("chn_", "ch_").replace("->", "").replace("->", "").replace("->", "");
						var o = document.ch.elements[cc];
						if(o!=null) {
							o.checked = true;
							checker = true;
							counter++;
						}
					}
					if(checker) break;
				}
				if(checker) break;
			}
		}
	    messageNotification("1Number of channels selected: " + counter + "^");
	}
	</script>
  </head>
  <body onLoad="messageNotification('<%= message %>'); document.getElementById('content').innerHTML=displayChannels_('*','*','*', 'status', '');">
   <div id="label">
   		<h2>SAP PO Channel Manager v3.1</h2>
   		<img src="images/mt.jpg" style="display: inline-block; vertical-align: top; float: right;" />
   </div>
   <div class="message0" id="messager">&nbsp;</div>
   <div style="padding: 8px;">
    <div>
      <a class="main" href="index.jsp" style="text-decoration: underline">Channel Management</a>
      <a class="main" href="chmassa.jsp">Channel ID Setup</a>
    </div>
    <div style="height: 15px"></div>
    <div class="lined">
      <input class="exec" type="button" value=" Start Selected Channels " onClick="displayProgressBox(0); modifySelectedChannels( 'start', document.x.party.value,document.x.service.value,document.x.channel.value);"/>
      <input class="exec" type="button" value=" Stop Selected Channels " onClick="displayProgressBox(0); modifySelectedChannels( 'stop', document.x.party.value,document.x.service.value,document.x.channel.value);"/>
      <div style="display: none;">
      	<div id="path"><%= location %></div>
      </div>
      <div style="display: inline-block; float: right;">
	      <input type="button" value=" Save Selection " onClick="javascript:saveSelection();"/>
	      <input type="button" value=" Restore Selection " onClick="javascript:getSelections();"/>
	      <input type="button" value=" Create Snapshot " onClick="javascript:saveSnapshot();"/>
	      <input type="button" value=" Compare2Snapshot " onClick="javascript:getSnapshots();"/>
      </div>
    </div>
       <form name="x">
      <div id="filter">
       <table style="padding-top: 5px;">
         <tr>
         <td class="s">Party</td><td class="s">&nbsp;</td><td class="s"><input type="text" name="party" value="*"/></td>
         <td class="s">Service</td><td class="s">&nbsp;</td><td class="s"><input type="text" name="service" value="*"/></td>
         <td class="s">Channel</td><td class="s">&nbsp;</td><td class="s"><input type="text" name="channel" value="*"/></td>
         <td class="s"><input type="button" value=" Display " onClick="cancelMessager(); document.getElementById('content').innerHTML=displayChannels_(document.x.party.value,document.x.service.value,document.x.channel.value, 'status', '');"/></td>
         <td class="s"><input type="button" value=" Display All " onClick="cancelMessager(); document.x.party.value='*';document.x.service.value='*';document.x.channel.value='*';document.getElementById('content').innerHTML=displayChannels_('*','*','*', 'status', '');"/></td>
         </tr>
       </table>
      </div>
    <div class="lined">
         <input type="button" value=" Select Started " onClick="javascript:selectionStarted();"/>
         <input type="button" value=" Select All " onClick="javascript:selectionAll();"/>
         <input type="button" value=" Invert Selection " onClick="javascript:selectionInvert();"/>
         <input type="button" value=" Clear Selection " onClick="javascript:selectionClear();"/>
	     <input type="button" value=" Display Selection List " onClick="javascript:displaySelectionList();"/>
    </div>
    <div class="linedb">
         <input type="button" value=" List Selection " onClick="javascript:selectList(document.x.iflist.value);"/>
         <input type="text" name="iflist" value="<%= list %>" size="120" />
         <input type="button" value=" Save List " onClick="javascript:saveList(document.x.iflist.value);"/>
    </div>
       </form>
    <div style="padding-left: 20px;">
        <div id="ccstatus" style="display: inline-block; float: right; padding-right: 20px;">
        </div>
      <div id="content">
      </div>
<form name="lform">
        <div id="logBox">
          <div style="background-color: #336699; padding: 5px; height: 17px;">
            <span id="lbLabel"></span>
            <a href="javascript:" onClick="document.getElementById('logBox').style.display='none';" style="color: white; decoration: none; font-family: Arial; font-size: 13px; float: right; padding-right: 10px;">Close</a>
          </div>
          <div style="padding: 5px; overflow-y: scroll;">
              <div id="logBoxContent" style="font-family: Arial; font-size: 12px; height: 350px;">Processing...</div>
          </div>
        </div>
</form>
    </div>
   </div>
  </body>
</html>
