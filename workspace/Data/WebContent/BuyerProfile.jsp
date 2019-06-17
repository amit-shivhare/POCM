<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mt.data.DataStore" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileNotFoundException" %>
<%@ page import="java.util.ArrayList" %>
<%

	String fileName = "Properties";
	String message = "";
	int msgtype = 0;
	int retcode = 0;
	ArrayList<String> items = null;

	String bpid = request.getParameter("ID");
	DataStore ds = new DataStore(fileName);
	
	try {
		items = ds.getRows();
	}
	catch(FileNotFoundException e) {
		msgtype=-1;
		message = "Properties file does not exist!.";
	}
		
	if(items==null) {
		msgtype=-1;
		message = "Problem with reading data file.";
	}

	String msgcol = "grey";
	if(msgtype<0) msgcol = "red";
	else
	if(msgtype>0) msgcol = "green";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>BP Manager: Buyer Profile</title>
		<style>
			a {color: darkblue; font-family: Arial; font-size: 12px}
		</style>
	</head>
	<body onLoad="document.f.ID.focus();">
		<table height="100%" width="100%" cellpadding="2" cellspacing="2">
			<tr>
				<td style="height: 10px;">
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td style="vertical-align: top; height: 10px; font-family: Arial; font-weight: bold; font-size: 22px;">BP Manager: Buyer Profile</td>
							<td style="height: 10px; text-align: right"><img src="http://media.mt.com/etc/designs/mt/docroot/images/logo/logo.jpg"/></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="height: 18px; border: black solid 1px; background-color: grey; color: white; font-weight: bold; font-family: Arial; font-size: 12px;">&nbsp;Used File: <%= ds.getFilePath() + fileName + ".dta" %></td>
			</tr>
			<tr>
				<td style="height: 10px; text-align: right;">
					<a style="color: green; font-family: Arial; font-size: 12px; font-weight: bold" href="/Data/BuyerProfiles.jsp">BP Manager</a>&nbsp;
				</td>
			</tr>
			<tr>
				<td style="vertical-align: top; height: 90px;">
					<form name="f" method="POST" action="/Data/BuyerProfile.jsp">
					
					<div style="width: 940px; height: 400px; overflow-y: scroll;">
						<table>

<%
	if(retcode>-5) {
%>

		<tr>
			<td style="width: 200px; font-family: Arial; font-size: 12px;">Buyer&nbsp;Profile&nbsp;ID</td>
			<td>&nbsp;&nbsp;</td>
			<td><input type="text" name="ID" size="3" maxlength="4" value="<%= bpid %>">&nbsp;<input type="submit" value=" Search "></td>
			<td></td>
		</tr>

<%
		String item = null;
		int listSize = items.size();
		for(int i=0; i<listSize; i++) {
			item = items.get(i);
			DataStore data = new DataStore(item);
			String propVal = data.getValueForKey(bpid);
			if(propVal==null || propVal.equals(">nO_FilE<")) propVal="";
%>


		<tr>
			<td style="width: 200px; font-family: Arial; font-size: 12px;"><%= item %></td>
			<td>&nbsp;&nbsp;</td>
			<td><input type="text" size="80"/ value="<%= propVal %>" disabled></td>
			<td><input type="button" value=" Maintain  " onClick="document.location='/Data/Maintain.jsp?subject=<%= item %>&action_=search&key=<%= bpid %>'"></td>
		</tr>

<%
		}
	}
%>
						</table>
					</div><br/>
					
					</form>
				</td>
			</tr>
			<tr>
				<td height="100%">&nbsp;</td>
			</tr>
			<tr>
				<td style="height: 18px; border: black solid 1px; background-color: <%= msgcol %>; color: white; font-weight: bold; font-family: Arial; font-size: 12px;">&nbsp;
					<%= message %>
				</td>
			</tr>
		</table>
	</body>
</html>