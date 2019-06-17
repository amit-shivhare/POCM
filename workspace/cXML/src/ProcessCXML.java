import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import com.mt.cxml.PIResponse;
import com.mt.cxml.BuyerProfiles;
import com.mt.cxml.ECORequestXML;
import com.mt.cxml.BuyerProfileData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.net.InetAddress;
import java.nio.charset.Charset;


/**
 * Servlet implementation class ProcessCXML
 */
public class ProcessCXML extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String dataLocationDir = "\\Interfaces\\cXML\\";
	private static final String logRejectedLocationDir = "\\Interfaces\\cXML\\RejectedLog\\";
	private static final String logInvoiceLocationDir = "\\Interfaces\\cXML\\InvoiceLog\\";
	private static final String logRequestLocationDir = "\\Interfaces\\cXML\\RequestResponseLog\\";
	private static final String stylesheetLocationDir = "\\Interfaces\\cXML\\CustomResponseStylesheets\\";

	private static String Credentials = "";
	private static String PIHost = "";
	private static String PIPort = "";
	private static String dataLocation = "";
	private static String logInvoiceLocation = "";
	private static String logRejectedLocation = "";
	private static String logRequest = "";
	private static String logRejected = "";
	private static String logFilesRetentionDays = "7";
	private static String logRequestLocation = "";
	private static String passIfSenderNotIdentified = "";
	private static String stylesheetsLocation = "";
	private static String logInvoicePayload = "";
	private static String initiationStateID = "INIT";
	
	//Buyer Profiles in the memory as shared data
	private static BuyerProfiles BPs = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */

	public ProcessCXML() {
        super();
        // TODO Auto-generated constructor stub
        cacheRefresh("POST");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String qs = request.getQueryString();
		String cr = null;
		if(qs!=null)
			cr = getParamValue("cacheRefresh", qs);
		
		PrintWriter respPW = response.getWriter();
		if(cr==null) {
			respPW.write(getIntroPage());
		}
		else
		if(cr.equals("true")) {
			respPW.write(cacheRefresh("GET"));
		}
		else {
			respPW.write(getIntroPage());
		}
		respPW.flush();
	  	respPW.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		//checkInitiationState - if there is a need to reload Buyer Profile data for instance
		if(!initiationStateID.equals(getInitiationStateID())) {
			cacheRefresh("POST");
		}
		
		String url = "";
		String returnString = "";
		String returnCode = "200";
		String qs = request.getQueryString();
		String action = getParamValue("action", qs);
		String senderSystem = null;
		String senderInterface = null;
		String customResponse = "";
		String responseXSLT = null;
		String custCXML = "";
		String sysCXML = "";
		String targetID = "";
		ECORequestXML ecoreq = null;
		Properties props = new Properties();

		if(action!=null) {
			
			boolean sendToPI = true;
			
		    //getting the request body content
			custCXML = getPostData(request);
			
			//checking incoming request
			try {

				//get the appropriate URL
				//process action according to action parameter
				if(request.getParameter("senderSystem")!=null) senderSystem=request.getParameter("senderSystem");
				if(request.getParameter("interface")!=null) senderInterface=request.getParameter("interface");

				if(action.equals("Punchout")) {
					if(senderSystem==null) senderSystem="cXML"; 
					if(senderInterface==null) senderInterface="Punch_Req_Axis_In"; 
					url = "http://" + PIHost + ".eu.mt.mtnet:" + PIPort + "/XIAxisAdapter/MessageServlet?senderParty=Marketplace&senderService=" + senderSystem + "&receiverParty=&receiverService=&interface=" + senderInterface + "&interfaceNamespace=urn:mt.com:A:ARIBA:EPROCUREMENT&rest&removeDTDs=true";
				}
				else
				if(action.equals("Order")) {
					if(senderSystem==null) senderSystem="cXML"; 
					if(senderInterface==null) senderInterface="OrderRequest_Out_A"; 
					url = "http://" + PIHost + ".eu.mt.mtnet:" + PIPort + "/XIAxisAdapter/MessageServlet?senderParty=Marketplace&senderService=" + senderSystem + "&receiverParty=&receiverService=&interface=" + senderInterface + "&interfaceNamespace=urn:mt.com:A:ARIBA:EPROCUREMENT&removeDTDs=true";
					if(!senderSystem.equals("cXML")) url += "&rest";
//					url = "http://" + PIHost + ".eu.mt.mtnet:" + PIPort + "/HttpAdapter/HttpMessageServlet?senderParty=Marketplace&senderService=" + senderSystem + "&receiverParty=&receiverService=&interface=" + senderInterface + "&interfaceNamespace=urn:mt.com:A:ARIBA:EPROCUREMENT&qos=EO";
				}
				else
					if(action.equals("OCIPunchout")) {
						if(senderInterface==null) senderInterface="PunchoutOCI_Out_Sync"; 
						if(senderSystem==null) senderSystem="OCI";
						String hook_url = getParamValue("HOOK_URL", qs);
						if(hook_url==null) {
							qs = custCXML;
							hook_url = getParamValue("HOOK_URL", qs);
						}
						url = "http://" + PIHost + ".eu.mt.mtnet:" + PIPort + "/HttpAdapter/HttpMessageServlet?interfaceNamespace=urn:mt.com:A:HYBRIS:eCommerce&interface=" + senderInterface + "&senderService=" + senderSystem + "&senderParty=Marketplace&agency=&scheme=&qos=BE&sap-client=100&sap-language=EN";
						if(hook_url!=null) url += "&HOOK_URL=" + hook_url;
						
						custCXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<OCIPunchoutHookURL>" + hook_url + "</OCIPunchoutHookURL>"; 
				}
				else
				if(action.equals("Invoice")) {
						targetID = getParamValue("target", qs);
						if(targetID!=null) {
						    props.load(new FileInputStream(dataLocation + "invoiceTarget.properties"));
						    url = props.getProperty(targetID);
						    if(url==null) url="";
						}
						
				}
				//get the appropriate URL END

				//check the request (maybe throws exception)
				ecoreq = new ECORequestXML(fixRequestContent(custCXML));
				
				//credit card check
				//if any additional request checks are necessary, this is the place to add them
				ecoreq.creditCardCheck();

			}
			catch(Exception e) {
				returnString = e.getMessage();
				returnCode = "480";
			}
			
			if(returnCode.equals("200")) {
				//if no appropriate action found
				if(url.equals("")) {
					if(returnCode.equals("200")) {
						returnString = "The query string parameter list contains wrong values. Set the parameter action=[Punchout/OCIPunchout/Order/Invoice] and in case of Invoice check existence of targetID in the properties file.";
						returnCode = "450";
					}
				}
				else {
					
					//****************  START OF PI BUYER PROFILE

					try {
						BuyerProfileData bpd = BPs.getMatchingBuyerProfile(ecoreq.getDocumentContent());

						if(!passIfSenderNotIdentified.equals("true")) {
							//PARTNER PROFILE NOT FOUND
							if(bpd==null) {
								sendToPI = false;
								returnCode = "410";
								returnString = "PARTNER NOT ALLOWED";
								if(logRejected.equals("true"))
									logFile(logRejectedLocation, "410_request", "xml", ecoreq.getOriginalContent(), true);
							}
							//PROFILE FOUND, SHARED SECRET CHECK
							else {
								//SS NOT OK
								if(!bpd.isSharedSecretOK(ecoreq.getDocumentContent())) {
									sendToPI = false;
									returnCode = "411";
									returnString = "INVALID SHARED SECRET";
									if(logRejected.equals("true"))
										logFile(logRejectedLocation, "411_request", "xml", ecoreq.getOriginalContent(), true);
								}
								responseXSLT = bpd.CustomResponse;
							}
						}
						else {
							if(bpd!=null)
								responseXSLT = bpd.CustomResponse;
						}
					}
					catch(Exception e) {
						sendToPI = false;
						returnCode = "500";
						returnString = "ERROR: " + e.getMessage();
						if(logRejected.equals("true"))
							logFile(logRejectedLocation, "500_request", "xml", custCXML, true);
					}
					//*****************  END OF PI BUYER PROFILE
					
					if(sendToPI) {

						if(action.equals("Order") && senderSystem.startsWith("cXML")) {
							sysCXML = 	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
							"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
							"<soapenv:Body>" +  ecoreq.getNoPrologContent() +
							"</soapenv:Body></soapenv:Envelope>";
						}
						else {
							sysCXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ecoreq.getNoPrologContent();
						}

						try {
							PIResponse pir = executePost(url, sysCXML, action);
							returnString = pir.getContent();
							returnCode = pir.getCodeString();
						}
						catch(Exception e) {
							returnCode = "500";
							returnString = "ERROR: " + e.getMessage();
						}

						//logging of invoices
						if(action.equals("Invoice")) {
							
							try {
								DateFormat dateFormat = new SimpleDateFormat("dd-MM / HH:mm:ss");
								Date date = new Date();
								String dts = dateFormat.format(date);
								String logItemUnique = dts.substring(8, 10) + dts.substring(11, 13) + dts.substring(14);
								String logID = dts.substring(0,2);

								File log = new File(logInvoiceLocation + "Invoice_" + logID + ".htm");
								File logFolder = new File(logInvoiceLocation);
								if(!log.exists()) {
									//delete logs for next 4 days
									int currentDay = Integer.parseInt(logID);
									for(int l=1; l<5; l++) {
										int logIDtoDeleteInt = currentDay + l;
										if(logIDtoDeleteInt>31) {
											logIDtoDeleteInt-=31;
										}
										String logIDtoDelete = String.valueOf(logIDtoDeleteInt);
										if(logIDtoDeleteInt<10) logIDtoDelete = "0" + logIDtoDelete;
										File logToDelete = new File(logInvoiceLocation + "Invoice_" + logIDtoDelete + ".htm");
										logToDelete.delete();
										
										//deleting the payload files
										for(File f:logFolder.listFiles())
										    if(f.getName().startsWith("invPayload_" + logIDtoDelete))
										        f.delete();
									}
									//create the new html log file for the current day
									FileWriter fw = new FileWriter(log, true);
									String htmlstart = "<html><header><style>td {font-family: Arial; font-size: 12px;}</style>";
									htmlstart += "<script language=\"javascript\"></script>";
									htmlstart += "</header><body><h2>cXML Invoices Log</h2><table><tr bgcolor=\"#CCCC99\"><td width=\"140\">Date / Time</td><td width=\"70px\">Target ID</td><td width=\"100px\">Invoice No.</td><td width=\"70px\">Status</td><td width=\"200px\">Payloads</td></tr>";
									
									fw.append(htmlstart);
									fw.close();
								}
								
								//get invoiceID
								int invIndex = sysCXML.indexOf("invoiceID=");
								String invoiceID = "NA";
								if(invIndex>-1) {
									invoiceID = sysCXML.substring(invIndex, invIndex+23);
									invoiceID = invoiceID.substring(11, invoiceID.lastIndexOf("\""));
								}

								//checking the response
							    props.load(new FileInputStream(dataLocation + "invoiceResponseCheck.properties"));
							    String comparator = props.getProperty(targetID);
							    int succStrFound = returnString.indexOf(comparator);
							    String Status = "n/a";
							    if(succStrFound>-1) Status = "Success";
							    
							    if(logInvoicePayload==null) logInvoicePayload = "";

								//logging the invoice information
								FileWriter fw = new FileWriter(log, true);
								String logLine = "<tr><td>" + dts + "</td>";
								logLine += "<td>" + targetID + "</td>";
								logLine += "<td>" + invoiceID + "</td>";
								logLine += "<td>" + Status + "<span id=\"" + invoiceID + logItemUnique + "\" style=\"visibility: 'hidden'; font-size: 0px;\">" + returnString + "</span></td><td>";
								if(logInvoicePayload.equals("true")) {
									String invpayfilename = "invPayload_" + logID + "_" + invoiceID + "_" + logItemUnique;
									logFile(logInvoiceLocation, invpayfilename, "xml",custCXML, false);
									invpayfilename = logInvoiceLocation + invpayfilename + ".xml";
									logLine += "<a href=\"" + invpayfilename + "\" target=\"_new\">Request</a>&nbsp;&nbsp;";
								}
								logLine += "<a href=\"javascript:alert(document.getElementById('" + invoiceID + logItemUnique + "').innerHTML);\">Response</td></tr>";
								fw.append(logLine);
	/*
								fw.append( + "|TargetID:" + targetID + "|InvoiceID:" + invoiceID + "|ReturnCode:" + returnCode + "|SentTo:" + url + "\r\n");
								fw.append("Response:" + returnString + "\r\n");
								fw.append("***************************************************************************************\r\n");
	*/								
								fw.close();
							}
							catch(Exception e) {
								returnCode = "201";
								returnString = "Invoice sent to customer but error occured during logging: " + e.getMessage();
							}
							
						}
						else {
						}
					}
				}
			}
				
		}
		else {
			returnString = "Parameter action not specified for cXML processing. Set the parameter action=[Punchout/Order]";
			returnCode = "450";
		}

		//manage custom response
		if(responseXSLT!=null && action.equals("Order")) {
			//code to return customResponse
			customResponse = "true";
			if(returnCode.equals("200"))
				responseXSLT += ".xsl";
			else
				responseXSLT += "_ERR.xsl";
			try {
		        TransformerFactory factory = TransformerFactory.newInstance();
		        File stylesheet = new File(stylesheetsLocation + responseXSLT);
		        if(stylesheet.exists()) {
			        Source xslt = new StreamSource(stylesheet);
			        Transformer transformer = factory.newTransformer(xslt);

			        StringReader sr = new StringReader(sysCXML);
			        StringWriter sw = new StringWriter();
			        transformer.transform(new javax.xml.transform.stream.StreamSource(sr), new javax.xml.transform.stream.StreamResult(sw));									
			        customResponse = sw.toString();
		        }
		        else {
		        	returnCode = "500";
		        	returnString = "Response stylesheet " + stylesheet + " does not exist.";
		        }
			}
			catch(Exception e) {
				returnCode = "500";
				returnString = "ERROR: " + e.getMessage();
			}
		}
		
		//**********************************************
		//++++++++++  adjusting the return  ++++++++++++
		//**********************************************
		if(customResponse.equals("")) {
			//it is a cXML protocol customer
			if(!returnCode.equals("200"))
				returnString = composeCXMLResponse(returnCode, returnString);
		}
		else
		if(customResponse.equals("true")) {
			//if customresponse is required but transformation failed
			returnString =  composeEcoErrorResponse(returnCode, returnString);
		}
		else {
			//customResponse required, transformation successful
			returnString = customResponse.replace("-|RET_STR|-", returnString).replace("-|RET_COD|-", returnCode);
		}

		//return response
		if(returnString.contains("text/html"))
			response.setContentType("text/html; charset=UTF-8");
		else
			response.setContentType("text/xml; charset=UTF-8");
		
		//**************************************************
		//+++++++++++++++++++   logging   ++++++++++++++++++
		//**************************************************
		if(logRequest.equals("true")) {
			try {
				logFile(logRequestLocation, "request", "xml", custCXML, true);
				logFile(logRequestLocation, "response", "xml", returnString, true);
			}
			catch(Exception e) {
				returnCode = "201";
				returnString = "Processing successful but error occured during logging: " + e.getMessage();
			}
		}

		//setting the repsonse HTTP code (v2.1)
		response.setStatus(Integer.valueOf(returnCode));
		
		PrintWriter respPW = response.getWriter();
		respPW.write(returnString);
		respPW.flush();
	  	respPW.close();

	}
	
	private String fixRequestContent(String xml) {
		//return xml.replaceAll("[^\\x00-\\x7F]", "_");
		return xml;
	}
	
	private void deleteFilesOlderThanNdays(int daysBack, String dirWay) {
		File directory = new File(dirWay);
		if(directory.exists()){
		    File[] listFiles = directory.listFiles();           
		    long purgeTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);
		    for(File listFile : listFiles) {
		        if(listFile.lastModified() < purgeTime) {
		            if(!listFile.delete()) {
		                System.err.println("Unable to delete file: " + listFile);
		            }
   	            }
		    }
		}
    }
	
	private void logFile(String path, String filename, String extension, String content, boolean timestamp) throws IOException {
		String ts = "";
		
		deleteFilesOlderThanNdays(Integer.parseInt(logFilesRetentionDays), path);
		
		if(timestamp) {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
			Date date = new Date();
			ts = "_" + dateFormat.format(date);
		}
		
		File file = new File(path + filename + ts + "." + extension);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes("UTF-8"));
		fos.close();
	}
	
	private PIResponse executePost(String targetURL, String urlParameters, String action) {
		
	    URL url;
	    HttpURLConnection connection = null;
	    
	    try {
	      //Create connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", 
	           "text/xml; charset=UTF-8");
	      
	      //added headers
	      if(action.equals("OCIPunchout"))
	    	  connection.setRequestProperty("Authorization", "Basic " + Credentials);
	      else
	      if(action.equals("Order")) 
	    	  connection.setRequestProperty("SOAPAction", "\"http://sap.com/xi/WebService/soap1.1\"");

	      connection.setRequestProperty("Content-Length", "" + 
	               Integer.toString(urlParameters.getBytes().length));
	      //connection.setRequestProperty("Content-Language", "en-US");  

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
//	      logFile(logRequestLocation, "aaarequest", "xml", urlParameters, true);
	      
	      //Get Response    
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	      }
	      rd.close();
	      
	      return new PIResponse(response.toString(), 200);

	    } 
	    catch (Exception e) {
    		String resp = "Error: " + e.getMessage(); 
    		return new PIResponse(resp, 500);
	    } 
	}
	
	private String getPostData(HttpServletRequest req) {
		
    	String result = "";
        String line = "";

    	try {
	    	InputStream is = req.getInputStream();
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		      while((line = rd.readLine()) != null) {
		        result += line + "\r\n";
		      }
		      rd.close();

	    } 
    	catch(IOException e) {
	    	try {
	    		logFile(logRequestLocation, "line", "dat", line, true);
	    	}
	    	catch(Exception ex) {}
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
	
	private String getIntroPage() {
		String page = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
					  "<html>\n" +
					  "  <head>\n" +
					  "    <title>MT e-Commerce Processing Servlet</title>\n" +
					  "    <style>body {font-family: Arial, Verdana}</style>\n" +
					  "  </head>\n" +
					  "  <body>\n" +
					  "    <h1>e-Commerce Processing Servlet v2.1</h1>\n" +
					  "    <h2>by &copy;Mettler Toledo</h2>\n" +
					  "    <br/><br/>\n" +
					  "    <h2>Connection successful!</h2>\n" +
					  "    <br/><br/>\n" +
					  "    SAP PI Host: " + PIHost + "<br/>\n" +
					  "  </body>\n" +
					  "</html>";
					  
		return page;
	}
	
	private String composeCXMLResponse(String code, String text) {
		String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					  "<!DOCTYPE cXML SYSTEM \"http://xml.cxml.org/schemas/cXML/1.2.024/cXML.dtd\">\n" +
					  "<cXML>\n" +
					  "  <Response>\n" +
					  "    <Status code=\"" + code + "\" text=\"" + text + "\"/>\n" +
					  "  </Response>\n" +
					  "</cXML>";
					  
		return resp;
	}
	
	private String composeEcoErrorResponse(String code, String text) {
		String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					  "<MettlerToledoECommerceResponse>\n" +
					  "  <Status code=\"" + code + "\" text=\"" + text + "\"/>\n" +
					  "</MettlerToledoECommerceResponse>";
					  
		return resp;
	}
	
	private String cacheRefresh(String method) {
		
		Properties props = new Properties();
		String returnLog = "";
		
		//get host name
        try {
        	InetAddress ip;
            ip = InetAddress.getLocalHost();
            PIHost = ip.getHostName();
        }
        catch (Exception e) {
        	PIHost = "UNIDENTIFIED!!!";
        }
        returnLog += "PIHost: " + PIHost + "<br/>\n";
        
		//manipulate the initiateStatus
        //setting flag file to get instances updated
        if(method.equals("GET")) {
    		String ISID = "";
    		try {
    			ISID = setInitiationStateID();
    			initiationStateID = ISID;
    		}
    		catch(IOException e) {
    			ISID = "ERROR: " + e.getMessage();
    		}
            returnLog += "ServletInitiationStateID: SET [" + ISID + "]<br/>\n";
        }
        //only updating cache of the servlet
        else {
        	try {
        		initiationStateID = getInitiationStateID();
        	}
        	catch(Exception e) {}
        }
		
        //set locations
        returnLog += "<br/><b>Locations:</b><br/>\n";
        dataLocation = "\\\\" + PIHost + dataLocationDir;
        returnLog += "DataLocation: " + dataLocation + "<br/>\n";
        stylesheetsLocation = "\\\\" + PIHost + stylesheetLocationDir;
        returnLog += "StylesheetLocation: " + stylesheetsLocation + "<br/>\n";
        logRejectedLocation = "\\\\" + PIHost + logRejectedLocationDir;
        returnLog += "LogRejectedLocation: " + logRejectedLocation + "<br/>\n";
        logRequestLocation = "\\\\" + PIHost + logRequestLocationDir;
        returnLog += "LogRequestLocation: " + logRequestLocation + "<br/>\n";
        logInvoiceLocation = "\\\\" + PIHost + logInvoiceLocationDir;
        returnLog += "LogInvoiceLocation: " + logInvoiceLocation + "<br/>\n";
        
        //read servlet properties file and set the values
		try {
			returnLog += "<br/><b>Servlet Properties:</b><br/>\n";
		    props.load(new FileInputStream(dataLocation + "servlet.properties"));
		    PIPort = props.getProperty("PIPort");
		    if(PIPort==null) PIPort = "8205";
		    returnLog += "PIPort: " + PIPort + "<br/>\n";
		    passIfSenderNotIdentified = props.getProperty("passIfSenderNotIdentified");
		    if(passIfSenderNotIdentified==null) passIfSenderNotIdentified = "true";
		    returnLog += "PassIfSenderNotIdentified: " + passIfSenderNotIdentified + "<br/>\n";
		    Credentials = props.getProperty("credentials");
		    if(Credentials==null) returnLog += "Credentials: NOT SET<br/>\n";
		    else returnLog += "Credentials: SET<br/>\n";
		    logRequest = props.getProperty("logRequest");
		    if(logRequest==null) logRequest = "false";
		    returnLog += "LogRequest: " + logRequest + "<br/>\n";
		    logRejected = props.getProperty("logRejected");
		    if(logRejected==null) logRejected = "false";
		    returnLog += "LogRejected: " + logRejected + "<br/>\n";
		    logFilesRetentionDays = props.getProperty("logFilesRetentionDays");
		    if(logFilesRetentionDays==null) logFilesRetentionDays = "7";
		    returnLog += "LogFilesRetentionDays: " + logFilesRetentionDays + "<br/>\n";
		    logInvoicePayload = props.getProperty("logInvoicePayload");
		    if(logInvoicePayload==null) logInvoicePayload = "false";
		    returnLog += "LogInvoicePayload: " + logInvoicePayload + "<br/>\n";
		}
		catch(Exception e) {
		    returnLog += "Properties Exception: " + e.getMessage() + "<br/>\n";
		}
		
        returnLog += "<br/><b>Buyer Profiles:</b><br/>\n";
		try {
			BPs = new BuyerProfiles(PIHost);
			ArrayList<BuyerProfileData> alBP = BPs.getBuyerProfiles(); 
			
			for(int i=0; i<alBP.size(); i++) {
				BuyerProfileData bp = alBP.get(i);
				String line = "**********************************<br/>\n";
				line = line + "ID: " + bp.ID + "<br/>\n";
				String item = "";
				if(!bp.Identity.equals("ERROR"))
					item = "OK";
				else
					item = "ERROR";
				line = line + "Identity: " + item + "<br/>\n";
				if(!bp.SharedSecret.equals("ERROR"))
					item = "OK";
				else
					item = "ERROR";
				line = line + "SharedSecret: " + item + "<br/>\n";
				line = line + "CustomResponse: " + bp.CustomResponse + "<br/>\n";
				line = line + "<br/>\n";
				returnLog += line;
			}

		}
		catch (Exception e) {
		    returnLog += "Buyer Profiles Exception: " + e.getMessage() + "<br/>\n";
		}
		
		String page = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
					  "<html>\n" +
					  "  <head>\n" +
					  "    <title>MT e-Commerce Processing Servlet</title>\n" +
					  "    <style>body {font-family: Arial, Verdana}</style>\n" +
					  "  </head>\n" +
					  "  <body>\n" +
					  "    <h1>e-Commerce Processing Servlet v2.0</h1>\n" +
					  "    <h2>by &copy;Mettler Toledo</h2>\n" +
					  "    <br/><br/>\n" +
					  "    <h2>Cache Refresh Finished</h2>\n" +
					  "    <br/><br/>\n" + returnLog +
					  "  </body>\n" +
					  "</html>";
					  
		return page;
	}
	
	private String setInitiationStateID() throws IOException{
		String resp = "";
		File f = new File("\\\\" + PIHost + dataLocationDir + "ServletDataInitiationStateID");
		FileWriter fw = new FileWriter(f);
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmSSS");
		Date date = new Date();
		resp = dateFormat.format(date);
		fw.write(resp);
		fw.close();
		return resp;
	}
	
	private String getInitiationStateID() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("\\\\" + PIHost + dataLocationDir + "ServletDataInitiationStateID"));
	    String resp = br.readLine();
	    br.close();
	    return resp;
	}
}
