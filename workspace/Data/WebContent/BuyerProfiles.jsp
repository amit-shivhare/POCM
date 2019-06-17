<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mt.data.DataStore" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileNotFoundException" %>
<%@ page import="java.util.ArrayList" %>
<%

String fileName = "BuyerProfile";
	String message = "";
	int msgtype = 0;
	int retcode = 0;
	ArrayList<String> items = null;

	String action_ = request.getParameter("action_");
	String bpid = request.getParameter("bpid");
	String bpname = request.getParameter("bpname");
	DataStore ds = new DataStore(fileName);

	if(action_!=null && bpid!=null) {

		if(action_.equals("create")) {

			try{
				ds.updateValueForKey(bpid, bpname);
			}
			catch(Exception e) {
				retcode = -3;
			}
		
			if(retcode==0) {
				msgtype=1;
				message = "Buyer Profile \"" + bpid + "\" was successfully added.";
			}
			else 
			if(retcode==-3) {
				msgtype=-1;
				message = "Error occured during Buyer Profile processing!";
			}

		}
		else 
		if(action_.equals("delete")) {
			
			try {
				retcode = ds.deleteKey(bpid);
				DataStore props = new DataStore("Properties");
				items = props.getRows();
				int listSize = items.size();
				for(int i=0; i<listSize; i++) {
					String item = items.get(i);
					DataStore data = new DataStore(item);
					data.deleteKey(bpid);
				}
			}
			catch(Exception e) {
				retcode = -4;
			}

			if(retcode==0) {
				msgtype=1;
				message = "Buyer Profile \"" + bpid + "\" was successfully deleted.";
			}

		}
	
	}
	
	try {
		items = ds.getRows();
	}
	catch(FileNotFoundException e) {
		retcode = -10;
	}
		
	if(retcode==-1) {
		msgtype=-1;
		message = "Problem with reading DataContent.";
	}
	else
	if(retcode==-2) {
		msgtype=-1;
		message = "Problem with writing DataContent.";
	}
	else
	if(retcode==-10) {
		msgtype=0;
		message = "Buyer Profile file does not exist and will be created.";
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
		<title>BP Manager: Buyer Profiles</title>
		<style>
			a {color: darkblue; font-family: Arial; font-size: 12px}
		</style>
		<script language="JavaScript">
			function chDel(cb, id) {
				button = document.getElementById(id);
				if(cb.checked)
					button.disabled = false;
				else
					button.disabled = true;
			}

			function delProperty(id) {
				if(confirm('Are you really sure you want to delete this Buyer Profile?')) {
					document.f.action_.value = 'delete';
					document.f.bpid.value = id;
					document.f.submit();
				}
			}
		</script>
	</head>
	<body>
		<table height="100%" width="100%" cellpadding="2" cellspacing="2">
			<tr>
				<td style="height: 10px;">
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td style="vertical-align: top; height: 10px; font-family: Arial; font-weight: bold; font-size: 22px;">BP Manager: Buyer Profiles</td>
							<td style="height: 10px; text-align: right"><img src="http://media.mt.com/etc/designs/mt/docroot/images/logo/logo.jpg"/></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="height: 18px; border: black solid 1px; background-color: grey; color: white; font-weight: bold; font-family: Arial; font-size: 12px;">&nbsp;Used File: <%= ds.getFilePath() + fileName + ".dta"%></td>
			</tr>
			<tr>
				<td style="height: 10px; text-align: right;">
					<a style="color: green; font-family: Arial; font-size: 12px; font-weight: bold" href="/Data/BPProperties.jsp">BP Properties</a>&nbsp;
				</td>
			</tr>
			<tr>
				<td style="vertical-align: top; height: 90px;">
					<form name="f" method="POST" action="/Data/BuyerProfiles.jsp">
					
					<div style="width: 440px; height: 300px; overflow-y: scroll;">
						<table>

<%
	if(retcode>-5) {


		String item = null;
		int listSize = items.size();
		for(int i=0; i<listSize; i++) {
			item = items.get(i);
			int sindex = item.indexOf("->");
			String id = item.substring(0, sindex);
			item = item.substring(sindex+2);
		
%>


		<tr>
			<td style="width: 300px;"><a href="/Data/BuyerProfile.jsp?ID=<%= id %>"><%= id %> - <%= item %></a></td>			<td>&nbsp;&nbsp;</td>
			<td><input type="checkbox" ID="cb<%= Integer.toString(i) %>" onClick="chDel(this, 'db<%= Integer.toString(i) %>')"/></td>
			<td><input type="button" ID="db<%= Integer.toString(i) %>" value=" Delete  "/ disabled onClick="delProperty('<%= id %>')"></td>
		</tr>

<%
		}
	}
%>
						</table>
					</div><br/>
					
					
						<table style="border: solid 1px black;">
		<tr>
			<td colspan="10">
				<table>
					<tr>
						<td style="font-family: Arial; font-size:12px;">Buyer&nbsp;Profile&nbsp;ID</td>
						<td><input type="text" size="3" maxlength="4" name="bpid"/></td><td style="font-family: Arial; font-size:12px;">&nbsp;Buyer&nbsp;Profile&nbsp;</td><td><input type="text" size="40" maxlength="50" name="bpname"/></td>
						<td><input type="submit" value=" Add/Update "/></td>
					</tr>
				</table>
			</td>
		</tr>


						</table>

						<input type="hidden" name="action_" value="create"/>
						
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