<%@ page import="java.io.FileNotFoundException, java.io.File, java.io.FileReader, java.io.BufferedReader" %><%
	String filename = request.getParameter("filename");
	String path = request.getParameter("path");
	if(!path.endsWith("\\")) path += "\\";
	String filename_ = filename + ".snapshot";
	String content = "";

	try {
		if(!new File(path + filename_).exists()) throw new FileNotFoundException("");
		FileReader fr = new FileReader(path + filename_);
		BufferedReader br = new BufferedReader(fr);
		content = br.readLine();
		fr.close();
		br.close();
		if(content==null) content="";
		out.write(content);
	}
	catch(FileNotFoundException e) {
		out.write("3Snapshot '" + filename + "' was not found!^");
	}
	catch(Exception ex) {
		out.write("3Error: " + ex.getMessage() + "^");
	}
%>