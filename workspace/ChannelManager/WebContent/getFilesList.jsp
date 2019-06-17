<%@ page import="java.io.File, java.io.FileReader, java.io.BufferedReader, java.io.FileWriter, java.util.Enumeration, java.util.ArrayList, java.util.Properties" %><%

	String filetype = request.getParameter("filetype");
	String folder = request.getParameter("folder");
	String res = "";


	try {
		File folder_ = new File(folder);
		File[] listOfFiles = folder_.listFiles();

		for (File file : listOfFiles) {
	    	if (file.isFile()) {
	    		String name = file.getName();
	    		int i = name.lastIndexOf(".");
	    		if(i>-1)
	    		  if(name.substring(i+1).equals(filetype))
	            	res += name.substring(0,i) + "-NIT-";
	    	}
	    }
	}
	catch(Exception e) {
		res = "3Error: " + e.getMessage() + "^";
	}
	
	out.write(res);
%>