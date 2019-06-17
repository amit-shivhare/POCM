<%@ page import="java.io.File, java.io.FileReader, java.io.BufferedReader, java.io.FileWriter, java.util.Enumeration, java.util.ArrayList, java.util.Properties" %><%
	String filename = "CMLocation.dat";
	String listfile = "listSelection.dat";
	String folder = "ChannelManagerAppData"; //app config file where 'location' is stored
	String separator = "\\";
	String location = "";
	String message = "";
	String list = request.getParameter("list");

	try {
		String fname = folder + separator + filename;
		if(new File(fname).exists()) {
			FileReader fr = new FileReader(fname);
			BufferedReader br = new BufferedReader(fr);
			location = br.readLine();
			fr.close();
			if(location!=null) {
				fname = location + separator + listfile;
				FileWriter fw = new FileWriter(fname);
				fw.write(list);
				fw.close();
			}
			message += "2List has been successfully saved.^";
		}
	}
	catch(Exception e) {
		message += "3Error during saving the list: " + e.getMessage() + "^";
	}
	
	message = message.replaceAll("\\\\", "/").replaceAll("'", "-");

%><%= message %>