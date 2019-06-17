package com.mt.cxml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.xml.sax.SAXException;
import com.mt.data.DataStore;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.NodeList;


public class BuyerProfile {
	
	private String BPID = null;
	private String xmlDocString = "";
	Document xmlDocDoc = null;
	private String FileServer = "";
	
	public BuyerProfile(String cXML, String server) throws ParserConfigurationException, IOException, SAXException {
		
		xmlDocString = cXML;
		DocumentBuilderFactory builderFactory =  DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(cXML.getBytes());
		xmlDocDoc = builder.parse(is);
		FileServer = server;
	}
	
	public String getBPID() {
		return BPID;
	}
	
	public String getXMLDoc() {
		return xmlDocString;
	}
	
	public int check() throws IOException, XPathExpressionException {
		
		int retCode = 0;

		BPID = getProfileID();
		
		if(BPID!=null) {
			
			if(!isSharedSecretOK()) retCode = -2;
			
		}
		else {
			retCode = -1;
		}

		return retCode;
		
	}
	
	public String getXSLTFile() {
		DataStore ds = new DataStore("CustomResponse", FileServer);
		return ds.getValueForKey(BPID);
	}
	
	public String getTranslatedCXML() {
		return xmlDocString;
	}
	
	private String getProfileID() throws IOException {
		
		String profileID = null;

		DataStore ds = new DataStore("cXMLIdentity", FileServer);
		ArrayList<String> rows = ds.getRows();
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		int listSize = rows.size();
		for(int i=0; i<listSize; i++) {
			String row = rows.get(i);
			int sep_index = row.indexOf("->");
			String xpath = row.substring(sep_index+2);
			
			try {
				String profileFound = xPath.compile("boolean(" + xpath + ")").evaluate(xmlDocDoc);
				if(profileFound.equals("true")) {
					profileID = row.substring(0, sep_index);
					break;
				}
			}
			catch(XPathExpressionException e) {}
			
		}
		
		return profileID;
		
	}

	private boolean isSharedSecretOK() throws IOException, XPathExpressionException {
		
		String cust_SS = "";
		String sys_SS = "";
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			cust_SS = xPath.compile("string(cXML/Header/Sender/Credential/SharedSecret)").evaluate(xmlDocDoc);
		}
		catch(XPathExpressionException e) {}
		
		DataStore ds = new DataStore("cXMLSharedSecret", FileServer);
		sys_SS = ds.getValueForKey(BPID);
		
		//logAccess();
		
		if(sys_SS.equals(cust_SS)) return true;
		else return false;
		
	}
	
	public void applyCopyRules() throws IOException, XPathExpressionException {
		
		DataStore ds = new DataStore("cXMLCopyRules", FileServer);
		ArrayList<String> rules = new ArrayList<String>();
		
		rules = ds.getValuesForID(BPID);
		int listSize = rules.size();
		XPath xPath =  XPathFactory.newInstance().newXPath();
		for(int i=0; i<listSize; i++) {
			String valueToReplace="";
			String row = rules.get(i);
			int sep_index = row.indexOf(":=");
			String xpath = row.substring(0, sep_index);
			try {
				valueToReplace = row.substring(sep_index+2);
			}
			catch (Exception e) {}
			
			try {
				NodeList nodesToChangeList = (NodeList) xPath.compile(xpath).evaluate(xmlDocDoc, XPathConstants.NODESET);
				int l = nodesToChangeList.getLength();
				for(int k=0; k<l; k++ ) {
					nodesToChangeList.item(k).setNodeValue(valueToReplace);
				}
			}
			catch(XPathExpressionException e) {}
		}
	}

	public void logAccess() throws IOException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd|HH:mm:ss");
		Date date = new Date();
		String dts = dateFormat.format(date);
		int index = dts.indexOf("|");
		String dt = dts.substring(0,index);
		String tm = dts.substring(index+1);
		String file = DataStore.logPath + dt+ ".clg";
		
		String line = "[" + tm + "] " + BPID + "\n";

		try {
			FileWriter fw = new FileWriter(file, true);
			fw.append(line);
			fw.close();
		}
		catch (IOException e) {}

		
	}

}
