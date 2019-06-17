import java.io.*;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 /**
 * Servlet implementation class Comparator
 */
public class Comparator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String SI3_Credentials = "amFyblablaxOkltb3RoB4Mw==";          //base64 encoded (username:password)
	private static final String DI3_Credentials = "amFyblablaxOkltb3RoB4Mw==";          //base64 encoded (username:password)
	private static final String OI3_Credentials = "amFyblablaxOkltb3RoB4Mw==";          //base64 encoded (username:password)
	private static final String QI3_Credentials = "amFyblablaxOkltb3RoB4Mw==";          //base64 encoded (username:password)
	private static final String PI3_Credentials = "amFyblablaxOkltb3RoB4Mw==";          //base64 encoded (username:password)
	private static final String SI3_Server = "http://ch00vsi3s.eu.mt.mtnet:8205";
	private static final String DI3_Server = "http://ch00vdi3s.eu.mt.mtnet:8205";
	private static final String OI3_Server = "http://ch00voi3s.eu.mt.mtnet:8205";
	private static final String QI3_Server = "http://ch00vqi3s.eu.mt.mtnet:8205";
	private static final String PI3_Server = "http://ch00vpi3s.eu.mt.mtnet:8205";
	private static final String SimpleQueryPath = "/rep/support/SimpleQuery";
	
	ArrayList<String[]> SourceObjectList = new ArrayList<String[]>();
	ArrayList<String[]> TargetObjectList = new ArrayList<String[]>();
	int Differences = 0;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Comparator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Differences = 0;
		String server = "";
		String credentials = "";
		String inputStr = getInput(request); 
		String compareTo = getParamValue("compareTo", inputStr);
		if(compareTo.equals("SI3")) {
			server = SI3_Server;
			credentials = SI3_Credentials;
		}
		else if(compareTo.equals("DI3")) {
			server = DI3_Server;
			credentials = DI3_Credentials;
		}
		else if(compareTo.equals("OI3")) {
			server = OI3_Server;
			credentials = OI3_Credentials;
		}
		else if(compareTo.equals("QI3")) {
			server = QI3_Server;
			credentials = QI3_Credentials;
		}
		else if(compareTo.equals("PI3")) {
			server = PI3_Server;
			credentials = PI3_Credentials;
		}
		server += SimpleQueryPath;
		String sourceServer = "http://" + request.getServerName() + ":" + request.getServerPort() + SimpleQueryPath;
		
		PrintWriter pw = response.getWriter();
		try {
			boolean onlyDifferences = true;
			if(inputStr.indexOf("onlyDifferences")==-1) onlyDifferences = false;
			SourceObjectList = parseSimpleQueryResponse(executePost(sourceServer, inputStr, credentials));
			TargetObjectList = parseSimpleQueryResponse(executePost(server, inputStr, credentials));
			pw.write(getHTMLComparisonResult(SourceObjectList, TargetObjectList, onlyDifferences));
		}
		catch(Exception e) {
			pw.write("ComparatorException: " + e.getMessage());
		}
		pw.close();
	}
	
	//**********************************************************
	//* GET INPUT STRING FROM THE REQUEST SENT TO THIS SERVLET *
	//**********************************************************
	private String getInput(HttpServletRequest requestp) throws IOException {
		InputStream is = requestp.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		StringBuilder out = new StringBuilder();
		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];

		for (; ; ) {
		    int rsz = isr.read(buffer, 0, buffer.length);
		    if (rsz < 0)
		        break;
		    out.append(buffer, 0, rsz);
		}
		return new String(out);
	}
	
	
	//**********************************************************
	//*        HTTP CALL TO GET THE SYSTEM OBJECTS LIST        *
	//**********************************************************
	private String executePost(String targetURL, String urlParameters, String Credentials) throws Exception {
		
	    URL url;
	    HttpURLConnection connection = null;
	    
	      //Create connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", 
	           "text/xml; charset=UTF-8");
	  	  connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	      connection.setRequestProperty("Authorization", "Basic " + Credentials);
	      connection.setUseCaches (false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

		  //Send request
	      OutputStream os = connection.getOutputStream();
	      OutputStreamWriter osw = new OutputStreamWriter(os, Charset.forName("UTF-8"));
	      osw.write(urlParameters);
	      osw.flush();
	      os.close();
	      osw.close();
	      
	      //Get Response    
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	      }
	      rd.close();
	      
	      return new String(response);
	}
	
	private ArrayList<String[]> parseSimpleQueryResponse(String in) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] ob = new String[5];
		
		String hStr = in.substring(in.indexOf("table bord"));
		hStr = hStr.substring(hStr.indexOf("/th"));
		//now hStr contains only lines of returned objects which will be parsed
		
		int TRindex = hStr.indexOf("<tr");
		while(TRindex>-1) {
			hStr = hStr.substring(TRindex);
			TRindex = hStr.indexOf("</tr>");
			String ProcessingLine = hStr.substring(0,TRindex);
			ob = parseLine(ProcessingLine);
			hStr = hStr.substring(TRindex);
			TRindex = hStr.indexOf("<tr");
			result.add(ob);
		}
		return result;
	}
	
	private String[] parseLine(String in) {
		String[] ob = new String[6];
		int TDindex = in.indexOf("<td");
		int position = 0;
		String name = "";
		while(TDindex>-1) {
			in = in.substring(TDindex);
			TDindex = in.indexOf("</td>");
			String value = in.substring(4,TDindex);
			in = in.substring(TDindex);
			TDindex = in.indexOf("<td");
			if(position==0) 
				ob[0] = value;
			else if (position==1) {
				ob[1] = value;
				ob[2] = value;
			}
			else if (position==5)
				name = value;
			else if (position==6){
				ob[2] = name + "|" + value + "|" + ob[2];
				name = "";
			}
			else if (position==7)
				ob[3] = value;
			else if (position==8)
				ob[4] = value;
			else if (position==9)
				ob[5] = value;
			position++;
		}
		return ob;
	}
	
	private String getHTMLComparisonResult(ArrayList<String[]> alSource, ArrayList<String[]> alTarget, boolean onlyDiff) {
		String result = "";
		String[] ObjectStatusSource = new String[6];
		String[] ObjectStatusTarget = new String[6];
		boolean areDifferences = false;
		
		int dk = 0;
		for(int i=0; i<alSource.size(); i++) {
			dk++;
			boolean matches = false;
			boolean found = false;
			ObjectStatusSource = alSource.get(i);
			
			for(int j=0; j<alTarget.size(); j++) {
				ObjectStatusTarget = alTarget.get(j);
				if(ObjectStatusSource[2].equals(ObjectStatusTarget[2])) {
					found = true;
					if(ObjectStatusSource[3].equals(ObjectStatusTarget[3]))
						matches = true;
					else {
						areDifferences = true;
						Differences++;
					}
					alTarget.remove(j);
					break;
				}
			}

			if(!found) Differences++;
			
			if(onlyDiff) {
				if(!matches) {
					result += "<tr class=\"" + generateClassLineStyle(dk) + "\">";
					result += "<td class=\"sk2\">" + ObjectStatusSource[0] + "</td>";
					result += "<td class=\"sk2\">" + ObjectStatusSource[1] + "</td>";
					if(found) {
						result += "<td class=\"sk2r\">" + ObjectStatusSource[4] + "</td>";
						result += "<td class=\"sk2r\">" + ObjectStatusSource[5] + "</td>";
						result += "<td class=\"sk2r\">" + ObjectStatusTarget[4] + "</td>";
						result += "<td class=\"sk2r\">" + ObjectStatusTarget[5] + "</td>";
					}
					else {
						result += "<td class=\"sk2\">" + ObjectStatusSource[4] + "</td>";
						result += "<td class=\"sk2\">" + ObjectStatusSource[5] + "</td>";
						result += "<td class=\"sk2r\">N/A</td>";
						result += "<td class=\"sk2r\">N/A</td>";
					}
					result += "</tr>";
				}
			}
			else {
				result += "<tr class=\"" + generateClassLineStyle(dk) + "\">";
				result += "<td class=\"sk2\">" + ObjectStatusSource[0] + "</td>";
				result += "<td class=\"sk2\">" + ObjectStatusSource[1] + "</td>";
				if(matches) {
					result += "<td class=\"sk2\">" + ObjectStatusSource[4] + "</td>";
					result += "<td class=\"sk2\">" + ObjectStatusSource[5] + "</td>";
					result += "<td class=\"sk2\">" + ObjectStatusTarget[4] + "</td>";
					result += "<td class=\"sk2\">" + ObjectStatusTarget[5] + "</td>";
				}
				else {
					if(found) {
						result += "<td class=\"sk2r\">" + ObjectStatusSource[4] + "</td>";
						result += "<td class=\"sk2r\">" + ObjectStatusSource[5] + "</td>";
						result += "<td class=\"sk2r\">" + ObjectStatusTarget[4] + "</td>";
						result += "<td class=\"sk2r\">" + ObjectStatusTarget[5] + "</td>";
					}
					else {
						result += "<td class=\"sk2\">" + ObjectStatusSource[4] + "</td>";
						result += "<td class=\"sk2\">" + ObjectStatusSource[5] + "</td>";
						result += "<td class=\"sk2r\">N/A</td>";
						result += "<td class=\"sk2r\">N/A</td>";
					}
				}
				result += "</tr>";
			}
		}

		//remaining values from target
		for(int j=0; j<alTarget.size(); j++) {
			dk++;
			ObjectStatusTarget = alTarget.get(j);
			result += "<tr class=\"" + generateClassLineStyle(dk) + "\">";
			result += "<td class=\"sk2\">" + ObjectStatusTarget[0] + "</td>";
			result += "<td class=\"sk2\">" + ObjectStatusTarget[1] + "</td>";
			result += "<td class=\"sk2r\">N/A</td>";
			result += "<td class=\"sk2r\">N/A</td>";
			result += "<td class=\"sk2\">" + ObjectStatusTarget[4] + "</td>";
			result += "<td class=\"sk2\">" + ObjectStatusTarget[5] + "</td>";
			result += "</tr>";
			areDifferences = true;
		} 

		if(onlyDiff && !areDifferences)
			result += "<span>There are no differences in object versions in compared systems.</span>";
		else {
			result = Integer.valueOf(Differences) + "^<table style=\"border-collapse: collapse; border: solid 1px black;\">" +
					 "<tr><th>Object Name</th><th>Object SWCV</th>" +
					 "<th>Source&nbsp;Modified&nbsp;By</th><th>Source&nbsp;Modified&nbsp;On</th>" +
					 "<th>Target&nbsp;Modified&nbsp;By</th><th>Target&nbsp;Modified&nbsp;On</th></tr>"	
				+ result;
			result += "</table>";
		}
		
		return result;
	}

	private String getParamValue(String ParamName, String QueryString) {
		int sindex = QueryString.indexOf(ParamName);
		if(sindex>-1) {
			String s = QueryString.substring(sindex);
			int eindex = s.indexOf("&");
			if(eindex>-1)return(s.substring(s.indexOf("=")+1, eindex));
			else return(s.substring(s.indexOf("=")+1));
		}
		else {
			return null;
		}
	}
	
	private String generateClassLineStyle(int p) {
		int h = (p+1)%2 + 1;
		return "c"+String.valueOf(h);
	}
}
