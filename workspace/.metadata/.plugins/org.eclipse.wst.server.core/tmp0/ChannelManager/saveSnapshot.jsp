<%@ page import="java.io.FileWriter, java.io.File;" %><%
  String filename = request.getParameter("filename");
  String path = request.getParameter("path");
  String content = request.getParameter("content");
  if(!path.endsWith("\\")) path += "\\";
  FileWriter fw = new FileWriter(new File(path + filename + ".snapshot"));
  try {
	  fw.write(content);
	  fw.close();
	  out.write("2Snapshot '" + filename + "' has been successfully saved.^");
  }
  catch(Exception e) {
	  out.write("3Error: " + e.getMessage() + "^");
  }
%>
