<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mt.data.DataStore" %>
<%

	boolean keyFound = false;
	int pageStatus = 0;
	String key = null;
	String value = null;
	String message = "";
	int msgtype = 0;
	String subject = request.getParameter("subject");
	DataStore ds = null;

	if(subject==null) {
		pageStatus = -1;
	}
	else {
		String action_ = request.getParameter("action_");
		if(action_==null) 
			pageStatus = 0;
		else 
		if(action_.equals("search")) {
			key = request.getParameter("key");
			pageStatus = 1;
			ds = new DataStore(subject);
			value = ds.getValueForKey(key);
			if(value==null) {
				keyFound=false;
				value="";
			}
			else
				if(value.equals(">nO_FilE<")) {
					message = "Data file \"" + subject + ".dta\"" + "does not exist and will be created.";
					value = "";
				}
				else {
					keyFound = true;
				}
		}
		else 
		if(action_.equals("update")) {
			key = request.getParameter("key");
			value = request.getParameter("value");
			pageStatus = 2;
			ds = new DataStore(subject);
			int retcode = ds.updateValueForKey(key,value);
			if(retcode==0) {
				msgtype=1;
				message = "Key \"" + key + "\" was successfully updated with value \"" + value + "\".";
			}
			else
			if(retcode==1) {
				msgtype=1;
				message = "Key pair \"" + key + "/" + value + "\" was successfully created.";
			}
			else
			if(retcode==-1) {
				msgtype=-1;
				message = "Problem with reading DataContent.";
			}
			else
			if(retcode==-2) {
				msgtype=-1;
				message = "Problem with writing DataContent.";
			}
			else message = "Retcode: " + Integer.toString(retcode);
		}
		else 
		if(action_.equals("delete")) {
			key = request.getParameter("key");
			pageStatus = 3;
			ds = new DataStore(subject);
			int retcode = ds.deleteKey(key);
			if(retcode==0) {
				msgtype=1;
				message = "Key \"" + key + "\" was not in the file, so no need to delete.";
			}
			else
			if(retcode==1) {
				msgtype=1;
				message = "Key \"" + key + "\" was successfully deleted.";
			}
			else
			if(retcode==-1) {
				msgtype=-1;
				message = "Problem with reading DataContent.";
			}
			else
			if(retcode==-2) {
				msgtype=-1;
				message = "Problem with writing DataContent.";
			}
			else message = "Retcode: " + Integer.toString(retcode);
			
		}
		else pageStatus = -2;
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
		<title>BP Manager: Config</title>
	</head>
	<body onLoad="if(document.f.value!=null) document.f.value.focus(); else document.f.key.focus();">
		<table height="100%" width="100%" cellpadding="2" cellspacing="2">
			<tr>
				<td style="height: 10px;">
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td style="vertical-align: top; height: 10px; font-family: Arial; font-weight: bold; font-size: 22px;">BP Manager: <%= subject %></td>
							<td style="height: 10px; text-align: right"><img src="http://media.mt.com/etc/designs/mt/docroot/images/logo/logo.jpg"/></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>

				<td style="height: 18px; border: black solid 1px; background-color: grey; color: white; font-weight: bold; font-family: Arial; font-size: 12px;">&nbsp;Used File: <%= ds.getFilePath() + subject + ".dta" %></td>
			</tr>
			<tr>
				<td style="height: 10px; text-align: right;">
					<a style="color: green; font-family: Arial; font-size: 12px; font-weight: bold" href="/Data/BuyerProfiles.jsp">BP Manager</a>&nbsp;
				</td>
			</tr>
			<tr>
				<td style="height: 2px;">&nbsp;</td>
			</tr>
			<tr>
				<td style="vertical-align: top; height: 90px;"><table><tr><td>
					<form name="f" method="POST" action="/Data/Maintain.jsp?subject=<%= subject %>">
						<table style="border: solid 1px black;">
<%
	if(pageStatus<0) {
		message = "Processing Error: " + pageStatus;
		msgtype = -1;
	}
	else 
	if(pageStatus==0 || pageStatus==2 || pageStatus==3) {
%>
		<tr>
			<td style="font-family: Arial; font-size:12px;">Buyer&nbsp;Profile&nbsp;ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td><input type="text" size="3" maxlength="4" name="key"/>&nbsp;<input type="submit" value=" Search "/></td>
		</tr>
		<input type="hidden" name="action_" value="search"/>
<%
	}
	else 
	{
%>
		<tr>
			<td style="font-family: Arial; font-size:12px;">Buyer&nbsp;Profile&nbsp;ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td><input type="text" size="3" maxlength="4" name="key" value="<%= key %>"/>&nbsp;<input type="submit" value=" Search "/></td>
		</tr>
		<tr>
			<td style="font-family: Arial; font-size:12px;">Property&nbsp;Value</td>
			<td><input type="text" size="80" maxlength="100" name="value" value="<%= value %>"/></td>
<%
			if(keyFound) {
%>
			<td><input type="button" value="  Update  " onClick="document.f.action_.value='update'; document.f.submit();"/></td>
			<td><input type="button" value="  Delete  " onClick="document.f.action_.value='delete'; document.f.submit();"/></td>
<%				
			}
			else {
%>
			<td>
				<input type="button" value="  Insert  " onClick="document.f.action_.value='update'; document.f.submit();"/><br/>
			</td>
<%				
			}
%>
		</tr>
		<input type="hidden" name="action_" value="search"/>
		<input type="hidden" name="subject" value="<%= subject %>"/>
		
<%
	}
%>
						</table>
					</form></td></tr></table>
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