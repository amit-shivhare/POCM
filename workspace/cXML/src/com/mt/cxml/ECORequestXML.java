package com.mt.cxml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;

public class ECORequestXML {
	
	String originalContent = null;
	String noPrologContent = null;
	Document documentContent = null;

	public ECORequestXML(String xml_) throws Exception {

		originalContent = xml_;
		
		noPrologContent = removeRequestProlog(xml_);

		DocumentBuilderFactory builderFactory =  DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(noPrologContent.getBytes("UTF-8"));
		documentContent = builder.parse(is);

	}
	
	public Document getDocumentContent() {
		return documentContent;
	}
	
	public String getNoPrologContent() {
		return noPrologContent;
	}
	
	public String getOriginalContent() {
		return originalContent;
	}
	
	private String removeRequestProlog(String request_) {
		int index = request_.indexOf("<!DOCTYPE");
		if(index>-1) {
			request_ = request_.substring(index+3);
			index = request_.indexOf(">");
			return request_.substring(index+1);
		}
		else {
			index = request_.indexOf("?>");
			return request_.substring(index+2);
		}
	}
	
	public void creditCardCheck() throws Exception {
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("string(//cXML/Request/OrderRequest/OrderRequestHeader/Payment/PCard/@number)");
	    String value = expr.evaluate(documentContent).toString();
	    if(value!=null) {
	    	String etext = "Credit card details are not allowed in Order request message.";
	    	if(value.length()>4) {
	    		int astlen = value.length()-4;
	    		String comphval = "";
	    		for(int j=0; j<astlen; j++) comphval += "*";
	    		String hval = value.substring(0, astlen);
	    		if(!hval.equals(comphval))
		    		throw new Exception(etext);
	    	}
	    }
		
	}
	
	
	
}
