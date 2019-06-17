<%@ page import="java.io.File, java.io.FileReader, java.io.BufferedReader, java.io.FileWriter, java.util.Enumeration, java.util.ArrayList, java.util.Properties" %>
<%

	String filename = "CMLocation.dat";
	String setupfile = "channels.properties";
	String folder = "ChannelManagerAppData"; //app config file where 'location' is stored
	String separator = "\\";
	String location = "";
	String message = "";
	String js_channels = "";
	String js_values = "";
	boolean allow_overwrite = true;
	boolean sent = false;
	
	if(request.getParameter("sent")!=null) sent = true;
	
	//initial checks
	File file = new File(folder); //folder
	
	if(!file.exists()) {
		message = "3Folder '" + folder + "' does not exist.^";
		if(file.mkdir()) {
			message += "2Folder '" + folder + "' created.^";
		}
		else message += "3Folder '" + folder + "' could have not been created.^";
	}
	
	file = new File(folder + separator + filename); //location file

	try {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		location = br.readLine();
		fr.close();
		String fname = location + separator + setupfile;
		File f = new File(fname);
		if(!f.exists()) {
			if(!sent)
				throw new Exception("1File '" + fname + "' does not exist. Values can not be read. You must save settings first to create the file.");
		}
		else {
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
	}
	catch (Exception e) {
		if(!file.exists()) {
			try {
				file.createNewFile();
				location = file.getAbsolutePath();
				message += "2File 'CMLocation.dat' did not exist. It was now created.^";
			}
			catch (Exception ex) {
				message += ex.getMessage() + "^";
			}
		}
		else {
			message += e.getMessage() + "^";
		}
	}
	
	//write updates
	if(sent) {
		String location_ = request.getParameter("location");
		if(location==null) {
			allow_overwrite = false;
		}
		else
			if(!location.equals(location_)) allow_overwrite = false;
		FileWriter fw = new FileWriter(file);
		if(new File(location_).exists()) {
			try {
				fw.write(location_);
				fw.close();
				location = location_;
				String fname = location + separator + setupfile;
				if(!allow_overwrite) {
					message += "2Location has been successfully changed.^";
					Properties props = new Properties();
					if(new File(fname).exists()) {
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
				}
				
				try {
					if(allow_overwrite) {
						//saving channel properties
						Properties props = new Properties();
						Enumeration params = request.getParameterNames();
						js_channels = "";
						js_values = "";
						while(params.hasMoreElements()) {
							String param = (String)params.nextElement();
							if(param.startsWith("chn_")) {
								String value = request.getParameter(param).replaceAll(" ", "");
								props.setProperty(param, value);
								js_channels += ",'" + param + "'";
								js_values += ",'" + value + "'";
							}
						}

						fw = new FileWriter(location + separator + setupfile);
						props.store(fw, null);
						fw.close();
						message += "2Data has been successfully saved.^";
					}

					if(!new File(fname).exists()) {
						message += "1File '" + fname + "' does not exist. Values can not be read. You must save settings first to create the file.^";
					}

				}
				catch (Exception e2) {
					message += e2.getMessage() + "^";
				}
				
			}
			catch (Exception e1) {
				message += e1.getMessage() + "^";
			}
		}
		else {
			message += "3Folder '" + location_ + "' does not exist. Enter a valid folder.^";
		}
	}
	
	//read values
	if(js_channels.length()>0)
		js_channels = js_channels.substring(1);
	if(js_values.length()>0)
		js_values = js_values.substring(1);
	
	message = message.replaceAll("\\\\", "/").replaceAll("'", "-");
%>
<html>
  <head>
    <title>SAP PO ChannelManager v3.1</title>
    <link href="style.css" rel="stylesheet"/>
    <script language="javascript" src="script.js"></script>
    <script>
	var js_channels = [<%= js_channels %>];
	var js_values = [<%= js_values %>];
	function populate() {
		for(i=0; i<js_channels.length; i++) {
			if(document.x.elements[js_channels[i]]!=null) {
				document.x.elements[js_channels[i]].value = js_values[i];
			}
		}
	}
    </script>
  </head>
  <body onLoad="messageNotification('<%= message %>'); document.getElementById('content').innerHTML=displayChannelsSetup('*','*','*', 'status', ''); populate();">
   <div id="label">
   		<h2>SAP PO Channel Manager v3.1</h2>
   		<img src="images/mt.jpg" style="display: inline-block; vertical-align: top; float: right;" />
   </div>
   <div class="message0" id="messager">&nbsp;</div>
   <div style="padding: 8px;">
    <div>
      <a class="main" href="index.jsp">Channel Management</a>
      <a class="main" href="chmassa.jsp" style="text-decoration: underline">Channel ID Setup</a>
    </div>
    <div style="height: 15px"></div>
    <div class="lined">
     <div  id="saver">
      <input class="exec" type="button" value=" Save Settings " onClick="document.x.submit();"/>
     </div>
    </div>
       <form name="x" action="chmassa.jsp" method="post">
      <div id="filter">
       <table style="padding-top: 5px;">
         <tr>
         <td class="s">Party</td><td class="s">&nbsp;</td><td class="s"><input type="text" name="party" value="*"/></td>
         <td class="s">Service</td><td class="s">&nbsp;</td><td class="s"><input type="text" name="service" value="*"/></td>
         <td class="s">Channel</td><td class="s">&nbsp;</td><td class="s"><input type="text" name="channel" value="*"/></td>
         <td class="s"><input type="button" value=" Display " onClick="document.getElementById('saver').style.visibility='hidden';document.getElementById('content').innerHTML=displayChannelsSetup(document.x.party.value,document.x.service.value,document.x.channel.value, 'status', ''); populate();"/></td>
         <td class="s"><input type="button" value=" Display All " onClick="document.getElementById('saver').style.visibility='visible';document.x.party.value='*';document.x.service.value='*';document.x.channel.value='*';document.getElementById('content').innerHTML=displayChannelsSetup('*','*','*', 'status', ''); populate();"/></td>
         </tr>
       </table>
      </div>
      <div class="lined">
	      <span>Configuration Files Location</span>
    	  <input type="text" name="location" value="<%= location %>" size="120" />
    	  <input type="hidden" name="sent" value="true" />
      </div>
    <div style="padding: 20px;">
      <div id="content" style="width: 1180px;">
      </div>
    </div>
    </form>
   </div>
  </body>
</html>
